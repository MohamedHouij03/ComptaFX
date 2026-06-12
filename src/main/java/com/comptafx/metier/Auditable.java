package com.comptafx.metier;

import java.time.LocalDateTime;

/**
 * Interface for entities that need audit tracking
 */
public interface Auditable {
    
    LocalDateTime getCreatedAt();
    
    void setCreatedAt(LocalDateTime createdAt);
    
    LocalDateTime getUpdatedAt();
    
    void setUpdatedAt(LocalDateTime updatedAt);
    
    String getCreatedBy();
    
    void setCreatedBy(String createdBy);
    
    String getUpdatedBy();
    
    void setUpdatedBy(String updatedBy);
}

