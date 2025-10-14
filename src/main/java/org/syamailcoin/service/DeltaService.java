package org.syamailcoin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.syamailcoin.core.Blockrecursive;
import org.syamailcoin.core.Block;
import java.math.BigDecimal;
import java.util.*;

@Service
public class DeltaService {
    
    private Blockrecursive blockrecursive;
    
    @Autowired
    private WalletService walletService;
    
    public DeltaService() {
        this.blockrecursive = new Blockrecursive();
    }
    
    public Map<String, Object> performDelta(String walletAddress) {
        Map<String, Object> response = new HashMap<>();
        
        Block newBlock = blockrecursive.createBlock(walletAddress);
        
        BigDecimal reward = new BigDecimal("0.0002231668235294118");
        walletService.addBalance(walletAddress, reward);
        
        response.put("status", "success");
        response.put("blockHash", newBlock.getHash());
        response.put("blockIndex", newBlock.getIndex());
        response.put("reward", reward);
        response.put("timestamp", newBlock.getTimestamp());
        response.put("message", "Block created via Delta!");
        
        return response;
    }
    
    public Map<String, Object> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        
        status.put("totalBlocks", blockrecursive.getBlocks().size());
        status.put("genesisHash", blockrecursive.getGenesisHash());
        status.put("currentStage", blockrecursive.getCurrentStage());
        status.put("stageRemaining", blockrecursive.getStageRemaining());
        status.put("totalRemaining", blockrecursive.getTotalRemaining());
        status.put("isValid", blockrecursive.isValid());
        
        return status;
    }
}
