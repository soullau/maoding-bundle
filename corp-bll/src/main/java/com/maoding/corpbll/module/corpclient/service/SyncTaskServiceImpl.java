package com.maoding.corpbll.module.corpclient.service;

import com.google.common.collect.Lists;
import com.maoding.core.base.BaseService;
import com.maoding.core.bean.ApiResult;
import com.maoding.corpbll.constDefine.*;
import com.maoding.corpbll.module.corpclient.dao.SyncTaskDao;
import com.maoding.corpbll.module.corpclient.dao.SyncTaskGroupDao;
import com.maoding.corpbll.module.corpclient.model.SyncTask;
import com.maoding.corpbll.module.corpclient.model.SyncTaskGroup;
import com.maoding.corpbll.module.corpserver.dto.SyncCompanyDto_Select;
import com.maoding.utils.JsonUtils;
import com.maoding.utils.StringUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by Wuwq on 2017/2/14.
 */
@Service("syncTaskService")
public class SyncTaskServiceImpl extends BaseService implements SyncTaskService {

    private static final Logger log = LoggerFactory.getLogger(SyncTaskServiceImpl.class);

    @Autowired
    private SyncTaskGroupDao syncTaskGroupDao;

    @Autowired
    private SyncTaskDao syncTaskDao;

    //@Autowired
    //private SyncCompanyDao syncCompanyDao;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private CoService coService;

    @Autowired
    private SyncService syncService;

//    /**
//     * 同步组织
//     */
//    @Override
//    public void syncCompany(String corpEndpoint) throws Exception {
//        ApiResult apiResult = coService.pullFromCorpServer(null, CorpServer.URL_GET_SYNC_COMPANY + "/" + corpEndpoint);
//        if (apiResult.isSuccessful()) {
//            List<SyncCompanyDto_Select> scsNew = JsonUtils.json2list(JsonUtils.obj2json(apiResult.getData()), SyncCompanyDto_Select.class);
//            List<SyncCompanyDto_Select> scsOld = syncCompanyDao.selectSyncCompany(corpEndpoint);
//
//            if (scsNew == null || scsNew.size() == 0)
//                return;
//
//            List<SyncCompanyDto_Select> adds = scsNew.stream().filter(n -> !scsOld.stream().anyMatch(o -> o.getId().equalsIgnoreCase(n.getId()))).collect(Collectors.toList());
//
//
//            List<SyncCompanyDto_Select> deletes = scsOld.stream().filter(o -> !scsNew.stream().anyMatch(n -> n.getId().equalsIgnoreCase(o.getId()))).collect(Collectors.toList());
//
//            if (deletes != null && deletes.size() > 0) {
//                deletes.forEach(d ->
//                {
//                    syncCompanyDao.deleteByPrimaryKey(d.getId());
//                    log.info("移除组织：{}({})", d.getCompanyId(), d.getCompanyName());
//                });
//            }
//
//            if (adds != null && adds.size() > 0) {
//                adds.forEach(a ->
//                {
//                    SyncCompany sc = new SyncCompany();
//                    sc.initEntity();
//                    BeanUtils.copyProperties(a, sc);
//                    syncCompanyDao.insert(sc);
//                    log.info("新增组织：{}({})", a.getCompanyId(), a.getCompanyName());
//                });
//            }
//        }
//    }

