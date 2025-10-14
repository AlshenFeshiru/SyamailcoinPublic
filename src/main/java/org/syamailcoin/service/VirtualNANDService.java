package org.syamailcoin.service;

import org.springframework.stereotype.Service;
import org.syamailcoin.entity.VirtualNAND;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;

@Service
public class VirtualNANDService {
    
    private final SecureRandom random = new SecureRandom();
    private final Map<String, Set<String>> landmarkGroups = new HashMap<>();
    
    public BigDecimal calculateRandomBonus() {
        int bonus = 10 + random.nextInt(90);
        return new BigDecimal(bonus);
    }
    
    public String generateParameterSignature(int parameterLevel) {
        return "PARAM_LEVEL_" + parameterLevel;
    }
    
    public String findOrCreateLandmark(String parameterSignature, String walletAddress) {
        landmarkGroups.putIfAbsent(parameterSignature, new HashSet<>());
        Set<String> members = landmarkGroups.get(parameterSignature);
        members.add(walletAddress);
        
        return "LANDMARK_" + parameterSignature + "_" + members.size();
    }
    
    public boolean validateBalance(byte[] data) {
        int ones = 0;
        int total = data.length * 8;
        
        for (byte b : data) {
            ones += Integer.bitCount(b & 0xFF);
        }
        
        double ratio = (double) ones / total;
        return ratio >= 0.4 && ratio <= 0.6;
    }
    
    public byte[] balanceData(byte[] data) {
        if (validateBalance(data)) {
            return data;
        }
        
        byte[] inverted = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            inverted[i] = (byte) ~data[i];
        }
        
        if (validateBalance(inverted)) {
            return inverted;
        }
        
        return null;
    }
}
