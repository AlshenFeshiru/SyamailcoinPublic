package org.syamailcoin.core;

import java.math.BigDecimal;

public class Transaction {
    private String from;
    private String to;
    private BigDecimal amount;
    private long timestamp;
    private String signature;
    private String txHash;
    
    public Transaction(String from, String to, BigDecimal amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.timestamp = System.currentTimeMillis();
        this.txHash = calculateHash();
    }
    
    public String calculateHash() {
        String data = from + to + amount.toPlainString() + timestamp;
        return SAI288.hash(data);
    }
    
    public boolean isValid() {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) return false;
        if (from == null || to == null) return false;
        if (!txHash.equals(calculateHash())) return false;
        return true;
    }
    
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public BigDecimal getAmount() { return amount; }
    public long getTimestamp() { return timestamp; }
    public String getTxHash() { return txHash; }
    
    @Override
    public String toString() {
        return from + to + amount.toPlainString() + timestamp;
    }
}
