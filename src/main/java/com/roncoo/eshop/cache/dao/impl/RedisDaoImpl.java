package com.roncoo.eshop.cache.dao.impl;

import com.roncoo.eshop.cache.dao.RedisDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.JedisCluster;

/**
 * Component:
 * Description:
 * Date: 17/6/30
 *
 * @author yue.zhang
 */
@Repository
public class RedisDaoImpl implements RedisDao {

    @Autowired
    private JedisCluster jedisCluster;

    @Override
    public void set(String key, String value) {
        jedisCluster.set(key,value);
    }

    @Override
    public String get(String key) {
        return jedisCluster.get(key);
    }

    @Override
    public void delete(String key) {
        jedisCluster.del(key);
    }
}
