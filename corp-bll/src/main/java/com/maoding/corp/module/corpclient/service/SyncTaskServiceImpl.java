package com.maoding.corp.module.corpclient.service;

import com.google.common.collect.Maps;
import com.maoding.constDefine.corp.SyncCmd;
import com.maoding.constDefine.corp.SyncPriority;
import com.maoding.constDefine.corp.SyncStatus;
import com.maoding.constDefine.corp.TaskStatus;
import com.maoding.core.base.BaseService;
import com.maoding.core.bean.ApiResult;
import com.maoding.corp.module.corpclient.dao.SyncTaskDAO;
import com.maoding.corp.module.corpclient.model.SyncTaskDO;
import com.maoding.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Wuwq on 2017/2/14.
 */
@Service("syncTaskService")
public class SyncTaskServiceImpl extends BaseService implements SyncTaskService {

    private static final Logger log = LoggerFactory.getLogger(SyncTaskServiceImpl.class);

    @Autowired
    private SyncTaskDAO syncTaskDao;

    @Autowired
    private CoService coService;

    @Autowired
    private SowService sowService;

    /**
     * 同步协同占用
     */
    @Override
    public ApiResult syncCorpSizeByCompanyId(String companyId) {
        try {
            //协同占用空间同步
            ApiResult ar = sowService.getCorpSizeByCompanyId(companyId);
            if (ar.isSuccessful()) {
                Long corpSize = Math.round((double) ar.getData());
                Map<String, Object> param = Maps.newHashMap();
                param.put("companyId", companyId);
                param.put("corpSize", corpSize.toString());
                coService.updateCorpSizeOnCompanyDisk(param);
                return ApiResult.success(null, null);
            }
        } catch (Exception e) {
            log.error(String.format("组织：%s 同步协同占用发生异常（syncCorpSizeByCompanyId）", companyId), e);
        }
        return ApiResult.failed(null, null);
    }

