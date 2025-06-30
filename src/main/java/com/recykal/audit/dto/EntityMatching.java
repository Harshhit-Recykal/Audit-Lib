package com.recykal.audit.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "audit.entity-matching")
public class EntityMatching {

    /**
     * Maps incoming URL entity names (like "products") to JPA entity class names (like "Product")
     */
    private Map<String, String> map = new HashMap<>();

    /**
     * Allows dynamic addition during runtime (optional)
     */
    public void addEntity(String url, String entityName) {
        map.put(url, entityName);
    }

    /**
     * Returns matched entity name, or falls back to original key if not found
     */
    public String getEntityName(String key) {
        return map.getOrDefault(key, key); // fallback to key itself
    }
}
