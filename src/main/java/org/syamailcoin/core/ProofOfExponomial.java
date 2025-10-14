package org.syamailcoin.core;

import java.math.BigDecimal;

public class ProofOfExponomial {
    public static final double THRESHOLD = 0.1447;
    
    public static long binomial(int n, int r) {
        if (r > n) return 0;
        if (r == 0 || r == n) return 1;
        
        long result = 1;
        r = Math.min(r, n - r);
        for (int i = 0; i < r; i++) {
            result = result * (n - i) / (i + 1);
        }
        return result;
    }
    
    public static long calculatePoE(int n, int r, int deltaN, int deltaR) {
        long binom1 = binomial(n, r);
        long binom2 = binomial(deltaN, deltaR);
        return Math.abs(binom1 - binom2);
    }
    
    public static boolean verifyProof(double proof) {
        return proof >= THRESHOLD;
    }
    
    public static double getDefaultProof() {
        return calculatePoE(25, 5, 20, 3);
    }
}
