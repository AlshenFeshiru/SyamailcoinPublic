package chain;

import core.AccumulationFunction;
import consensus.ProofOfExponomial;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlockrecursiveChain {
    
    private Map<String, Block> blocks;
    private Map<Long, List<String>> indexToHashes;
    private Block genesisBlock;
    private AccumulationFunction accFunc;
    private ProofOfExponomial poe;
    private BigDecimal remainingSupply;
    private static final BigDecimal TOTAL_SUPPLY = new BigDecimal("9469999.9999999428");
    private static final BigDecimal GENESIS_AMOUNT = new BigDecimal("235294");
    
    public BlockrecursiveChain() {
        this.blocks = new ConcurrentHashMap<>();
        this.indexToHashes = new ConcurrentHashMap<>();
        this.accFunc = new AccumulationFunction();
        this.poe = new ProofOfExponomial();
        this.remainingSupply = TOTAL_SUPPLY.subtract(GENESIS_AMOUNT);
        initializeGenesis();
    }
    
    private void initializeGenesis() {
        List<Transaction> genesisTx = new ArrayList<>();
        Transaction tx = new Transaction("SYSTEM", "GENESIS_WALLET", GENESIS_AMOUNT);
        genesisTx.add(tx);
        
        genesisBlock = new Block(0, "0", genesisTx);
        genesisBlock.setFiValue(accFunc.computeFi(0));
        genesisBlock.setAccumulation(accFunc.computeAn(0));
        genesisBlock.setProofValue(1.0);
        genesisBlock.setCommitment("Gödel's Untouched Money - 5 October 2025");
        genesisBlock.setStorageBalanced(true);
        genesisBlock.calculateHash();
        
        addBlock(genesisBlock);
    }
    
    public boolean addBlock(Block block) {
        if (block == null || !block.isValid()) {
            return false;
        }
        
        String hash = block.getHash();
        if (hash == null) {
            hash = block.calculateHash();
        }
        
        blocks.put(hash, block);
        
        long index = block.getIndex();
        indexToHashes.computeIfAbsent(index, k -> new ArrayList<>()).add(hash);
        
        return true;
    }
    
    public Block createBlock(String previousRef, List<Transaction> transactions, List<String> recursiveRefs) {
        long newIndex = getLatestIndex() + 1;
        Block block = new Block(newIndex, previousRef, transactions);
        
        if (recursiveRefs != null) {
            for (String ref : recursiveRefs) {
                if (blocks.containsKey(ref)) {
                    block.addRecursiveReference(ref);
                }
            }
        }
        
        double[] sj = new double[(int)newIndex + 1];
        for (int i = 0; i <= newIndex && i < sj.length; i++) {
            sj[i] = 1.0 + (i * 0.01);
        }
        accFunc.setSjValues(sj);
        
        BigDecimal fi = accFunc.computeFi((int)newIndex);
        BigDecimal an = accFunc.computeAn((int)newIndex);
        BigDecimal reward = accFunc.computeReward((int)newIndex, remainingSupply);
        
        block.setFiValue(fi);
        block.setAccumulation(an);
        
        double proofValue = poe.computePoE(25, 5, 20, 3);
        block.setProofValue(proofValue);
        
        block.calculateHash();
        
        if (block.isValid()) {
            remainingSupply = remainingSupply.subtract(reward);
        }
        
        return block;
    }
    
    public Block getBlock(String hash) {
        return blocks.get(hash);
    }
    
    public List<Block> getBlocksByIndex(long index) {
        List<String> hashes = indexToHashes.get(index);
        if (hashes == null) return new ArrayList<>();
        List<Block> result = new ArrayList<>();
        for (String hash : hashes) {
            Block b = blocks.get(hash);
            if (b != null) result.add(b);
        }
        return result;
    }
    
    public Block getGenesisBlock() {
        return genesisBlock;
    }
    
    public long getLatestIndex() {
        return indexToHashes.keySet().stream().max(Long::compare).orElse(0L);
    }
    
    public int getBlockCount() {
        return blocks.size();
    }
    
    public BigDecimal getRemainingSupply() {
        return remainingSupply;
    }
    
    public boolean validateChain() {
        for (Block block : blocks.values()) {
            if (!block.isValid()) {
                return false;
            }
            if (block.getIndex() > 0) {
                String prevRef = block.getPreviousReference();
                if (prevRef != null && !blocks.containsKey(prevRef)) {
                    return false;
                }
            }
        }
        return true;
    }
}
