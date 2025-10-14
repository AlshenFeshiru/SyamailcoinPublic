package org.syamailcoin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.syamailcoin.service.EnhancedPaymentService;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {
    
    @Autowired
    private EnhancedPaymentService paymentService;
    
    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePayment(@RequestBody Map<String, Object> request) {
        try {
            String method = (String) request.get("method");
            String walletAddress = (String) request.get("walletAddress");
            String amount = (String) request.get("amount");
            
            Map<String, Object> result = paymentService.initiatePayment(method, walletAddress, amount);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/webhook/stripe")
    public ResponseEntity<?> stripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signature) {
        try {
            paymentService.handleStripeWebhook(payload, signature);
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/webhook/coinbase")
    public ResponseEntity<?> coinbaseWebhook(@RequestBody String payload, @RequestHeader("X-CC-Webhook-Signature") String signature) {
        try {
            paymentService.handleCoinbaseWebhook(payload, signature);
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, String> request) {
        try {
            String transactionId = request.get("transactionId");
            Map<String, Object> result = paymentService.confirmPayment(transactionId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/price")
    public ResponseEntity<?> getCurrentPrice() {
        try {
            Map<String, Object> price = paymentService.getCurrentSACPrice();
            return ResponseEntity.ok(price);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
