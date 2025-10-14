package org.syamailcoin.service;

import org.springframework.stereotype.Service;
import org.syamailcoin.core.Wallet;
import java.math.BigDecimal;
import java.util.*;
import java.security.SecureRandom;

@Service
public class WalletService {
    
    private Map<String, Wallet> walletCache = new HashMap<>();
    
    public Map<String, Object> createNewWallet() {
        Wallet wallet = new Wallet();
        walletCache.put(wallet.getAddress(), wallet);
        
        String[] seedPhrase = generateSeedPhrase();
        
        Map<String, Object> response = new HashMap<>();
        response.put("address", wallet.getAddress());
        response.put("balance", wallet.getBalance());
        response.put("seedPhrase", seedPhrase);
        
        return response;
    }
    
    public Map<String, Object> getBalance(String address) {
        Wallet wallet = walletCache.get(address);
        
        Map<String, Object> response = new HashMap<>();
        if (wallet != null) {
            response.put("address", address);
            response.put("balance", wallet.getBalance());
        } else {
            response.put("address", address);
            response.put("balance", BigDecimal.ZERO);
        }
        
        return response;
    }
    
    public Map<String, Object> importWallet(String seedPhrase) {
        Wallet wallet = new Wallet();
        walletCache.put(wallet.getAddress(), wallet);
        
        Map<String, Object> response = new HashMap<>();
        response.put("address", wallet.getAddress());
        response.put("balance", wallet.getBalance());
        response.put("imported", true);
        
        return response;
    }
    
    public boolean deductBalance(String address, BigDecimal amount) {
        Wallet wallet = walletCache.get(address);
        if (wallet != null) {
            return wallet.deductBalance(amount);
        }
        return false;
    }
    
    public void addBalance(String address, BigDecimal amount) {
        Wallet wallet = walletCache.get(address);
        if (wallet != null) {
            wallet.addBalance(amount);
        }
    }
    
    private String[] generateSeedPhrase() {
        String[] words = {
            "syamail", "exponomial", "delta", "accumulation", "proof",
            "recursive", "harmonic", "growth", "decay", "balance",
            "integrity", "byzantine", "factorial"
        };
        
        SecureRandom random = new SecureRandom();
        String[] seedPhrase = new String[12];
        
        for (int i = 0; i < 12; i++) {
            seedPhrase[i] = words[random.nextInt(words.length)];
        }
        
        return seedPhrase;
    }
}
