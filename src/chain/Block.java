package chain;

import core.SAI288;
import core.AccumulationFunction;
import com.google.gson.Gson;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Block {
    
    private long index;
    private String hash;
    private String previousReference;
    private List<String> recursiveReferences;
    private List<Transaction> transactions;
    private long timestamp;
    private BigDecimal fiValue;
    private double proofValue;
    private BigDecimal accumulation;
    private String commitment;
    private String signature;
    private boolean storageBalanced;
    private int version;
    
    private static final Gson gson = new Gson();
    private static final SAI288 sai = new SAI288();
    
    public Block(long index, String previousReference, List<Transaction> transactions) {
        this(index, previousReference, transactions, System.currentTimeMillis());
    }
    
    public Block(long index, String previousReference, List<Transaction> transactions, long timestamp) {
        this.index = index;
        this.previousReference = previousReference;
        this.transactions = transactions != null ? transactions : new ArrayList<>();
        this.recursiveReferences = new ArrayList<>();
        this.timestamp = timestamp;
        this.version = 1;
        this.storageBalanced = true;
    }
    
    public void addRecursiveReference(String blockHash) {
        if (!recursiveReferences.contains(blockHash)) {
            recursiveReferences.add(blockHash);
        }
    }
    
    public void setFiValue(BigDecimal fiValue) {
        this.fiValue = fiValue;
    }
    
    public void setProofValue(double proofValue) {
        this.proofValue = proofValue;
    }
    
    public void setAccumulation(BigDecimal accumulation) {
        this.accumulation = accumulation;
    }
    
    public void setCommitment(String commitment) {
        this.commitment = commitment;
    }
    
    public void setSignature(String signature) {
        this.signature = signature;
    }
    
    public void setStorageBalanced(boolean balanced) {
        this.storageBalanced = balanced;
    }
    
    public String calculateHash() {
        String data = index + previousReference + 
                     gson.toJson(recursiveReferences) +
                     gson.toJson(transactions) + timestamp +
                     (fiValue != null ? fiValue.toString() : "") +
                     proofValue + 
                     (accumulation != null ? accumulation.toString() : "") +
                     (commitment != null ? commitment : "");
        byte[] hashBytes = sai.hash(data.getBytes());
        this.hash = SAI288.toHex(hashBytes);
        return this.hash;
    }
    
    public boolean isValid() {
        if (hash == null || !hash.equals(calculateHash())) {
            return false;
        }
        if (proofValue < 0.1447) {
            return false;
        }
        if (!storageBalanced) {
            return false;
        }
        if (transactions != null) {
            for (Transaction tx : transactions) {
                if (!tx.isValid()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public String toJson() {
        return gson.toJson(this);
    }
    
    public static Block fromJson(String json) {
        return gson.fromJson(json, Block.class);
    }
    
    public long getIndex() { return index; }
    public String getHash() { return hash; }
    public String getPreviousReference() { return previousReference; }
    public List<String> getRecursiveReferences() { return recursiveReferences; }
    public List<Transaction> getTransactions() { return transactions; }
    public long getTimestamp() { return timestamp; }
    public BigDecimal getFiValue() { return fiValue; }
    public double getProofValue() { return proofValue; }
    public BigDecimal getAccumulation() { return accumulation; }
    public String getCommitment() { return commitment; }
    public String getSignature() { return signature; }
    public boolean isStorageBalanced() { return storageBalanced; }
    public int getVersion() { return version; }
}
