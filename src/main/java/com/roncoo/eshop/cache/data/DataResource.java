package com.roncoo.eshop.cache.data;

import com.alibaba.fastjson.JSONObject;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ShopInfo;

/**
 * Component:
 * Description:
 * Date: 17/7/31
 *
 * @author yue.zhang
 */
public class DataResource {

    public static ProductInfo getProductInfo(Long productId,String modifiedTime){

        Long id = ( productId != null ) ? productId : 1;

        String productInfoJSON = "{\"id\": "+id+", \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1, \"modified_time\": \""+modifiedTime+"\"}";

        ProductInfo productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);

        return productInfo;
    }

    public static ShopInfo getShopInfo(Long shopId){

        Long id = ( shopId != null ) ? shopId : 1;

        String shopInfoJSON = "{\"id\": "+id+", \"name\": \"小王的手机店\", \"level\": 5, \"goodCommentRate\":0.99}";

        ShopInfo shopInfo = JSONObject.parseObject(shopInfoJSON,ShopInfo.class);

        return shopInfo;
    }

}
