package chain;

import core.SAI288;
import com.google.gson.Gson;
import java.math.BigDecimal;

public class Transaction {
    
    private String txId;
    private String sender;
    private String recipient;
    private BigDecimal amount;
    private long timestamp;
    private String signature;
    private String publicKey;
    
    private static final Gson gson = new Gson();
    private static final SAI288 sai = new SAI288();
    
    public Transaction(String sender, String recipient, BigDecimal amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timestamp = System.currentTimeMillis();
        this.txId = calculateTxId();
    }
    
    public Transaction(String sender, String recipient, BigDecimal amount, long timestamp) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timestamp = timestamp;
        this.txId = calculateTxId();
    }
    
    public String calculateTxId() {
        String data = sender + recipient + amount.toString() + timestamp;
        byte[] hashBytes = sai.hash(data.getBytes());
        return SAI288.toHex(hashBytes);
    }
    
    public void setSignature(String signature) {
        this.signature = signature;
    }
    
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
    
    public boolean isValid() {
        if (txId == null || !txId.equals(calculateTxId())) {
            return false;
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (sender == null || recipient == null) {
            return false;
        }
        return true;
    }
    
    public String toJson() {
        return gson.toJson(this);
    }
    
    public static Transaction fromJson(String json) {
        return gson.fromJson(json, Transaction.class);
    }
    
    public String getTxId() { return txId; }
    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public BigDecimal getAmount() { return amount; }
    public long getTimestamp() { return timestamp; }
    public String getSignature() { return signature; }
    public String getPublicKey() { return publicKey; }
}
