package me.d4vide106.maintenance.redis;

import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Redis manager for multi-server synchronization.
 * <p>
 * Uses pub/sub for real-time updates across server network.
 * </p>
 * 
 * @author D4vide106
 * @version 1.0.0
 * @since 1.0.0
 */
public class RedisManager {
    
    private final JedisPool pool;
    private final String channel;
    private final ExecutorService executor;
    private JedisPubSub subscriber;
    private Consumer<RedisMessage> messageHandler;
    
    public RedisManager(
        @NotNull String host,
        int port,
        @NotNull String password,
        int database,
        @NotNull String channel
    ) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(16);
        config.setMaxIdle(8);
        config.setMinIdle(2);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        config.setTestWhileIdle(true);
        
        if (password.isEmpty()) {
            this.pool = new JedisPool(config, host, port, 2000, null, database);
        } else {
            this.pool = new JedisPool(config, host, port, 2000, password, database);
        }
        
        this.channel = channel;
        this.executor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setName("MaintenanceRedis-Sub");
            thread.setDaemon(true);
            return thread;
        });
    }
    
    /**
     * Initializes Redis connection and starts subscriber.
     */
    public CompletableFuture<Void> initialize() {
        return CompletableFuture.runAsync(() -> {
            try (Jedis jedis = pool.getResource()) {
                jedis.ping();
            }
        });
    }
    
    /**
     * Starts listening for messages on the channel.
     */
    public void subscribe(@NotNull Consumer<RedisMessage> handler) {
        this.messageHandler = handler;
        
        this.subscriber = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                try {
                    RedisMessage msg = RedisMessage.deserialize(message);
                    if (messageHandler != null) {
                        messageHandler.accept(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        
        executor.execute(() -> {
            try (Jedis jedis = pool.getResource()) {
                jedis.subscribe(subscriber, channel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Publishes a message to the channel.
     */
    public CompletableFuture<Void> publish(@NotNull RedisMessage message) {
        return CompletableFuture.runAsync(() -> {
            try (Jedis jedis = pool.getResource()) {
                jedis.publish(channel, message.serialize());
            }
        });
    }
    
    /**
     * Checks if Redis is connected.
     */
    public boolean isConnected() {
        try (Jedis jedis = pool.getResource()) {
            return jedis.isConnected();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Shuts down Redis connection.
     */
    public CompletableFuture<Void> shutdown() {
        return CompletableFuture.runAsync(() -> {
            if (subscriber != null && subscriber.isSubscribed()) {
                subscriber.unsubscribe();
            }
            if (pool != null && !pool.isClosed()) {
                pool.close();
            }
            executor.shutdown();
        });
    }
}