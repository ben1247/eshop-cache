package com.roncoo.eshop.cache.listener;

import com.roncoo.eshop.cache.kafka.KafkaConsumer;
import com.roncoo.eshop.cache.rebuild.RebuildCacheThread;
import com.roncoo.eshop.cache.spring.SpringContext;
import com.roncoo.eshop.cache.zk.ZookeeperSession;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Component: 系统初始化的监听器
 * Description:
 * Date: 17/7/15
 *
 * @author yue.zhang
 */
public class InitListener implements ServletContextListener{

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        // 设置spring的上下文
        ServletContext sc = sce.getServletContext();
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(sc);
        SpringContext.setApplicationContext(context);

        // kafka 消费线程
        new Thread(new KafkaConsumer("cache-message")).start();
        // 重建cache线程
        new Thread(new RebuildCacheThread()).start();

        // 初始化zk
        ZookeeperSession.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