    /**
     * 同步一个任务
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void runOneTask(String syncTaskId) {
        //重新从数据库加载
        SyncTaskDO task = syncTaskDao.selectByPrimaryKey(syncTaskId);

        //筛选待同步、待重试
        int syncStatus = task.getSyncStatus();

        String logPrefix;
        if (SyncCmd.CU.equalsIgnoreCase(task.getSyncCmd()) || SyncCmd.CD.equalsIgnoreCase(task.getSyncCmd())) {
            logPrefix = String.format("端点 %s 组织 %s 任务（%s - %s）", task.getCorpEndpoint(), task.getCompanyId(), task.getSyncCmd(), task.getId());
        } else if (SyncCmd.PALL.equalsIgnoreCase(task.getSyncCmd()) || SyncCmd.PU.equalsIgnoreCase(task.getSyncCmd())) {
            logPrefix = String.format("端点 %s 项目 %s 任务（%s - %s）", task.getCorpEndpoint(), task.getProjectId(), task.getSyncCmd(), task.getId());
        } else {
            logPrefix = String.format("端点 %s 项目 %s 阶段：%s 任务（%s - %s）", task.getCorpEndpoint(), task.getProjectId(), task.getSyncCmd(), task.getProjectPhaseId(), task.getId());
        }

        if (syncStatus == SyncStatus.WaitSync || syncStatus == SyncStatus.WaitRetry) {

            task.setLastEntry(LocalDateTime.now());

            //重试次数+1
            if (syncStatus == SyncStatus.WaitRetry) {
                task.setRetryTimes(task.getRetryTimes() + 1);
                log.info("{}：第 {} 次重试", logPrefix, task.getRetryTimes());
            } else {
                log.info("{}：开始执行", logPrefix);
            }

            boolean isOk = false;
            try {

                if (log.isDebugEnabled())
                    log.debug("{}：同步开始", logPrefix);

                if (SyncCmd.CU.equalsIgnoreCase(task.getSyncCmd())) {

                    ApiResult apiResult = sowService.pushUserByCompanyId(task.getCompanyId(), task.getSyncPoint());
                    isOk = apiResult.isSuccessful();
                } else if (SyncCmd.CD.equalsIgnoreCase(task.getSyncCmd())) {
                    syncCorpSizeByCompanyId(task.getCompanyId());
                    //无论如何，同步协同空间都算成功，避免影响正常逻辑
                    isOk = true;
                } else if (SyncCmd.PALL.equalsIgnoreCase(task.getSyncCmd())) {

                    ApiResult apiResult = sowService.setProject(task.getProjectId(), task.getSyncPoint(), true);
                    isOk = apiResult.isSuccessful();

                } else if (SyncCmd.PU.equalsIgnoreCase(task.getSyncCmd())) {

                    ApiResult apiResult = sowService.setProject(task.getProjectId(), task.getSyncPoint(), false);
                    isOk = apiResult.isSuccessful();

                } else if (SyncCmd.PT.equalsIgnoreCase(task.getSyncCmd())) {

                    ApiResult apiResult = sowService.setProjectNodes(task.getProjectId(), task.getSyncPoint(), task.getProjectPhaseId());
                    isOk = apiResult.isSuccessful();
                }

                if (log.isDebugEnabled())
                    log.debug("{}：同步结束", logPrefix);

            } catch (Exception e) {
                log.error(String.format("%s：执行同步发生异常", logPrefix), e);
            }

            if (isOk) {
                task.setSyncStatus(SyncStatus.SyncSuccess);
                task.setTaskStatus(TaskStatus.Finished);
                if (syncTaskDao.updateWithOptimisticLock(task) > 0)
                    log.info("{}：同步成功，状态更新为：已结束", logPrefix);
            } else {

                if (task.getRetryTimes() > 10) {
                    task.setSyncStatus(SyncStatus.SyncFailed);
                    if (syncTaskDao.updateWithOptimisticLock(task) > 0)
                        log.info("{}：重试超出次数，同步失败，状态更新为：已结束", logPrefix);
                } else {
                    task.setSyncStatus(SyncStatus.WaitRetry);
                    if (syncTaskDao.updateWithOptimisticLock(task) > 0)
                        log.info("{}：同步失败，待重试", logPrefix);

                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {

                    }

                    SyncTaskService srv = (SyncTaskService) AopContextCurrentProxy();
                    srv.runOneTask(syncTaskId);
                }
            }
        } else {
            task.setTaskStatus(TaskStatus.Finished);
            if (syncTaskDao.updateWithOptimisticLock(task) > 0)
                log.info("{}：状态更新为：已结束", logPrefix);
        }
    }

    /**
     * 执行指定优先级的任务
     */
    private boolean runTasksByPriority(String endpoint, int syncPriority, List<SyncTaskDO> tasks) {
        if (tasks.size() > 0) {
            log.info("端点 {} 开始执行 {} 级同步任务", endpoint, syncPriority);

            SyncTaskService srv = (SyncTaskService) AopContextCurrentProxy();

            if (syncPriority == SyncPriority.L3) {

                Map<String, List<SyncTaskDO>> groups = tasks.stream().collect(Collectors.groupingBy(t -> t.getProjectId()));

                CountDownLatch latch = new CountDownLatch(groups.size());
                Semaphore sp = new Semaphore(3);

                groups.forEach((k, v) -> {
                    CompletableFuture.runAsync(() -> {
                        try {
                            sp.acquire();

                            v.forEach(t -> {
                                try {
                                    srv.runOneTask(t.getId());
                                } catch (Exception e) {
                                    log.error(String.format("端点 {} 执行 %s 级同步任务：%s 发生异常#1", endpoint, syncPriority, t.getId()), e);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            sp.release();
                            latch.countDown();
                        }
                    });
                });

                try {
                    //组塞线程（超时控制按每个三分钟来算)
                    latch.await(tasks.size() * 2, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    log.error(String.format("端点 {} 执行 %s 级同步任务 发生异常#2", endpoint, syncPriority), e);
                }

            } else {

                CountDownLatch latch = new CountDownLatch(tasks.size());
                Semaphore sp = new Semaphore(3);

                tasks.forEach(t -> {
                    SyncTaskDO task = t;
                    //异步并发
                    CompletableFuture.runAsync(() -> {
                        try {
                            sp.acquire();

                            //执行同步逻辑
                            srv.runOneTask(t.getId());

                        } catch (InterruptedException e) {
                            log.error(String.format("端点 {} 执行 %s 级同步任务：%s 发生异常#3", endpoint, syncPriority, task.getId()), e);
                        } finally {
                            sp.release();
                            latch.countDown();
                        }
                    });
                });

                try {
                    //组塞线程（超时控制按每个三分钟来算)
                    latch.await(tasks.size() * 2, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    log.error(String.format("端点 {} 执行 %s 级同步任务 发生异常#4", endpoint, syncPriority), e);
                }

            }

            //判断同步任务是否还存在未完成任务，若存在则返回等待下一次重试
            if (tasks.stream().filter(t -> t.getTaskStatus() != TaskStatus.Finished).count() > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 执行指定协同端下的任务
     */
    private void runTasks(String corpEndpoint) {
        /*//判断状态是否为执行中
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
        }*/
    }

    /**
     * 执行同步任务
     */
    @Override
    public void runSyncTask(String endpoint) throws Exception {
        if (StringUtils.isBlank(endpoint))
            throw new RuntimeException("拉取变更失败，corpEndPoint为空");

        //查询未完成任务
        List<SyncTaskDO> tasks = syncTaskDao.listUnfinishedTask(endpoint);
        List<SyncTaskDO> cuTasks = tasks.stream().filter(t -> t.getSyncCmd().equalsIgnoreCase(SyncCmd.CU) || t.getSyncCmd().equalsIgnoreCase(SyncCmd.CD)).collect(Collectors.toList());
        if (!runTasksByPriority(endpoint, SyncPriority.L1, cuTasks))
            return;

        List<SyncTaskDO> puTasks = tasks.stream().filter(t -> t.getSyncCmd().equalsIgnoreCase(SyncCmd.PALL) || t.getSyncCmd().equalsIgnoreCase(SyncCmd.PU)).collect(Collectors.toList());
        if (!runTasksByPriority(endpoint, SyncPriority.L2, puTasks))
            return;

        List<SyncTaskDO> ptTasks = tasks.stream().filter(t -> t.getSyncCmd().equalsIgnoreCase(SyncCmd.PT)).collect(Collectors.toList());
        if (!runTasksByPriority(endpoint, SyncPriority.L3, ptTasks))
            return;
    }


    /**
     * 执行同步任务
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @Override
    public void resetTaskStatusOnStartup() {
        syncTaskDao.updateRunngingAsWaitRunningStatus();
    }
}
