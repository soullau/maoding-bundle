package com.maoding.notify.endpoint;

import com.maoding.notify.endpoint.utils.SpringContextUtils;
import com.maoding.notify.endpoint.verticle.SockJsServerVerticle;
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
@ComponentScan({"com.maoding.notify.endpoint"})
@Import({SpringContextUtils.class})
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableJms
public class Application {

    @Autowired
    private SockJsServerVerticle sockJsServerVerticle;

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
