package com.roncoo.eshop.cache.service;

import com.roncoo.eshop.cache.model.ProductInfo;

/**
 * Component: 缓存service接口
 * Description:
 * Date: 17/7/10
 *
 * @author yue.zhang
 */
public interface CacheService {

    /**
     * 将商品信息保存到本地缓存中
     * @param productInfo
     * @return
     */
    ProductInfo saveLocalCache(ProductInfo productInfo);

    /**
     * 从本地缓存中获取商品信息
     * @param id
     * @return
     */
    ProductInfo getLocalCache(Long id);

}
