package com.maoding.corpclient;

import com.maoding.corp.config.CorpClientConfig;
import com.maoding.corp.module.corpclient.service.SyncService;
import com.maoding.corp.module.corpclient.service.SyncTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Wuwq on 2017/2/9.
 * 同步的定时任务
 */
@Component
public class Schedule {
    private static final Logger log = LoggerFactory.getLogger(Schedule.class);

    @Autowired
    private SyncService syncService;

    @Autowired
    private SyncTaskService syncTaskService;

    //同步所有数据（每天凌晨1点）
    //@Scheduled(initialDelay = 0, fixedDelay = 86400 * 1000)
    @Scheduled(cron = "0 0 1 * * *")
    public void job_SyncAll() throws Exception {
        if (!Application.EnableSchedule)
            return;
        log.info("-------------- SyncAll Start --------------");
        syncTaskService.syncAllCompanyUserAndProject(CorpClientConfig.EndPoint);
    }

//    //同步组织
//    @Scheduled(initialDelay = 600 * 1000, fixedDelay = 600 * 1000)
//    public void job_SyncCompanies() throws Exception {
//        log.info("-------------- SyncCompanies Start --------------");
//        syncTaskService.syncCompany(CorpClientConfig.EndPoint);
//        log.info("-------------- SyncCompanies End --------------");
//    }

    //拉取变更
    @Scheduled(initialDelay = 10 * 1000, fixedDelay = 15 * 1000)
    public void job_PullChanges() throws Exception {
        if (!Application.EnableSchedule)
            return;
        log.info("------- -------PullChanges Start --------------");
        syncService.pullChanges(CorpClientConfig.EndPoint);
    }

    //执行同步任务
    @Scheduled(initialDelay = 20 * 1000, fixedDelay = 15 * 1000)
    public void job_RunSyncTask() throws Exception {
        if (!Application.EnableSchedule)
            return;
        log.info("-------------- RunSyncTask Start --------------");
        syncTaskService.runSyncTask(CorpClientConfig.EndPoint);
    }

//    private void testPushChanges(String companyId, String syncCmd) {
//        CompletableFuture.runAsync(() -> {
//            try {
//                //读取协同组织
//                RReadWriteLock cLock = redissonClient.getReadWriteLock("Lock_" + RKey.CorpCompanies);
//                RLock r = cLock.readLock();
//                r.lock(5, TimeUnit.SECONDS);
//                RSet<String> set_companies = redissonClient.getSet(RKey.CorpCompanies);
//                Set<String> companies = set_companies.readAll();
//                r.unlock();
//
//                if (companies.size() == 0)
//                    return;
//
//                //匹配组织
//                List<String> matches = Lists.newArrayList();
//                companies.forEach(c -> {
//                    if (StringUtils.endsWithIgnoreCase(c, ":" + companyId))
//                        matches.add(c);
//                });
//
//                if (matches.size() == 0)
//                    return;
//
//                //写入变更
//                matches.forEach(sc -> {
//
//                    String setName = RKey.CorpChanges + ":" + sc;
//                    RLock sLock = redissonClient.getLock("Lock_" + setName);
//                    sLock.lock(5, TimeUnit.SECONDS);
//
//                    RSet<Object> changes = redissonClient.getSet(setName);
//
//                    String change = syncCmd;
//                    changes.add(change);
//
//                    //  log.info("{} 添加协同变更: {}", setName, change);
//
//                    sLock.unlock();
//
//                });
//
//            } catch (Exception e) {
//                log.error("推送协同变更发生异常", e);
//                //TODO 考虑失败重试
//            }
//        });
//    }
}
