package com.maoding.yongyoucloud;

import com.maoding.config.MultipartConfig;
import com.maoding.utils.SpringContextUtils;
import com.maoding.yongyoucloud.module.enterprise.service.EnterpriseSearchServiceImpl;
import com.maoding.yongyoucloud.config.PoolConnManagerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * Created by Wuwq on 2016/12/13.
 */
@Configuration
@EnableAutoConfiguration(exclude = {MultipartAutoConfiguration.class})
@ComponentScan(basePackages = {"com.maoding.yongyoucloud"})
@Import({SpringContextUtils.class, MultipartConfig.class, PoolConnManagerConfig.class})
@EnableAspectJAutoProxy(exposeProxy = true)
public class Application extends SpringBootServletInitializer {
    private static final Logger logger = LoggerFactory.getLogger(EnterpriseSearchServiceImpl.class);
    public static void main(String[] args) {
        try{
            SpringApplication.run(Application.class, args);
        }catch (Exception e){
            logger.debug(e.getMessage());
        }
    }
}
