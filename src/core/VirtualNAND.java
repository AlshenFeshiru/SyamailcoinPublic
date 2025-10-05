package core;

import java.util.BitSet;

public class VirtualNAND {
    
    private static final double MIN_RATIO = 0.4;
    private static final double MAX_RATIO = 0.6;
    
    public static class BalancedData {
        public byte[] data;
        public boolean transformed;
        
        public BalancedData(byte[] data, boolean transformed) {
            this.data = data;
            this.transformed = transformed;
        }
    }
    
    public static BalancedData balance(byte[] data) {
        double ratio = calculateOnesRatio(data);
        
        if (ratio >= MIN_RATIO && ratio <= MAX_RATIO) {
            return new BalancedData(data, false);
        }
        
        byte[] inverted = invert(data);
        double invertedRatio = calculateOnesRatio(inverted);
        
        if (invertedRatio >= MIN_RATIO && invertedRatio <= MAX_RATIO) {
            return new BalancedData(inverted, true);
        }
        
        return null;
    }
    
    public static byte[] unbalance(BalancedData balanced) {
        if (balanced.transformed) {
            return invert(balanced.data);
        }
        return balanced.data;
    }
    
    public static boolean verify(byte[] data) {
        double ratio = calculateOnesRatio(data);
        return ratio >= MIN_RATIO && ratio <= MAX_RATIO;
    }
    
    private static double calculateOnesRatio(byte[] data) {
        int totalBits = data.length * 8;
        int ones = 0;
        
        for (byte b : data) {
            ones += Integer.bitCount(b & 0xFF);
        }
        
        return (double) ones / totalBits;
    }
    
    private static byte[] invert(byte[] data) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) ~data[i];
        }
        return result;
    }
    
    public static String integrityCheck(byte[] original, BalancedData balanced) {
        SAI288 sai = new SAI288();
        byte[] hash1 = sai.hash(balanced.data);
        byte[] hash2 = sai.hash(original);
        
        byte[] xor = new byte[hash1.length];
        for (int i = 0; i < hash1.length; i++) {
            xor[i] = (byte) (hash1[i] ^ hash2[i]);
        }
        
        return SAI288.toHex(xor);
    }
}
