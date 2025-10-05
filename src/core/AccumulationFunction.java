package core;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class AccumulationFunction {
    
    private static final double GAMMA = 1.05;
    private static final double R = 10.0;
    private static final double TAU = 0.5;
    private static final double PHI = 0.9;
    private static final MathContext MC = new MathContext(50, RoundingMode.HALF_UP);
    private static final int CACHE_SIZE = 10000;
    
    private BigDecimal[] fiCache;
    private double[] SjValues;
    
    public AccumulationFunction() {
        this.fiCache = new BigDecimal[CACHE_SIZE];
        this.SjValues = new double[CACHE_SIZE];
    }
    
    public void setSjValues(double[] values) {
        int len = Math.min(values.length, SjValues.length);
        System.arraycopy(values, 0, SjValues, 0, len);
        fiCache = new BigDecimal[CACHE_SIZE];
    }
    
    public BigDecimal computeFi(int i) {
        if (i < CACHE_SIZE && fiCache[i] != null) {
            return fiCache[i];
        }
        double exponent = (double)i / R;
        double growth = Math.pow(GAMMA, exponent);
        BigDecimal sum = BigDecimal.ZERO;
        for (int j = 0; j <= i && j < SjValues.length; j++) {
            double Sj = SjValues[j];
            double phiJ = Math.pow(PHI, j);
            BigDecimal term = BigDecimal.valueOf(Sj * phiJ);
            sum = sum.add(term, MC);
        }
        BigDecimal Fi = BigDecimal.valueOf(growth * TAU).multiply(sum, MC);
        if (i < CACHE_SIZE) {
            fiCache[i] = Fi;
        }
        return Fi;
    }
    
    public BigDecimal computeAn(int n) {
        BigDecimal An = BigDecimal.ZERO;
        for (int i = 0; i <= n; i++) {
            BigDecimal Fi = computeFi(i);
            BigDecimal Fi288 = Fi.pow(288, MC);
            An = An.add(Fi288, MC);
        }
        return An;
    }
    
    public BigDecimal computeDerivative(int i) {
        BigDecimal Fi = computeFi(i);
        BigDecimal FiPlus1 = computeFi(i + 1);
        BigDecimal dFdi = FiPlus1.subtract(Fi, MC);
        BigDecimal Fi287 = Fi.pow(287, MC);
        BigDecimal result = BigDecimal.valueOf(288).multiply(Fi287, MC).multiply(dFdi, MC);
        return result;
    }
    
    public BigDecimal computeReward(int i, BigDecimal remaining) {
        BigDecimal MIN_REWARD = new BigDecimal("0.0002231668235294118");
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            return MIN_REWARD;
        }
        BigDecimal Fi = computeFi(i);
        BigDecimal Fi288 = Fi.pow(288, MC);
        BigDecimal Ai = computeAn(i);
        if (Ai.compareTo(BigDecimal.ZERO) == 0) {
            return MIN_REWARD;
        }
        BigDecimal reward = Fi288.divide(Ai, MC).multiply(remaining, MC);
        return reward.max(MIN_REWARD);
    }
    
    public double computeStageDuration(int k, int kPlus1) {
        BigDecimal Ak = computeAn(k);
        BigDecimal AkPlus1 = computeAn(kPlus1);
        if (Ak.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        double ratio = AkPlus1.divide(Ak, MC).doubleValue();
        double lnRatio = Math.log(ratio);
        double duration = (R / GAMMA) * lnRatio;
        return duration;
    }
}
