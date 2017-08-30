package com.roncoo.eshop.cache.prewarm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.service.CacheService;
import com.roncoo.eshop.cache.spring.SpringContext;
import com.roncoo.eshop.cache.zk.ZookeeperSession;

/**
 * Component: 缓存预热线程
 * Description:
 * Date: 17/8/30
 *
 * @author yue.zhang
 */
public class CachePrewarmThread extends Thread {

    @Override
    public void run() {

        CacheService cacheService = (CacheService) SpringContext.getApplicationContext().getBean("cacheService");

        ZookeeperSession zkSession = ZookeeperSession.getInstance();

        // 获取taskid列表
        String taskidList = zkSession.getNodeData("/taskid-list");

        if(taskidList != null && !"".equals(taskidList)){
            String [] taskidListSplited = taskidList.split(",");
            for(String taskid : taskidListSplited){
                String taskidLockPath = "/taskid-lock-" + taskid;
                boolean result = zkSession.acquireFastFailedDistributedLock(taskidLockPath);
                if(!result){
                    continue;
                }

                String taskidStatusLockPath = "/taskid-status-lock-" + taskid;
                zkSession.acquireDistributedLock(taskidStatusLockPath);

                String taskidStatus = zkSession.getNodeData("/taskid-status-" + taskid);
                if("".equals(taskidStatus)){
                    // 没有预热过
                    String productidList = zkSession.getNodeData("/task-hot-product-list-" + taskid);
                    JSONArray productidJSONArray = JSONArray.parseArray(productidList);
                    for(int i = 0 ; i < productidJSONArray.size(); i++){
                        Long productId = productidJSONArray.getLong(i);
                        // 从数据库里查出商品信息，这里就是简单的模拟一下
                        String productInfoJSON = "{\"id\": " + productId + ", \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1, \"modifiedTime\": \"2017-01-01 12:00:00\"}";
                        ProductInfo productInfo = JSONObject.parseObject(productInfoJSON,ProductInfo.class);
                        cacheService.saveProductInfo2LocalCache(productInfo);
                        cacheService.saveProductInfo2RedisCache(productInfo);
                    }

                    zkSession.setNodeData("/taskid-status-" + taskid , "success");
                }
                zkSession.releaseDistributeLock(taskidStatusLockPath);

                zkSession.releaseDistributeLock(taskidLockPath);
            }
        }

    }
}
