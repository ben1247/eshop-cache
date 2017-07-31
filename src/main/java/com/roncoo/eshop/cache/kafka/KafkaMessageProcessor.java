package com.roncoo.eshop.cache.kafka;

import com.alibaba.fastjson.JSONObject;
import com.roncoo.eshop.cache.data.DataResource;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ShopInfo;
import com.roncoo.eshop.cache.service.CacheService;
import com.roncoo.eshop.cache.spring.SpringContext;
import com.roncoo.eshop.cache.zk.ZookeeperSession;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Component: kafka消息处理线程 Description: Date: 17/7/15
 *
 * @author yue.zhang
 */
public class KafkaMessageProcessor implements Runnable {

	private KafkaStream kafkaStream;
	private CacheService cacheService;

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public KafkaMessageProcessor(KafkaStream kafkaStream) {
		this.kafkaStream = kafkaStream;
		cacheService = (CacheService)SpringContext.getApplicationContext().getBean("cacheService");
	}

	@Override
	public void run() {
		ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
		while (it.hasNext()) {

			try {

				String message = new String(it.next().message());
				System.out.println("==========================收到了kafka消息：" + message);

				// 首先将message转换成json对象
				JSONObject messageJSONObject = JSONObject.parseObject(message);

				// 从这里提取出消息对应的服务的标识
				String serviceId = messageJSONObject.getString("serviceId");

				// 如果是商品信息服务
				if ("productInfoService".equals(serviceId)) {
					processProductInfoChangeMessage(messageJSONObject);
				} else if ("shopInfoService".equals(serviceId)){
					processShopInfoChangeMessage(messageJSONObject);
				}
			}catch (Exception e){
				System.out.println("收到一条不合理的消息：" + e.getMessage());
			}

		}
	}

	/**
	 * 处理商品信息变更的消息
	 * 
	 * @param messageJSONObject
	 */
	private void processProductInfoChangeMessage(JSONObject messageJSONObject) {

		// 提取出商品id
		Long productId = messageJSONObject.getLong("productId");

		// 在将数据直接写入redis缓存之前，应该先获取一个zk的分布式锁
		ZookeeperSession zkSession = ZookeeperSession.getInstance();

		try {
			// 调用商品信息服务的接口
			// 直接用注释模拟：getProductInfo?productId=1，传递过去
			// 商品信息服务，一般来说就会去查询数据库，去获取productId=1的商品信息，然后返回回来
			ProductInfo productInfo = DataResource.getProductInfo(productId,"2017-01-01 12:00:00");

			// 加锁
			zkSession.acquireDistributedLock(productId);

			// 获取到了锁
			// 先从redis中获取数据
			ProductInfo existedProductInfo = cacheService.getProductInfoFromRedisCache(productId);
			if(existedProductInfo != null){
				// 比较当前数据的时间版本比已有数据的时间版本是新还是旧
				try {
					Date date = sdf.parse(productInfo.getModifiedTime());
					Date existedDate = sdf.parse(existedProductInfo.getModifiedTime());
					if(date.before(existedDate)){
						System.out.println("current date=" + productInfo.getModifiedTime() + " is before existed date=[" + existedProductInfo.getModifiedTime() + "]");
						zkSession.releaseDistributeLock(productId); // 释放锁
						return;
					}else{
						System.out.println("current date=" + productInfo.getModifiedTime() + " is after existed date=[" + existedProductInfo.getModifiedTime() + "]");
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}else{
				System.out.println("existed product info is null ......");
			}


			try {
				// 假设业务代码需要处理10秒
				Thread.sleep(10 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			cacheService.saveProductInfo2LocalCache(productInfo);
			cacheService.saveProductInfo2RedisCache(productInfo);

			// 释放分布式锁
			zkSession.releaseDistributeLock(productId);
		}catch (Exception e){
			zkSession.releaseDistributeLock(productId);
			e.printStackTrace();
		}


	}

    /**
     * 处理店铺信息变更的消息
     * @param messageJSONObject
     */
	private void processShopInfoChangeMessage(JSONObject messageJSONObject){
	    // 提取出店铺id
        Long shopId = messageJSONObject.getLong("shopId");
        // 根据shopId查库或者调接口获取店铺信息数据，这里就模拟写死了数据
		ShopInfo shopInfo = DataResource.getShopInfo(shopId);
        cacheService.saveShopInfo2LocalCache(shopInfo);
		System.out.println("==========================获取刚保存到本地缓存的店铺信息："+ cacheService.getShopInfoFromLocalCache(shopInfo.getId()));
        cacheService.saveShopInfo2RedisCache(shopInfo);
    }
}
