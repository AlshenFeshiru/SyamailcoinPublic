package wallet;

import crypto.MLDSASignature;
import core.SAI288;
import chain.Transaction;
import com.google.gson.Gson;
import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
    
    private String address;
    private MLDSASignature.KeyPair keyPair;
    private BigDecimal balance;
    private MLDSASignature mldsa;
    private SAI288 sai;
    private static final Gson gson = new Gson();
    
    public Wallet() {
        this.mldsa = new MLDSASignature();
        this.sai = new SAI288();
        this.balance = BigDecimal.ZERO;
        generateKeys();
    }
    
    private void generateKeys() {
        this.keyPair = mldsa.generateKeyPair();
        this.address = generateAddress(keyPair.publicKeyEncoded);
    }
    
    private String generateAddress(String pubKeyEncoded) {
        byte[] hash = sai.hash(pubKeyEncoded.getBytes());
        String fullHash = SAI288.toHex(hash);
        return "SAC" + fullHash.substring(0, 40);
    }
    
    public Transaction createTransaction(String recipient, BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            return null;
        }
        Transaction tx = new Transaction(address, recipient, amount);
        String signature = mldsa.sign(tx.calculateTxId(), keyPair);
        tx.setSignature(signature);
        tx.setPublicKey(keyPair.publicKeyEncoded);
        balance = balance.subtract(amount);
        return tx;
    }
    
    public boolean verifyTransaction(Transaction tx) {
        return true;
    }
    
    public void addBalance(BigDecimal amount) {
        balance = balance.add(amount);
    }
    
    public void save(String filename) throws IOException {
        Map<String, String> data = new HashMap<>();
        data.put("address", address);
        data.put("publicKey", keyPair.publicKeyEncoded);
        data.put("privateKey", keyPair.privateKeyEncoded);
        data.put("balance", balance.toString());
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
        }
    }
    
    public static Wallet load(String filename) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Map<String, String> data = (Map<String, String>) ois.readObject();
            Wallet wallet = new Wallet();
            wallet.address = data.get("address");
            wallet.balance = new BigDecimal(data.get("balance"));
            return wallet;
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }
    
    public String getAddress() { return address; }
    public String getPublicKey() { return keyPair.publicKeyEncoded; }
    public BigDecimal getBalance() { return balance; }
}
