package network;

import chain.*;
import wallet.Wallet;
import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Node extends WebSocketServer {
    
    private BlockrecursiveChain chain;
    private Wallet wallet;
    private Set<WebSocket> peers;
    private static final Gson gson = new Gson();
    private boolean running;
    
    public Node(int port) {
        super(new InetSocketAddress(port));
        this.chain = new BlockrecursiveChain();
        this.wallet = new Wallet();
        this.peers = ConcurrentHashMap.newKeySet();
        this.running = false;
    }
    
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        peers.add(conn);
        System.out.println("New peer connected: " + conn.getRemoteSocketAddress());
        sendChainInfo(conn);
    }
    
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        peers.remove(conn);
        System.out.println("Peer disconnected: " + conn.getRemoteSocketAddress());
    }
    
    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            Map<String, Object> msg = gson.fromJson(message, HashMap.class);
            String type = (String) msg.get("type");
            
            switch (type) {
                case "NEW_BLOCK":
                    handleNewBlock(msg);
                    break;
                case "REQUEST_BLOCK":
                    handleBlockRequest(conn, msg);
                    break;
                case "CHAIN_INFO":
                    handleChainInfo(msg);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }
    
    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("WebSocket error: " + ex.getMessage());
    }
    
    @Override
    public void onStart() {
        System.out.println("Node started on port " + getPort());
        running = true;
    }
    
    private void sendChainInfo(WebSocket conn) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("type", "CHAIN_INFO");
        msg.put("blockCount", chain.getBlockCount());
        msg.put("latestIndex", chain.getLatestIndex());
        conn.send(gson.toJson(msg));
    }
    
    private void handleNewBlock(Map<String, Object> msg) {
        String blockJson = (String) msg.get("block");
        Block block = Block.fromJson(blockJson);
        
        if (block != null && block.isValid()) {
            if (chain.addBlock(block)) {
                System.out.println("Added new block: " + block.getHash());
                broadcastBlock(block);
            }
        }
    }
    
    private void handleBlockRequest(WebSocket conn, Map<String, Object> msg) {
        String hash = (String) msg.get("hash");
        Block block = chain.getBlock(hash);
        
        if (block != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("type", "BLOCK_RESPONSE");
            response.put("block", block.toJson());
            conn.send(gson.toJson(response));
        }
    }
    
    private void handleChainInfo(Map<String, Object> msg) {
        int remoteCount = ((Double) msg.get("blockCount")).intValue();
        long remoteIndex = ((Double) msg.get("latestIndex")).longValue();
        
        if (remoteIndex > chain.getLatestIndex()) {
            System.out.println("Peer has longer chain, syncing...");
        }
    }
    
    private void broadcastBlock(Block block) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("type", "NEW_BLOCK");
        msg.put("block", block.toJson());
        String json = gson.toJson(msg);
        
        for (WebSocket peer : peers) {
            peer.send(json);
        }
    }
    
    public void mineBlock(List<Transaction> transactions) {
        Block latestBlock = chain.getBlocksByIndex(chain.getLatestIndex()).get(0);
        String prevHash = latestBlock.getHash();
        
        Block newBlock = chain.createBlock(prevHash, transactions, null);
        
        if (newBlock.isValid() && chain.addBlock(newBlock)) {
            System.out.println("Mined new block: " + newBlock.getHash());
            broadcastBlock(newBlock);
        }
    }
    
    public BlockrecursiveChain getChain() {
        return chain;
    }
    
    public Wallet getWallet() {
        return wallet;
    }
    
    public boolean isRunning() {
        return running;
    }
}
