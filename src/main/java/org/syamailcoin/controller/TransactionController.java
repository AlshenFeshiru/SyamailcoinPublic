package org.syamailcoin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.syamailcoin.service.TransactionService;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/transaction")
@CrossOrigin(origins = "*")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody Map<String, String> request) {
        try {
            String from = request.get("from");
            String to = request.get("to");
            BigDecimal amount = new BigDecimal(request.get("amount"));
            
            Map<String, Object> result = transactionService.createTransfer(from, to, amount);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/history/{address}")
    public ResponseEntity<?> getHistory(@PathVariable String address) {
        try {
            Map<String, Object> history = transactionService.getTransactionHistory(address);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
