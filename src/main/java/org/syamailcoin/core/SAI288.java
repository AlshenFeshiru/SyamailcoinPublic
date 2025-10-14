package org.syamailcoin.core;

import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.math.MathContext;

public class SAI288 {
    private static final long[] IV = {
        0x243F6A88L, 0x85A308D3L, 0x13198A2EL, 0x03707344L,
        0xA4093822L, 0x299F31D0L, 0x082EFA98L, 0xEC4E6C89L, 0x452821E6L
    };
    
    private static final double GAMMA = 1.05;
    private static final double R = 10.0;
    private static final double TAU = 0.5;
    private static final double PHI = 0.9;
    
    private long[] state;
    
    public SAI288() {
        this.state = new long[9];
        System.arraycopy(IV, 0, this.state, 0, 9);
    }
    
    private long rotateLeft(long value, int shift) {
        shift = shift & 31;
        return ((value << shift) | (value >>> (32 - shift))) & 0xFFFFFFFFL;
    }
    
    private long rotateRight(long value, int shift) {
        shift = shift & 31;
        return ((value >>> shift) | (value << (32 - shift))) & 0xFFFFFFFFL;
    }
    
    private double calculateF(int t) {
        double expGrowth = Math.pow(GAMMA, (double)t / R);
        double weightedSum = 0.0;
        int limit = Math.min(t, 8);
        for (int j = 0; j <= limit; j++) {
            weightedSum += state[j] * Math.pow(PHI, j);
        }
        return expGrowth * TAU * weightedSum;
    }
    
    public String hash(byte[] input) {
        System.arraycopy(IV, 0, this.state, 0, 9);
        
        int blockSize = 72;
        int numBlocks = (input.length + blockSize - 1) / blockSize;
        
        for (int block = 0; block < numBlocks; block++) {
            int offset = block * blockSize;
            int length = Math.min(blockSize, input.length - offset);
            byte[] blockData = new byte[blockSize];
            System.arraycopy(input, offset, blockData, 0, length);
            
            long[] M = new long[18];
            for (int i = 0; i < 18; i++) {
                int idx = i * 4;
                if (idx + 3 < blockSize) {
                    M[i] = ((blockData[idx] & 0xFFL) << 24) |
                           ((blockData[idx + 1] & 0xFFL) << 16) |
                           ((blockData[idx + 2] & 0xFFL) << 8) |
                           (blockData[idx + 3] & 0xFFL);
                }
            }
            
            for (int t = 0; t < 64; t++) {
                double fValue = calculateF(t);
                long fLong = ((long)fValue) & 0xFFFFFFFFL;
                
                long f1 = (state[(t + 1) % 9] ^ M[t % 18]) + fLong;
                f1 ^= rotateLeft(state[(t + 4) % 9], (int)(PHI * t) % 32);
                f1 &= 0xFFFFFFFFL;
                
                long f2 = (state[(t + 5) % 9] + M[(int)(t * PHI) % 18]) & 0xFFFFFFFFL;
                f2 ^= rotateRight(state[(t + 7) % 9], t % 29);
                f2 &= 0xFFFFFFFFL;
                
                state[t % 9] = (f1 + f2 + state[t % 9]) & 0xFFFFFFFFL;
            }
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            result.append(String.format("%08x", state[i]));
        }
        return result.substring(0, 72);
    }
    
    public static String hash(String input) {
        SAI288 hasher = new SAI288();
        return hasher.hash(input.getBytes(StandardCharsets.UTF_8));
    }
}
