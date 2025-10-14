package org.syamailcoin.core;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class ExponomialFunction {
    private static final BigDecimal GAMMA = new BigDecimal("1.05");
    private static final BigDecimal R = new BigDecimal("10.0");
    private static final BigDecimal TAU = new BigDecimal("0.5");
    private static final BigDecimal PHI = new BigDecimal("0.9");
    private static final MathContext MC = new MathContext(50, RoundingMode.HALF_UP);
    
    public static BigDecimal calculate(int i, BigDecimal[] S) {
        double exponent = (double)i / R.doubleValue();
        double gammaValue = Math.pow(GAMMA.doubleValue(), exponent);
        BigDecimal gammaPow = new BigDecimal(gammaValue, MC);
        
        BigDecimal weightedSum = BigDecimal.ZERO;
        for (int j = 0; j <= i && j < S.length; j++) {
            double phiPow = Math.pow(PHI.doubleValue(), j);
            BigDecimal phiDecimal = new BigDecimal(phiPow, MC);
            weightedSum = weightedSum.add(S[j].multiply(phiDecimal, MC), MC);
        }
        
        return gammaPow.multiply(TAU, MC).multiply(weightedSum, MC);
    }
    
    public static BigDecimal calculateAccumulation(int n, BigDecimal[] S) {
        BigDecimal accumulation = BigDecimal.ZERO;
        for (int i = 0; i <= n; i++) {
            BigDecimal Fi = calculate(i, S);
            BigDecimal Fi288 = Fi.pow(288, MC);
            accumulation = accumulation.add(Fi288, MC);
        }
        return accumulation;
    }
}
