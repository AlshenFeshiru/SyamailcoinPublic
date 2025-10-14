package org.syamailcoin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.syamailcoin.service.DeltaService;
import java.util.Map;

@RestController
@RequestMapping("/api/delta")
@CrossOrigin(origins = "*")
public class DeltaController {
    
    @Autowired
    private DeltaService deltaService;
    
    @PostMapping("/perform")
    public ResponseEntity<?> performDelta(@RequestBody Map<String, String> request) {
        try {
            String walletAddress = request.get("walletAddress");
            Map<String, Object> result = deltaService.performDelta(walletAddress);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        try {
            Map<String, Object> status = deltaService.getSystemStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
