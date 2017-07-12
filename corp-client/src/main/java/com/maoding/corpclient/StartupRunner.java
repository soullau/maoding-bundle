package com.maoding.corpclient;

import com.maoding.corp.module.corpclient.service.SyncTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by Idccapp21 on 2017/2/20.
 */

@Component
public class StartupRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupRunner.class);
    @Autowired
    private SyncTaskService syncTaskService;

    @Override
    public void run(String... strings) throws Exception {
        log.info("------- ResetTaskStatus Start -------");
        syncTaskService.resetTaskStatusOnStartup();

        if (!Application.EnableSchedule)
            return;

        //log.info("-------------- SyncAll Start --------------");
        //syncTaskService.syncAll(CorpClientConfig.EndPoint);
    }

}
