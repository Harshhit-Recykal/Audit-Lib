package com.recykal.audit.service;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "custom")
@Getter
@Setter
public class EntityMatching {

    private Map<String, String> map = new HashMap<>();
    @PostConstruct
    public void init() {
        map.put("products", "Product");
        // Add more URL-EntityName mappings as needed
    }
    public void addEntity(String url , String entityName){
        map.put(url, entityName);
    }

    public String getEntityName(String entityName) {
        return map.get(entityName);
    }
}
