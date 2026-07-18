package com.demo.auth.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to manage blacklisted (logged out) tokens.
 * In production, use Redis instead of in-memory storage.
 */
@Service
public class TokenBlacklistService {

    // In production, replace with Redis:
    // @Autowired private RedisTemplate<String, String> redisTemplate;
    
    private final Set<String> blacklist = ConcurrentHashMap.newKeySet();

    /**
     * Add token to blacklist when user logs out
     */
    public void blacklistToken(String token) {
        blacklist.add(token);
    }

    /**
     * Check if token is blacklisted
     */
    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }

    /**
     * Optional: Clear old tokens periodically
     * (In real production with Redis, expiry is automatic)
     */
    public void clearOldTokens() {
        // Could implement token expiration cleanup here
    }
}