package org.syamailcoin.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "virtual_nand")
public class VirtualNAND {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "owner_address", nullable = false)
    private String ownerAddress;
    
    @Column(name = "parameter_level")
    private Integer parameterLevel;
    
    @Column(name = "storage_capacity")
    private Long storageCapacity;
    
    @Column(name = "bandwidth_requirement")
    private String bandwidthRequirement;
    
    @Column(name = "gpu_usage", precision = 5, scale = 2)
    private BigDecimal gpuUsage;
    
    @Column(name = "battery_drain", precision = 5, scale = 2)
    private BigDecimal batteryDrain;
    
    @Column(name = "bonus_amount", precision = 40, scale = 20)
    private BigDecimal bonusAmount;
    
    @Column(name = "purchase_price", precision = 40, scale = 20)
    private BigDecimal purchasePrice;
    
    @Column(name = "payment_status")
    private String paymentStatus;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "activated_at")
    private LocalDateTime activatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public Long getId() { return id; }
    public String getOwnerAddress() { return ownerAddress; }
    public void setOwnerAddress(String ownerAddress) { this.ownerAddress = ownerAddress; }
    
    public Integer getParameterLevel() { return parameterLevel; }
    public void setParameterLevel(Integer parameterLevel) { 
        this.parameterLevel = parameterLevel;
        configureByLevel();
    }
    
    private void configureByLevel() {
        switch (parameterLevel) {
            case 1:
                this.gpuUsage = new BigDecimal("0");
                this.batteryDrain = new BigDecimal("0");
                this.storageCapacity = 0L;
                this.bandwidthRequirement = "none";
                break;
            case 2:
                this.gpuUsage = new BigDecimal("0");
                this.batteryDrain = new BigDecimal("0");
                this.storageCapacity = 0L;
                this.bandwidthRequirement = "none";
                break;
            case 3:
                this.gpuUsage = new BigDecimal("0");
                this.batteryDrain = new BigDecimal("3");
                this.storageCapacity = 0L;
                this.bandwidthRequirement = "none";
                break;
            case 4:
            case 5:
                this.gpuUsage = new BigDecimal("0");
                this.batteryDrain = new BigDecimal("0");
                this.storageCapacity = 128L * 1024 * 1024 * 1024;
                this.bandwidthRequirement = "none";
                break;
            case 6:
            case 7:
            case 8:
            case 9:
                this.gpuUsage = new BigDecimal("0");
                this.batteryDrain = new BigDecimal("0");
                this.storageCapacity = 1024L * 1024 * 1024 * 1024;
                this.bandwidthRequirement = "43Mbps/4hours";
                break;
            case 10:
                this.gpuUsage = new BigDecimal("77");
                this.batteryDrain = new BigDecimal("0");
                this.storageCapacity = 0L;
                this.bandwidthRequirement = "none";
                break;
        }
    }
    
    public BigDecimal getBonusAmount() { return bonusAmount; }
    public void setBonusAmount(BigDecimal bonusAmount) { this.bonusAmount = bonusAmount; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
