package com.roncoo.eshop.cache.spring;


import org.springframework.context.ApplicationContext;

/**
 * Component: spring 上下文
 * Description:
 * Date: 17/7/15
 *
 * @author yue.zhang
 */
public class SpringContext {

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringContext.applicationContext = applicationContext;
    }
}
