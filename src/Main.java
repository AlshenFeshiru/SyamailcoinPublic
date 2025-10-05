import network.Node;
import chain.*;
import wallet.Wallet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    
    private static Node node;
    private static Wallet wallet;
    private static final String CONFIG_FILE = "config.json";
    private static final String WALLET_FILE = "data/syamailcoin_wallet.dat";
    private static final Gson gson = new Gson();
    
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("SYAMAILCOIN NODE - Gödel's Untouched Money");
        System.out.println("=".repeat(60));
        
        JsonObject config = loadConfig();
        int port = config.has("port") ? config.get("port").getAsInt() : 8333;
        
        try {
            wallet = loadWallet();
            System.out.println("Wallet loaded: " + wallet.getAddress());
        } catch (Exception e) {
            wallet = new Wallet();
            System.out.println("New wallet created: " + wallet.getAddress());
            try {
                wallet.save(WALLET_FILE);
            } catch (IOException ex) {
                System.err.println("Failed to save wallet: " + ex.getMessage());
            }
        }
        
        node = new Node(port);
        node.start();
        
        System.out.println("\nNode Status:");
        System.out.println("- Port: " + port);
        System.out.println("- Wallet: " + wallet.getAddress());
        System.out.println("- Balance: " + wallet.getBalance());
        System.out.println("- Genesis Block: " + node.getChain().getGenesisBlock().getHash());
        System.out.println("- Remaining Supply: " + node.getChain().getRemainingSupply());
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("\nShutting down node...");
                wallet.save(WALLET_FILE);
                node.stop(1000);
            } catch (Exception e) {
                System.err.println("Error during shutdown: " + e.getMessage());
            }
        }));
        
        if (System.console() != null) {
            startCommandLoop();
        } else {
            System.out.println("\nRunning in daemon mode (no interactive console)");
            System.out.println("Node will continue running until stopped");
            while (true) {
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
    
    private static JsonObject loadConfig() {
        try {
            String json = new String(Files.readAllBytes(Paths.get(CONFIG_FILE)));
            return gson.fromJson(json, JsonObject.class);
        } catch (Exception e) {
            JsonObject defaultConfig = new JsonObject();
            defaultConfig.addProperty("port", 8333);
            defaultConfig.addProperty("maxPeers", 10);
            try {
                Files.write(Paths.get(CONFIG_FILE), gson.toJson(defaultConfig).getBytes());
            } catch (IOException ex) {
                System.err.println("Failed to create config: " + ex.getMessage());
            }
            return defaultConfig;
        }
    }
    
    private static Wallet loadWallet() throws IOException {
        return Wallet.load(WALLET_FILE);
    }
    
    private static void startCommandLoop() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nCommands: status | balance | mine | send | blockrecursive | quit");
        
        while (true) {
            System.out.print("\n> ");
            String command = scanner.nextLine().trim().toLowerCase();
            
            switch (command) {
                case "status":
                    showStatus();
                    break;
                case "balance":
                    System.out.println("Balance: " + wallet.getBalance() + " SAC");
                    break;
                case "mine":
                    mineBlock();
                    break;
                case "send":
                    sendTransaction(scanner);
                    break;
                case "blockrecursive":
                    showBlockrecursiveInfo();
                    break;
                case "quit":
                case "exit":
                    System.exit(0);
                    break;
                default:
                    System.out.println("Unknown command");
            }
        }
    }
    
    private static void showStatus() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Node Status:");
        System.out.println("- Running: " + node.isRunning());
        System.out.println("- Address: " + wallet.getAddress());
        System.out.println("- Balance: " + wallet.getBalance() + " SAC");
        System.out.println("- Blocks: " + node.getChain().getBlockCount());
        System.out.println("- Latest Index: " + node.getChain().getLatestIndex());
        System.out.println("- Remaining Supply: " + node.getChain().getRemainingSupply());
        System.out.println("=".repeat(60));
    }
    
    private static void mineBlock() {
        List<Transaction> txs = new ArrayList<>();
        node.mineBlock(txs);
        System.out.println("Block mined!");
    }
    
    private static void sendTransaction(Scanner scanner) {
        System.out.print("Recipient address: ");
        String recipient = scanner.nextLine().trim();
        System.out.print("Amount: ");
        String amountStr = scanner.nextLine().trim();
        
        try {
            BigDecimal amount = new BigDecimal(amountStr);
            Transaction tx = wallet.createTransaction(recipient, amount);
            if (tx != null) {
                System.out.println("Transaction created: " + tx.getTxId());
            } else {
                System.out.println("Insufficient balance");
            }
        } catch (Exception e) {
            System.out.println("Invalid amount");
        }
    }
    
    private static void showBlockrecursiveInfo() {
        BlockrecursiveChain chain = node.getChain();
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Blockrecursive Chain Info:");
        System.out.println("- Total Blocks: " + chain.getBlockCount());
        System.out.println("- Latest Index: " + chain.getLatestIndex());
        System.out.println("- Genesis: " + chain.getGenesisBlock().getHash());
        System.out.println("- Valid: " + chain.validateChain());
        System.out.println("=".repeat(60));
    }
}
