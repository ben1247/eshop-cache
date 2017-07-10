package com.roncoo.eshop.cache.dao;

/**
 * Component:
 * Description:
 * Date: 17/6/30
 *
 * @author yue.zhang
 */
public interface RedisDao {

    void set(String key ,String value);

    String get(String key);

    void delete(String key);
}
