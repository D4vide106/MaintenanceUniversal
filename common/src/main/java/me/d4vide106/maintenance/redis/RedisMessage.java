package me.d4vide106.maintenance.redis;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Message structure for Redis pub/sub communication.
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class RedisMessage {
    
    private static final Gson GSON = new Gson();
    
    private final MessageType type;
    private final String server;
    private final long timestamp;
    private final Map<String, String> data;
    
    public RedisMessage(
        @NotNull MessageType type,
        @NotNull String server
    ) {
        this.type = type;
        this.server = server;
        this.timestamp = System.currentTimeMillis();
        this.data = new HashMap<>();
    }
    
    public RedisMessage set(@NotNull String key, @Nullable String value) {
        data.put(key, value);
        return this;
    }
    
    @NotNull
    public MessageType getType() {
        return type;
    }
    
    @NotNull
    public String getServer() {
        return server;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    @Nullable
    public String get(@NotNull String key) {
        return data.get(key);
    }
    
    @NotNull
    public Map<String, String> getData() {
        return new HashMap<>(data);
    }
    
    /**
     * Serializes this message to JSON.
     */
    @NotNull
    public String serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("type", type.name());
        json.addProperty("server", server);
        json.addProperty("timestamp", timestamp);
        json.add("data", GSON.toJsonTree(data));
        return json.toString();
    }
    
    /**
     * Deserializes a message from JSON.
     */
    @NotNull
    public static RedisMessage deserialize(@NotNull String json) {
        JsonObject obj = GSON.fromJson(json, JsonObject.class);
        MessageType type = MessageType.valueOf(obj.get("type").getAsString());
        String server = obj.get("server").getAsString();
        
        RedisMessage message = new RedisMessage(type, server);
        
        JsonObject dataObj = obj.getAsJsonObject("data");
        for (String key : dataObj.keySet()) {
            message.set(key, dataObj.get(key).getAsString());
        }
        
        return message;
    }
    
    /**
     * Message types for different actions.
     */
    public enum MessageType {
        MAINTENANCE_ENABLED,
        MAINTENANCE_DISABLED,
        WHITELIST_ADDED,
        WHITELIST_REMOVED,
        WHITELIST_CLEARED,
        TIMER_SCHEDULED,
        TIMER_CANCELLED,
        CONFIG_RELOAD
    }
}