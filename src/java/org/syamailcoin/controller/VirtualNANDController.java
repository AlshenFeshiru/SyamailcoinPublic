package org.syamailcoin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.syamailcoin.service.VirtualNANDService;
import org.syamailcoin.service.PaymentService;
import java.util.Map;

@RestController
@RequestMapping("/api/virtualnand")
@CrossOrigin(origins = "*")
public class VirtualNANDController {
    
    @Autowired
    private VirtualNANDService virtualNANDService;
    
    @Autowired
    private PaymentService paymentService;
    
    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseVirtualNAND(@RequestBody Map<String, Object> request) {
        try {
            String walletAddress = (String) request.get("walletAddress");
            Integer parameterLevel = (Integer) request.get("parameterLevel");
            
            Map<String, Object> result = paymentService.initiatePurchase(walletAddress, parameterLevel);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/setup")
    public ResponseEntity<?> setupVirtualNAND(@RequestBody Map<String, Object> request) {
        try {
            String walletAddress = (String) request.get("walletAddress");
            Integer parameterLevel = (Integer) request.get("parameterLevel");
            
            Map<String, Object> result = paymentService.completeSetup(walletAddress, parameterLevel);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/status/{address}")
    public ResponseEntity<?> getStatus(@PathVariable String address) {
        try {
            Map<String, Object> status = paymentService.getVirtualNANDStatus(address);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