    /**
     * 同步组织人员和项目
     */
    @Override
    public void syncAllCompanyUserAndProject(String corpEndpoint) throws Exception {
        if (StringUtils.isBlank(corpEndpoint))
            throw new RuntimeException("拉取变更失败，corpEndPoint为空");

        ApiResult apiResult = syncService.pullFromCorpServer(CorpServer.URL_GET_SYNC_COMPANY + "/" + corpEndpoint);
        if (!apiResult.isSuccessful())
            throw new RuntimeException("拉取同步组织失败：" + apiResult.getMsg());

        List<SyncCompanyDto_Select> scs = JsonUtils.json2list(JsonUtils.obj2json(apiResult.getData()), SyncCompanyDto_Select.class);

        if (scs == null || scs.size() == 0)
            return;

        //计数器控制
        CountDownLatch latch = new CountDownLatch(scs.size());

        //信号量控制
        Semaphore sp = new Semaphore(2);

        scs.forEach(s -> {
            SyncCompanyDto_Select sc = s;
            //异步并发
            CompletableFuture.runAsync(() -> {
                try {
                    sp.acquire();

                    //执行同步逻辑
                    syncOneCompanyUserAndProject(sc.getCompanyId());

                } catch (Exception e) {
                    log.error("异步并发 syncCompanyUserAndProject 失败1", e);
                } finally {
                    sp.release();
                    latch.countDown();
                }
            });
        });

        try {
            //阻塞线程（超时控制按每个2分钟来算)
            latch.await(scs.size() * 2, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.error("异步并发 syncCompanyUserAndProject 失败2", e);
        }
    }

    /**
     * 同步组织人员和项目
     */
    @Override
    public ApiResult syncOneCompanyUserAndProject(String companyId) {
        try {
            coService.pushUserByCompanyId(companyId, null);
            coService.pushProjectByCompanyId(companyId, null);
        } catch (Exception e) {
            log.error(String.format("组织：%s 拉取同步数据发生异常", companyId), e);
        }
        return ApiResult.success(null, null);
    }


    /**
     * 同步任务
     */
    private void runOneTask(SyncTask task) throws InterruptedException {
        //筛选待同步、待重试
        int syncStatus = task.getSyncStatus();
        if (syncStatus == SyncStatus.WaitSync || syncStatus == SyncStatus.WaitRetry) {

            task.setLastEntry(LocalDateTime.now());

            //重试次数+1
            if (syncStatus == SyncStatus.WaitRetry) {
                task.setRetryTimes(task.getRetryTimes() + 1);
                log.info("{}-{} 任务（{}）：{} 第{}次重试", task.getCorpEndpoint(), task.getCompanyId(), task.getSyncCmd(), task.getId(), task.getRetryTimes());
            } else {
                log.info("{}-{} 任务（{}）：{} 开始执行", task.getCorpEndpoint(), task.getCompanyId(), task.getSyncCmd(), task.getId());
            }


            boolean isOk = false;
            //TODO 同步逻辑
            try {

                if (log.isDebugEnabled())
                    log.debug("Ep：{} 组织：{} 任务（{}）：{}  同步开始", task.getCorpEndpoint(), task.getCompanyId(), task.getSyncCmd(), task.getId());

                if (SyncCmd.ALL.equalsIgnoreCase(task.getSyncCmd())) {
                    ApiResult apiResult = syncOneCompanyUserAndProject(task.getCompanyId());
                    isOk = apiResult.isSuccessful();
                } else if (SyncCmd.CU.equalsIgnoreCase(task.getSyncCmd())) {

                    ApiResult apiResult = coService.pushUserByCompanyId(task.getCompanyId(), task.getSyncPoint());
                    isOk = apiResult.isSuccessful();

                } else if (SyncCmd.PU.equalsIgnoreCase(task.getSyncCmd())) {

                    ApiResult apiResult = coService.setProject(task.getCompanyId(), task.getProjectId(), task.getSyncPoint());
                    isOk = apiResult.isSuccessful();

                } else if (SyncCmd.PT.equalsIgnoreCase(task.getSyncCmd())) {

                    ApiResult apiResult = coService.setProjectNodes(task.getCompanyId(), task.getProjectId(), task.getSyncPoint());
                    isOk = apiResult.isSuccessful();

                } else if (SyncCmd.PF.equalsIgnoreCase(task.getSyncCmd())) {

                    /*paraMap.put("projectId", task.getProjectId());
                    paraMap.put("syncDate", task.getSyncPoint().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                    ApiResult apiResult = coService.getSkyDriveByProjectId(paraMap);
                    isOk = apiResult.isSuccessful();*/

                }

                if (log.isDebugEnabled())
                    log.debug("Ep：{} 组织：{} 任务（{}）：{}  同步结束", task.getCorpEndpoint(), task.getCompanyId(), task.getSyncCmd(), task.getId());

            } catch (Exception e) {
                log.error(String.format("执行1级同步任务：%s 发生异常", task.getId()), e);
            }

            if (isOk) {
                task.setSyncStatus(SyncStatus.SyncSuccess);
                task.setTaskStatus(TaskStatus.Finished);
                if (syncTaskDao.updateByPrimaryKey(task) > 0)
                    log.info("{}-{} 任务（{}）：{} 同步成功，状态更新为：已结束", task.getCorpEndpoint(), task.getCompanyId(), task.getSyncCmd(), task.getId());
            } else {
                if (task.getRetryTimes() > 10) {
                    task.setSyncStatus(SyncStatus.SyncFailed);
                    if (syncTaskDao.updateByPrimaryKey(task) > 0)
                        log.info("{}-{} 任务（{}）：{} 同步失败，状态更新为：已结束", task.getCorpEndpoint(), task.getCompanyId(), task.getSyncCmd(), task.getId());
                } else {
                    task.setSyncStatus(SyncStatus.WaitRetry);
                    if (syncTaskDao.updateByPrimaryKey(task) > 0)
                        log.info("{}-{} 任务（{}）：{} 同步失败，待重试", task.getCorpEndpoint(), task.getCompanyId(), task.getSyncCmd(), task.getId());

                    Thread.sleep(5 * 1000);

                    //重新从数据库加载
                    task = syncTaskDao.selectByPrimaryKey(task.getId());
                    runOneTask(task);
                }
            }
        } else {
            task.setTaskStatus(TaskStatus.Finished);
            if (syncTaskDao.updateByPrimaryKey(task) > 0)
                log.info("{}-{} 任务（{}）：{} 状态更新为：已结束", task.getCorpEndpoint(), task.getCompanyId(), task.getSyncCmd(), task.getId());
        }
    }

    /**
     * 执行指定优先级的任务
     */
    private boolean runTasksByPriority(String corpEndpoint, String companyId, SyncTaskGroup group, int syncPriority, List<SyncTask> tasks) {
        if (tasks.size() > 0) {
            log.info("{}-{} 开始执行 {} 级同步任务", corpEndpoint, companyId, syncPriority);

            //计数器控制
            CountDownLatch latch = new CountDownLatch(tasks.size());

            //信号量控制
            Semaphore sp = new Semaphore(2);

            tasks.forEach(t -> {
                SyncTask task = t;
                //异步并发
                CompletableFuture.runAsync(() -> {
                    try {
                        sp.acquire();

                        //执行同步逻辑
                        runOneTask(task);

                    } catch (Exception e) {
                        log.error(String.format("%s-%s 执行 %s 级同步任务：%s 发生异常", corpEndpoint, companyId, syncPriority, task.getId()), e);
                    } finally {
                        sp.release();
                        latch.countDown();
                    }
                });
            });

            try {
                //组塞线程（超时控制按每个三分钟来算)
                latch.await(tasks.size() * 3, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                log.error(String.format("%s-%s 执行 %s 级同步任务 发生异常", corpEndpoint, companyId, syncPriority), e);
            }

            //判断1级同步任务是否还存在未完成任务，若存在则返回等待下一次重试
            if (tasks.stream().filter(t -> t.getTaskStatus() != TaskStatus.Finished).count() > 0) {
                if (syncTaskGroupDao.updateAsWaitRunningStatus(group.getId(), group.getTaskGroupStatus()) > 0) {
                    group.setTaskGroupStatus(TaskGroupStatus.WaitRuning);
                    log.info("{}-{} 任务组：{} 更新状态为：待执行", corpEndpoint, companyId, group.getId());
                }
                return false;
            }
        }
        return true;
    }

    /**
     * 执行指定协同端下的组织的任务
     */
    private void runTasksByCompany(String corpEndpoint, String companyId) {
        //查询未完成的任务组（按1个来查）
        List<SyncTaskGroup> groups = syncTaskGroupDao.selectUnfinishedTaskGroups(corpEndpoint, companyId, 1);
        if (groups.size() == 0)
            return;
        log.info("{}-{} 发现未完成的任务组：{} 个", corpEndpoint, companyId, groups.size());

        SyncTaskGroup group = groups.get(0);
        //判断状态是否为执行中
        if (group.getTaskGroupStatus() == TaskGroupStatus.Running) {
            long seconds = Duration.between(group.getLastEntry(), LocalDateTime.now()).getSeconds();
            long minutes = seconds / 60;
            if (minutes >= group.getTaskAmount() * 3) {
                log.info("{}-{} 任务组：{} 之前执行了 {} 分钟，已超时", corpEndpoint, companyId, group.getId(), minutes);
                if (syncTaskGroupDao.updateAsWaitRunningStatus(group.getId(), group.getTaskGroupStatus()) > 0) {
                    group.setTaskGroupStatus(TaskGroupStatus.WaitRuning);
                    log.info("{}-{} 任务组：{} 更新状态为：待执行", corpEndpoint, companyId, group.getId());
                }
            } else
                log.info("{}-{} 任务组：{} 之前执行了 {} 分钟", corpEndpoint, companyId, group.getId(), minutes);
            return;
        }

        if (syncTaskGroupDao.updateAsRunningStatus(group.getId(), group.getTaskGroupStatus(), LocalDateTime.now()) == 0)
            return;
        group.setTaskGroupStatus(TaskGroupStatus.Running);
        log.info("{}-{} 任务组：{} 更新状态为：执行中", corpEndpoint, companyId, group.getId());

        //获取待同步的任务
        List<SyncTask> tasks = syncTaskDao.selectUnfinishedTasks(group.getId(), 0);
        log.info("{}-{} 发现 {} 个待同步的任务", corpEndpoint, companyId, tasks.size());
        if (tasks.size() == 0) {
            if (syncTaskGroupDao.updateAsFinishedStatus(group.getId(), group.getTaskGroupStatus()) > 0) {
                group.setTaskGroupStatus(TaskGroupStatus.Finished);
                log.info("{}-{} 任务组：{} 更新状态为：已完成", corpEndpoint, companyId, group.getId());
            }
            return;
        }

        //按优先级分组
        List<SyncTask> tasks_L1 = Lists.newArrayList();
        List<SyncTask> tasks_L2 = Lists.newArrayList();
        List<SyncTask> tasks_L3 = Lists.newArrayList();

        tasks.forEach(t -> {
            switch (t.getSyncPriority()) {
                case SyncPriority.L1:
                    tasks_L1.add(t);
                    break;
                case SyncPriority.L2:
                    tasks_L2.add(t);
                    break;
                case SyncPriority.L3:
                    tasks_L3.add(t);
                    break;
            }
        });

        if (!runTasksByPriority(corpEndpoint, companyId, group, SyncPriority.L1, tasks_L1))
            return;

        if (!runTasksByPriority(corpEndpoint, companyId, group, SyncPriority.L2, tasks_L2))
            return;

        if (!runTasksByPriority(corpEndpoint, companyId, group, SyncPriority.L3, tasks_L3))
            return;


        if (syncTaskGroupDao.updateAsFinishedStatus(group.getId(), group.getTaskGroupStatus()) > 0) {
            group.setTaskGroupStatus(TaskGroupStatus.Finished);
            log.info("{}-{} 任务组：{} 更新状态为：已完成", corpEndpoint, companyId, group.getId());
        }
    }

    /**
     * 执行同步任务
     */
    @Override
    public void runSyncTask(String corpEndpoint) throws Exception {
        if (StringUtils.isBlank(corpEndpoint))
            throw new RuntimeException("拉取变更失败，corpEndPoint为空");

        ApiResult apiResult = syncService.pullFromCorpServer(CorpServer.URL_GET_SYNC_COMPANY + "/" + corpEndpoint);
        if (!apiResult.isSuccessful())
            throw new RuntimeException("拉取同步组织失败：" + apiResult.getMsg());

        List<SyncCompanyDto_Select> scs = JsonUtils.json2list(JsonUtils.obj2json(apiResult.getData()), SyncCompanyDto_Select.class);
        if (scs == null || scs.size() == 0)
            return;

        if (scs == null || scs.size() == 0)
            return;

        scs.forEach(o -> {
            SyncCompanyDto_Select sc = o;
            //异步并发
            CompletableFuture.runAsync(() -> {
                try {
                    runTasksByCompany(sc.getCorpEndpoint(), sc.getCompanyId());
                } catch (Exception ex) {
                    log.error(String.format("%s-%s 执行同步任务发生异常", sc.getCorpEndpoint(), sc.getCompanyId()), ex);
                }
            });
        });
    }


    /**
     * 执行同步任务
     */
    @Override
    public void runUpdateStatusTask() {
        //获取组织
        syncTaskGroupDao.updateAsWaitRunningOldStatus(1);
    }
}
