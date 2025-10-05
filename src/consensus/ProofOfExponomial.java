package consensus;

import java.math.BigInteger;

public class ProofOfExponomial {
    
    private static final double THRESHOLD = 0.1447;
    private static final double BYZANTINE_TOLERANCE = 0.1447;
    private static final double ATTACK_THRESHOLD = 0.8553;
    
    public double computePoE(int n, int r, int deltaN, int deltaR) {
        BigInteger C1 = binomialCoefficient(n, r);
        BigInteger C2 = binomialCoefficient(deltaN, deltaR);
        BigInteger diff = C1.subtract(C2).abs();
        return diff.doubleValue();
    }
    
    public boolean validateProof(double proof) {
        return proof >= THRESHOLD;
    }
    
    public double getByzantineTolerance() {
        return BYZANTINE_TOLERANCE;
    }
    
    public double getAttackThreshold() {
        return ATTACK_THRESHOLD;
    }
    
    private BigInteger binomialCoefficient(int n, int k) {
        if (k > n) return BigInteger.ZERO;
        if (k == 0 || k == n) return BigInteger.ONE;
        if (k > n - k) k = n - k;
        
        BigInteger result = BigInteger.ONE;
        for (int i = 0; i < k; i++) {
            result = result.multiply(BigInteger.valueOf(n - i));
            result = result.divide(BigInteger.valueOf(i + 1));
        }
        return result;
    }
    
    public long factorial(int n) {
        if (n <= 1) return 1;
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
