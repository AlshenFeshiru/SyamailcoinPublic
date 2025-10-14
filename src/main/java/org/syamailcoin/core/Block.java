package org.syamailcoin.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Block {
    private long index;
    private String hash;
    private String previousHash;
    private List<Transaction> transactions;
    private long timestamp;
    private BigDecimal exponentialValue;
    private double proof;
    private BigDecimal accumulation;
    private String commitment;
    private String signature;
    private boolean storageBalanced;
    private int version;
    
    public Block(long index, String previousHash) {
        this.index = index;
        this.previousHash = previousHash;
        this.transactions = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
        this.version = 1;
        this.storageBalanced = true;
    }
    
    public String calculateHash() {
        StringBuilder data = new StringBuilder();
        data.append(index);
        data.append(previousHash);
        data.append(timestamp);
        data.append(exponentialValue);
        data.append(proof);
        data.append(accumulation);
        for (Transaction tx : transactions) {
            data.append(tx.toString());
        }
        this.hash = SAI288.hash(data.toString());
        return this.hash;
    }
    
    public boolean isValid() {
        if (!hash.equals(calculateHash())) return false;
        if (proof < ProofOfExponomial.THRESHOLD) return false;
        if (!storageBalanced) return false;
        for (Transaction tx : transactions) {
            if (!tx.isValid()) return false;
        }
        return true;
    }
    
    public boolean isGenesis() {
        return index == 0;
    }
    
    public long getIndex() { return index; }
    public String getHash() { return hash; }
    public String getPreviousHash() { return previousHash; }
    public List<Transaction> getTransactions() { return transactions; }
    public long getTimestamp() { return timestamp; }
    public BigDecimal getExponentialValue() { return exponentialValue; }
    public double getProof() { return proof; }
    public BigDecimal getAccumulation() { return accumulation; }
    
    public void setExponentialValue(BigDecimal value) { this.exponentialValue = value; }
    public void setProof(double proof) { this.proof = proof; }
    public void setAccumulation(BigDecimal accumulation) { this.accumulation = accumulation; }
    public void setCommitment(String commitment) { this.commitment = commitment; }
    public void setSignature(String signature) { this.signature = signature; }
    public void addTransaction(Transaction tx) { this.transactions.add(tx); }
}
