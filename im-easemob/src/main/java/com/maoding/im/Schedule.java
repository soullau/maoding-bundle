package com.maoding.im;

import com.maoding.im.module.imQueue.service.ImQueueService;
import com.maoding.im.module.schedule.service.ScheduleService;
import javafx.concurrent.ScheduledService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Wuwq on 2017/2/9.
 * 同步的定时任务
 */
@Component
public class Schedule {
    private static final Logger log = LoggerFactory.getLogger(Schedule.class);

    @Autowired
    private ImQueueService imQueueService;
    @Autowired
    private ScheduleService scheduleService;

    @Scheduled(initialDelay = 10 * 1000, fixedDelay = 15 * 1000)
    public void job_PullAndProcessImQueue() throws Exception {
        if (!Application.EnableSchedule)
            return;
        log.info("------- ------- ------- PullAndProcessImQueue Start ------- ------- -------");
        imQueueService.pullAndProcessImQueue(50);
    }

    @Scheduled(fixedDelay = 60 * 1000) //每1分钟调用一次
    public void job_ScheduleNotify() throws Exception {
        log.info("------- ------- ------- ScheduleNotify Start ------- ------- -------");
        scheduleService.notifySchedule();
    }
}
