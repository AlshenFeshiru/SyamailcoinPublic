package org.syamailcoin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.syamailcoin.entity.VirtualNAND;
import java.math.BigDecimal;
import java.util.*;

@Service
public class PaymentService {
    
    @Autowired
    private VirtualNANDService virtualNANDService;
    
    @Autowired
    private WalletService walletService;
    
    private static final BigDecimal VIRTUALNAND_PRICE_SAC = new BigDecimal("21000");
    
    public Map<String, Object> initiatePurchase(String walletAddress, Integer parameterLevel) {
        Map<String, Object> response = new HashMap<>();
        
        response.put("status", "initiated");
        response.put("price", VIRTUALNAND_PRICE_SAC);
        response.put("currency", "SAC");
        response.put("walletAddress", walletAddress);
        response.put("parameterLevel", parameterLevel);
        response.put("message", "Please confirm payment of " + VIRTUALNAND_PRICE_SAC + " SAC");
        
        return response;
    }
    
    public Map<String, Object> completeSetup(String walletAddress, Integer parameterLevel) {
        Map<String, Object> response = new HashMap<>();
        
        boolean paymentSuccess = walletService.deductBalance(walletAddress, VIRTUALNAND_PRICE_SAC);
        
        if (!paymentSuccess) {
            response.put("status", "failed");
            response.put("error", "Insufficient balance");
            return response;
        }
        
        BigDecimal bonus = virtualNANDService.calculateRandomBonus();
        walletService.addBalance(walletAddress, bonus);
        
        String parameterSignature = virtualNANDService.generateParameterSignature(parameterLevel);
        String landmarkId = virtualNANDService.findOrCreateLandmark(parameterSignature, walletAddress);
        
        response.put("status", "success");
        response.put("bonusAmount", bonus);
        response.put("landmarkId", landmarkId);
        response.put("parameterLevel", parameterLevel);
        response.put("message", "VirtualNAND K9K1208UOC activated successfully!");
        
        return response;
    }
    
    public Map<String, Object> getVirtualNANDStatus(String address) {
        Map<String, Object> status = new HashMap<>();
        status.put("address", address);
        status.put("hasVirtualNAND", false);
        return status;
    }
}
