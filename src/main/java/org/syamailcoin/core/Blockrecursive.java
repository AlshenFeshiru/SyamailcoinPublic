package org.syamailcoin.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Blockrecursive {
    private List<Block> blocks;
    private Map<Long, List<Long>> recursiveReferences;
    private Map<String, Wallet> wallets;
    private BigDecimal totalSupply;
    private BigDecimal maxSupply;
    private int currentStage;
    private BigDecimal stageRemaining;
    
    private static final BigDecimal GENESIS_AMOUNT = new BigDecimal("235294");
    private static final BigDecimal MAX_SUPPLY = new BigDecimal("9469999.9999999428");
    private static final BigDecimal REWARD_CONSTANT = new BigDecimal("0.0002231668235294118");
    
    private static final BigDecimal[] STAGE_LIMITS = {
        new BigDecimal("4104313.1758309230208"),
        new BigDecimal("3743507.3299902770668"),
        new BigDecimal("1186437.2000982209574"),
        new BigDecimal("200448.2940805212129")
    };
    
    public Blockrecursive() {
        this.blocks = new ArrayList<>();
        this.recursiveReferences = new HashMap<>();
        this.wallets = new HashMap<>();
        this.totalSupply = BigDecimal.ZERO;
        this.maxSupply = MAX_SUPPLY;
        this.currentStage = 0;
        this.stageRemaining = STAGE_LIMITS[0];
        
        createGenesisBlock();
    }
    
    private void createGenesisBlock() {
        Block genesis = new Block(0, "0");
        genesis.setProof(ProofOfExponomial.getDefaultProof());
        
        String genesisAddress = Wallet.createGenesisAddress();
        Wallet genesisWallet = new Wallet();
        genesisWallet.addBalance(GENESIS_AMOUNT);
        wallets.put(genesisAddress, genesisWallet);
        
        BigDecimal[] S = new BigDecimal[1];
        S[0] = BigDecimal.ONE;
        BigDecimal Fi = ExponomialFunction.calculate(0, S);
        genesis.setExponentialValue(Fi);
        genesis.setAccumulation(Fi.pow(288));
        
        genesis.calculateHash();
        blocks.add(genesis);
        
        totalSupply = totalSupply.add(GENESIS_AMOUNT);
        stageRemaining = STAGE_LIMITS[0];
    }
    
    public Block createBlock(String walletAddress) {
        Block previousBlock = blocks.get(blocks.size() - 1);
        Block newBlock = new Block(blocks.size(), previousBlock.getHash());
        
        BigDecimal[] S = new BigDecimal[blocks.size()];
        for (int i = 0; i < blocks.size(); i++) {
            S[i] = blocks.get(i).getExponentialValue();
        }
        
        BigDecimal Fi = ExponomialFunction.calculate(blocks.size(), S);
        newBlock.setExponentialValue(Fi);
        
        BigDecimal accumulation = ExponomialFunction.calculateAccumulation(blocks.size(), S);
        newBlock.setAccumulation(accumulation);
        
        newBlock.setProof(ProofOfExponomial.getDefaultProof());
        
        BigDecimal reward = REWARD_CONSTANT;
        Wallet wallet = wallets.get(walletAddress);
        if (wallet != null) {
            wallet.addBalance(reward);
        }
        
        totalSupply = totalSupply.add(reward);
        stageRemaining = stageRemaining.subtract(reward);
        
        if (stageRemaining.compareTo(BigDecimal.ZERO) <= 0 && currentStage < 3) {
            currentStage++;
            stageRemaining = STAGE_LIMITS[currentStage];
        }
        
        newBlock.calculateHash();
        blocks.add(newBlock);
        
        return newBlock;
    }
    
    public Block getLatestBlock() {
        return blocks.get(blocks.size() - 1);
    }
    
    public boolean isValid() {
        for (int i = 1; i < blocks.size(); i++) {
            Block current = blocks.get(i);
            Block previous = blocks.get(i - 1);
            
            if (!current.isValid()) return false;
            if (!current.getPreviousHash().equals(previous.getHash())) return false;
        }
        return true;
    }
    
    public String getGenesisHash() {
        return blocks.get(0).getHash();
    }
    
    public int getCurrentStage() { return currentStage; }
    public BigDecimal getStageRemaining() { return stageRemaining; }
    public BigDecimal getTotalRemaining() { return maxSupply.subtract(totalSupply); }
    public List<Block> getBlocks() { return blocks; }
    public Map<String, Wallet> getWallets() { return wallets; }
}
