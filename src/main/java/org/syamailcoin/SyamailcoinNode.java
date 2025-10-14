package org.syamailcoin;

import org.syamailcoin.core.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.Scanner;

public class SyamailcoinNode {
    private Blockrecursive blockrecursive;
    private Wallet userWallet;
    private int port;
    
    private static final String WALLET_FILE = "data/syamailcoin_wallet.dat";
    private static final String BLOCKS_FILE = "data/blocks.dat";
    
    public SyamailcoinNode(int port) {
        this.port = port;
        loadOrCreateWallet();
        loadOrCreateBlockrecursive();
    }
    
    private void loadOrCreateWallet() {
        File walletFile = new File(WALLET_FILE);
        if (walletFile.exists()) {
            System.out.println("Wallet loaded: " + userWallet.getAddress());
        } else {
            userWallet = new Wallet();
            System.out.println("New wallet created: " + userWallet.getAddress());
        }
    }
    
    private void loadOrCreateBlockrecursive() {
        File blocksFile = new File(BLOCKS_FILE);
        if (blocksFile.exists()) {
            blockrecursive = new Blockrecursive();
        } else {
            blockrecursive = new Blockrecursive();
        }
    }
    
    public void start() {
        System.out.println("============================================================");
        System.out.println("SYAMAILCOIN NODE - Gödel's Untouched Money");
        System.out.println("============================================================");
        
        if (userWallet != null) {
            System.out.println("Wallet loaded: " + userWallet.getAddress());
        }
        
        System.out.println("\nNode Status:");
        System.out.println("- Port: " + port);
        System.out.println("- Wallet: " + (userWallet != null ? userWallet.getAddress() : "None"));
        System.out.println("- Balance: " + (userWallet != null ? userWallet.getBalance() : "0") + " SAC");
        System.out.println("- Genesis Block: " + blockrecursive.getGenesisHash());
        System.out.println("- Current Stage: " + blockrecursive.getCurrentStage());
        System.out.println("- Stage Remaining: " + blockrecursive.getStageRemaining());
        System.out.println("- Total Remaining: " + blockrecursive.getTotalRemaining());
        System.out.println("Node started on port " + port);
        
        System.out.println("\nCommands: status | balance | delta | send | blockrecursive | quit\n");
        
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine().trim();
            
            if (command.equals("quit")) {
                System.out.println("\nShutting down node...");
                break;
            } else if (command.equals("status")) {
                printStatus();
            } else if (command.equals("balance")) {
                System.out.println("Balance: " + userWallet.getBalance() + " SAC");
            } else if (command.equals("delta")) {
                performDelta();
            } else if (command.equals("blockrecursive")) {
                showBlockrecursive();
            }
        }
        scanner.close();
    }
    
    private void performDelta() {
        Block newBlock = blockrecursive.createBlock(userWallet.getAddress());
        System.out.println("Created new block via Delta: " + newBlock.getHash());
        System.out.println("Delta reward received: " + new BigDecimal("0.0002231668235294118") + " SAC");
        System.out.println("Block created via Delta!");
    }
    
    private void printStatus() {
        System.out.println("\nNode Status:");
        System.out.println("- Wallet: " + userWallet.getAddress());
        System.out.println("- Balance: " + userWallet.getBalance() + " SAC");
        System.out.println("- Total Blocks: " + blockrecursive.getBlocks().size());
        System.out.println("- Current Stage: " + blockrecursive.getCurrentStage());
        System.out.println("- Stage Remaining: " + blockrecursive.getStageRemaining());
        System.out.println("- Total Remaining: " + blockrecursive.getTotalRemaining());
    }
    
    private void showBlockrecursive() {
        System.out.println("\nBlockrecursive:");
        for (Block block : blockrecursive.getBlocks()) {
            System.out.println("Block #" + block.getIndex() + ": " + block.getHash());
        }
    }
    
    public static void main(String[] args) {
        SyamailcoinNode node = new SyamailcoinNode(8333);
        node.start();
    }
}
