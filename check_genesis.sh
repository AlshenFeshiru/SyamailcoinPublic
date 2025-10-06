#!/bin/bash
cd /opt/SyamailcoinPublic
java -cp "bin:lib/*" -c "
import core.SAI288;
public class CheckGenesis {
    public static void main(String[] args) {
        SAI288 sai = new SAI288();
        byte[] hash = sai.hash(\"SYAMAILCOIN_GENESIS\".getBytes());
        String fullHash = SAI288.toHex(hash);
        System.out.println(\"Genesis Address: SAC\" + fullHash.substring(0, 40));
    }
}
"
