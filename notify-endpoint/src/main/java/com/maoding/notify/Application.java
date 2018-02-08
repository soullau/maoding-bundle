package com.maoding.notify;


import com.maoding.notify.verticle.SockJsServerVerticle;
import com.maoding.utils.SpringContextUtils;
import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;

import javax.annotation.PostConstruct;

/**
 * Created by Wuwq on 2016/10/10.
 */

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.maoding.notify"})
@EnableAspectJAutoProxy(exposeProxy = true)
@Import({SpringContextUtils.class})
@EnableJms
public class Application {

    @Autowired
    private SockJsServerVerticle sockJsServerVerticle;

    @Autowired
    private SpringContextUtils springContextUtils;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

       /* Vertx vertx =Vertx.vertx() ;
        VertxUtils.setVertx(vertx);
        //vertx.deployVerticle(SpringContextUtils.getApplicationContext().getBean(HttpServerVerticle.class));
        vertx.deployVerticle(SpringContextUtils.getApplicationContext().getBean(SockJsServerVerticle.class));*/
    }

    @PostConstruct
    public void deployVerticle() {
        Vertx.vertx().deployVerticle(sockJsServerVerticle);
    }
}
