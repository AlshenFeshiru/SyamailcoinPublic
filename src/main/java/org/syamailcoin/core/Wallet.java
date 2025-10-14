package org.syamailcoin.core;

import java.security.SecureRandom;
import java.math.BigDecimal;
import org.bouncycastle.crypto.digests.RIPEMD320Digest;

public class Wallet {
    private String address;
    private byte[] privateKey;
    private BigDecimal balance;
    
    public Wallet() {
        generateKeyPair();
        this.balance = BigDecimal.ZERO;
    }
    
    private void generateKeyPair() {
        SecureRandom random = new SecureRandom();
        this.privateKey = new byte[32];
        random.nextBytes(privateKey);
        
        RIPEMD320Digest digest = new RIPEMD320Digest();
        byte[] hash = new byte[digest.getDigestSize()];
        digest.update(privateKey, 0, privateKey.length);
        digest.doFinal(hash, 0);
        
        StringBuilder sb = new StringBuilder("SAC");
        for (int i = 0; i < 10; i++) {
            sb.append(String.format("%02x", hash[i]));
        }
        this.address = sb.toString();
    }
    
    public static String createGenesisAddress() {
        return "SAC000000001";
    }
    
    public String getAddress() { return address; }
    public BigDecimal getBalance() { return balance; }
    
    public void addBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
    
    public boolean deductBalance(BigDecimal amount) {
        if (balance.compareTo(amount) >= 0) {
            this.balance = this.balance.subtract(amount);
            return true;
        }
        return false;
    }
}
