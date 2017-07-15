package com.roncoo.eshop.cache.service;

import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ShopInfo;

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

    /**
     * 将商品信息保存到本地缓存中
     * @param productInfo
     */
    ProductInfo saveProductInfo2LocalCache(ProductInfo productInfo);

    /**
     * 从本地缓存中获取商品信息
     * @param productId
     * @return
     */
    ProductInfo getProductInfoFromLocalCache(Long productId);

    /**
     * 将店铺信息保存到本地缓存中
     * @param shopInfo
     */
    ShopInfo saveShopInfo2LocalCache(ShopInfo shopInfo);

    /**
     * 从本地缓存中获取店铺信息
     * @param shopId
     * @return
     */
    ShopInfo getShopInfoFromLocalCache(Long shopId);

    /**
     * 将商品信息保存到redis中
     * @param productInfo
     */
    void saveProductInfo2RedisCache(ProductInfo productInfo);

    /**
     * 将店铺信息保存到redis中
     * @param shopInfo
     */
    void saveShopInfo2RedisCache(ShopInfo shopInfo);


}
