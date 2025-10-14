package org.syamailcoin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.syamailcoin.service.WalletService;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "*")
public class WalletController {
    
    @Autowired
    private WalletService walletService;
    
    @PostMapping("/create")
    public ResponseEntity<?> createWallet() {
        try {
            Map<String, Object> wallet = walletService.createNewWallet();
            return ResponseEntity.ok(wallet);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/balance/{address}")
    public ResponseEntity<?> getBalance(@PathVariable String address) {
        try {
            Map<String, Object> balance = walletService.getBalance(address);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/import")
    public ResponseEntity<?> importWallet(@RequestBody Map<String, String> request) {
        try {
            String seedPhrase = request.get("seedPhrase");
            Map<String, Object> wallet = walletService.importWallet(seedPhrase);
            return ResponseEntity.ok(wallet);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
