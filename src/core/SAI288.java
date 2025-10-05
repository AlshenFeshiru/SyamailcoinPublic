package core;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SAI288 {
    
    private static final int[] IV = {
        0x243F6A88, 0x85A308D3, 0x13198A2E, 0x03707344, 0xA4093822,
        0x299F31D0, 0x082EFA98, 0xEC4E6C89, 0x452821E6
    };
    
    private static final double GAMMA = 1.05;
    private static final double R = 10.0;
    private static final double TAU = 0.5;
    private static final double PHI = 0.9;
    
    private int[] state;
    private int[] M;
    
    public SAI288() {
        this.state = new int[9];
        this.M = new int[18];
        reset();
    }
    
    public void reset() {
        System.arraycopy(IV, 0, state, 0, 9);
    }
    
    private double computeFi(int i) {
        double growth = Math.pow(GAMMA, (double)i / R);
        double sum = 0.0;
        int limit = Math.min(i, 8);
        for (int j = 0; j <= limit; j++) {
            double Sj = Integer.toUnsignedLong(state[j]);
            sum += Sj * Math.pow(PHI, j);
        }
        return growth * TAU * sum;
    }
    
    private static int rotl(int x, int n) {
        return (x << n) | (x >>> (32 - n));
    }
    
    private static int rotr(int x, int n) {
        return (x >>> n) | (x << (32 - n));
    }
    
    private void processBlock(byte[] block) {
        ByteBuffer bb = ByteBuffer.wrap(block).order(ByteOrder.BIG_ENDIAN);
        for (int i = 0; i < 18; i++) {
            M[i] = bb.getInt();
        }
        
        for (int t = 0; t < 64; t++) {
            double Fi = computeFi(t);
            int FiInt = (int)(Fi % 0x100000000L);
            
            int s1 = state[(t + 1) % 9];
            int m1 = M[t % 18];
            int s4 = state[(t + 4) % 9];
            int rotAmount1 = (int)(PHI * t) % 32;
            
            int f1 = (s1 ^ m1) + FiInt;
            f1 ^= rotl(s4, rotAmount1);
            
            int s5 = state[(t + 5) % 9];
            int mPhi = M[(int)(t * PHI) % 18];
            int s7 = state[(t + 7) % 9];
            int rotAmount2 = t % 29;
            
            int f2 = s5 + mPhi;
            f2 ^= rotr(s7, rotAmount2);
            
            int stateIndex = t % 9;
            state[stateIndex] = f1 + f2 + state[stateIndex];
        }
    }
    
    public byte[] hash(byte[] data) {
        reset();
        int blockCount = (data.length + 71) / 72;
        byte[] padded = new byte[blockCount * 72];
        System.arraycopy(data, 0, padded, 0, data.length);
        
        if (data.length < padded.length - 8) {
            ByteBuffer bb = ByteBuffer.wrap(padded, padded.length - 8, 8);
            bb.order(ByteOrder.BIG_ENDIAN);
            bb.putLong(data.length * 8);
        }
        
        byte[] block = new byte[72];
        for (int i = 0; i < blockCount; i++) {
            System.arraycopy(padded, i * 72, block, 0, 72);
            processBlock(block);
        }
        
        byte[] output = new byte[36];
        ByteBuffer bb = ByteBuffer.wrap(output).order(ByteOrder.BIG_ENDIAN);
        for (int i = 0; i < 9; i++) {
            bb.putInt(state[i]);
        }
        return output;
    }
    
    public static String toHex(byte[] hash) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b & 0xFF));
        }
        return sb.toString();
    }
}
