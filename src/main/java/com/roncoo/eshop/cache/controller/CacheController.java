package com.roncoo.eshop.cache.controller;

import com.roncoo.eshop.cache.data.DataResource;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ShopInfo;
import com.roncoo.eshop.cache.rebuild.RebuildCacheQueue;
import com.roncoo.eshop.cache.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Component: 缓存controller Description: Date: 17/7/10
 *
 * @author yue.zhang
 */
@Controller
public class CacheController {

	@Autowired
	private CacheService cacheService;

	@RequestMapping("/testPutCache")
	@ResponseBody
	public String testPutCache(ProductInfo productInfo) {
		cacheService.saveLocalCache(productInfo);
		return "success";
	}

	@RequestMapping("/testGetCache")
	@ResponseBody
	public ProductInfo testGetCache(Long id) {
		return cacheService.getLocalCache(id);
	}

	@RequestMapping("/getProductInfo")
	@ResponseBody
	public ProductInfo getProductInfo(Long productId){
		ProductInfo productInfo = null;

		productInfo = cacheService.getProductInfoFromRedisCache(productId);
		System.out.println("=================从redis中获取缓存，商品信息=" + productInfo);

		if(productInfo == null){
			productInfo = cacheService.getProductInfoFromLocalCache(productId);
			System.out.println("=================从ehcache中获取缓存，商品信息=" + productInfo);
		}

		if(productInfo == null){
			// 从数据库中获取，这里就模拟从数据库拿到了数据
			productInfo = DataResource.getProductInfo(productId,"2017-01-01 12:01:00");
			// 将数据推送到一个内存队列中
			RebuildCacheQueue rebuildCacheQueue = RebuildCacheQueue.getInstacne();
			rebuildCacheQueue.putProductInfo(productInfo);
		}

		return productInfo;
	}

	@RequestMapping("/getShopInfo")
	@ResponseBody
	public ShopInfo getShopInfo(Long shopId){
		ShopInfo shopInfo = null;

		shopInfo = cacheService.getShopInfoFromRedisCache(shopId);
		System.out.println("=================从redis中获取缓存，店铺信息=" + shopInfo);

		if(shopInfo == null){
			shopInfo = cacheService.getShopInfoFromLocalCache(shopId);
			System.out.println("=================从ehcache中获取缓存，店铺信息=" + shopInfo);
		}

		if(shopInfo == null){
			// 从数据库中获取，这里就模拟从数据库拿到了数据


		}

		return shopInfo;
	}
}
