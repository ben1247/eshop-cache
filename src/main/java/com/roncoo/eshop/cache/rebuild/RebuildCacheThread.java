package com.roncoo.eshop.cache.rebuild;

import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.service.CacheService;
import com.roncoo.eshop.cache.spring.SpringContext;
import com.roncoo.eshop.cache.zk.ZookeeperSession;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Component: 缓存重建线程
 * Description:
 * Date: 17/7/31
 *
 * @author yue.zhang
 */
public class RebuildCacheThread implements Runnable{

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void run() {

        RebuildCacheQueue rebuildCacheQueue = RebuildCacheQueue.getInstacne();
        ZookeeperSession zkSession = ZookeeperSession.getInstance();
        CacheService cacheService = (CacheService)SpringContext.getApplicationContext().getBean("cacheService");

        while (true){
            ProductInfo productInfo = rebuildCacheQueue.takeProductInfo();

            zkSession.acquireDistributedLock(productInfo.getId()); // 加锁

            ProductInfo existedProductInfo = cacheService.getProductInfoFromRedisCache(productInfo.getId());

            if(existedProductInfo != null){
                // 比较当前数据的时间版本比已有数据的时间版本是新还是旧
                try {
                    Date date = sdf.parse(productInfo.getModifiedTime());
                    Date existedDate = sdf.parse(existedProductInfo.getModifiedTime());
                    if(date.before(existedDate)){
                        System.out.println("current date=" + productInfo.getModifiedTime() + " is before existed date=[" + existedProductInfo.getModifiedTime() + "]");
                        zkSession.releaseDistributeLock(productInfo.getId()); // 释放锁
                        continue;
                    }else{
                        System.out.println("current date=" + productInfo.getModifiedTime() + " is after existed date=[" + existedProductInfo.getModifiedTime() + "]");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }else{
                System.out.println("existed product info is null ......");
            }

            cacheService.saveProductInfo2LocalCache(productInfo);
            cacheService.saveProductInfo2RedisCache(productInfo);

            // 释放分布式锁
            zkSession.releaseDistributeLock(productInfo.getId());
        }
    }
}
