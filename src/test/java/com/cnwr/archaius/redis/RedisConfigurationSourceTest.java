package com.cnwr.archaius.redis;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.netflix.config.DynamicConfiguration;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.config.FixedDelayPollingScheduler;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class RedisConfigurationSourceTest {

    Jedis jedisMock;
    JedisPool pool;
    RedisConfigurationSource rcs;
    
    @Before
    public void setUp() throws Exception {
        jedisMock = Mockito.mock(Jedis.class);
        pool = Mockito.mock(JedisPool.class);
        Mockito.when(pool.getResource()).thenReturn(jedisMock);
        
        HashMap<String, String> testData = new HashMap<String, String>();
        
        testData.put("testkey", "testvalue");
        testData.put("testkey2", "testvalue2");
        
        Mockito.when(jedisMock.hgetAll("archaius")).thenReturn(testData);
        rcs = new RedisConfigurationSource(pool);
    }

    @Test
    public void testMockRedisConfigurationSource() {
        FixedDelayPollingScheduler scheduler = new FixedDelayPollingScheduler(0, 10, false);
        DynamicConfiguration configuration = new DynamicConfiguration(rcs, scheduler);
        DynamicPropertyFactory.initWithConfigurationSource(configuration);
        DynamicStringProperty defaultProp = DynamicPropertyFactory.getInstance().getStringProperty(
                "moo", "cow");
        assertEquals("cow", defaultProp.get());
        
        DynamicStringProperty prop1 = DynamicPropertyFactory.getInstance().getStringProperty(
                "testkey", "cow");
        assertEquals("testvalue", prop1.get());    }

}
