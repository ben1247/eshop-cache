package com.roncoo.eshop.cache.controller;

import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ShopInfo;
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
			// TODO 从数据库中获取
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
			// TODO 从数据库中获取
		}

		return shopInfo;
	}
}
