package com.cnwr.archaius.redis;

import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.netflix.config.PollResult;
import com.netflix.config.PolledConfigurationSource;


public class RedisConfigurationSource implements PolledConfigurationSource
{

    private String configKey = "archaius";
    private String host = "localhost";
    private JedisPool pool;

    
    public RedisConfigurationSource() {
        this.pool = new JedisPool(new JedisPoolConfig(), host);
    }
    
    public RedisConfigurationSource(JedisPool pool) {
        this.pool = pool;
    }
    
    public RedisConfigurationSource(String host, String configKey) {
        this.host = host;
        this.configKey = configKey;
        this.pool = new JedisPool(new JedisPoolConfig(), host);
    }
    
    public RedisConfigurationSource(String host) {
        this.host = host;
    }
    
    public void setConfigPrefix(String configKey) {
        this.configKey = configKey;
    }

    
    public void setHost(String host) {
        this.host = host;
    }

    public PollResult poll(boolean initial, Object checkPoint) throws Exception {
        Map<String, Object> map = loadConfig();
        return PollResult.createFull(map);
    }
    
    synchronized Map<String, Object> loadConfig() {
        Map<String, Object> map = new HashMap<String, Object>();
       
        Jedis jedis = pool.getResource();
        Map<String, String> values = jedis.hgetAll(configKey);
        
        for (Map.Entry<String, String> entry : values.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        
        pool.returnResource(jedis);
        return map;
    }
}
