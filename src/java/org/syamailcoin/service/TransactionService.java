package org.syamailcoin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.syamailcoin.core.Transaction;
import java.math.BigDecimal;
import java.util.*;

@Service
public class TransactionService {
    
    @Autowired
    private WalletService walletService;
    
    private List<Transaction> transactionHistory = new ArrayList<>();
    
    public Map<String, Object> createTransfer(String from, String to, BigDecimal amount) {
        Map<String, Object> response = new HashMap<>();
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            response.put("status", "failed");
            response.put("error", "Invalid amount");
            return response;
        }
        
        boolean deducted = walletService.deductBalance(from, amount);
        if (!deducted) {
            response.put("status", "failed");
            response.put("error", "Insufficient balance");
            return response;
        }
        
        walletService.addBalance(to, amount);
        
        Transaction tx = new Transaction(from, to, amount);
        transactionHistory.add(tx);
        
        response.put("status", "success");
        response.put("txHash", tx.getTxHash());
        response.put("from", from);
        response.put("to", to);
        response.put("amount", amount);
        response.put("timestamp", tx.getTimestamp());
        
        return response;
    }
    
    public Map<String, Object> getTransactionHistory(String address) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> history = new ArrayList<>();
        
        for (Transaction tx : transactionHistory) {
            if (tx.getFrom().equals(address) || tx.getTo().equals(address)) {
                Map<String, Object> txData = new HashMap<>();
                txData.put("txHash", tx.getTxHash());
                txData.put("from", tx.getFrom());
                txData.put("to", tx.getTo());
                txData.put("amount", tx.getAmount());
                txData.put("timestamp", tx.getTimestamp());
                history.add(txData);
            }
        }
        
        response.put("address", address);
        response.put("transactions", history);
        response.put("count", history.size());
        
        return response;
    }
}
