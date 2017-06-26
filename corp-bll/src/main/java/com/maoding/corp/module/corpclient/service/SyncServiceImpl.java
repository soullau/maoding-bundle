package com.maoding.corp.module.corpclient.service;

import com.google.common.collect.Lists;
import com.maoding.constDefine.corp.*;
import com.maoding.core.base.BaseService;
import com.maoding.core.bean.ApiResult;
import com.maoding.corp.config.CorpClientConfig;
import com.maoding.corp.module.corpclient.dao.SyncTaskDAO;
import com.maoding.corp.module.corpclient.dao.SyncTaskGroupDAO;
import com.maoding.corp.module.corpclient.dto.PushResult;
import com.maoding.corp.module.corpclient.model.SyncTaskDO;
import com.maoding.corp.module.corpclient.model.SyncTaskGroupDO;
import com.maoding.corp.module.corpserver.dto.SyncCompanyDTO_Select;
import com.maoding.utils.GsonUtils;
import com.maoding.utils.JsonUtils;
import com.maoding.utils.OkHttpUtils;
import com.maoding.utils.StringUtils;
import okhttp3.Response;
import org.redisson.api.RLock;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by Wuwq on 2017/05/24.
 */
@Service("syncService")
public class SyncServiceImpl extends BaseService implements SyncService {

    private static final Logger log = LoggerFactory.getLogger(SyncServiceImpl.class);

    @Autowired
    private SyncTaskGroupDAO syncTaskGroupDao;

    @Autowired
    private SyncTaskDAO syncTaskDao;

    //@Autowired
    //private SyncCompanyDao syncCompanyDao;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 拉取变更
     */
    @Override
    public void pullChanges(String corpEndpoint) throws Exception {
        if (StringUtils.isBlank(corpEndpoint))
            throw new RuntimeException("拉取变更失败，corpEndPoint为空");

        ApiResult apiResult = pullFromCorpServer(CorpServer.URL_GET_SYNC_COMPANY + "/" + corpEndpoint);
        if (!apiResult.isSuccessful())
            throw new RuntimeException("拉取同步组织失败：" + apiResult.getMsg());

        List<SyncCompanyDTO_Select> scs = JsonUtils.json2list(JsonUtils.obj2json(apiResult.getData()), SyncCompanyDTO_Select.class);
        if (scs == null || scs.size() == 0)
            return;

        SyncService srv = (SyncService) AopContextCurrentProxy();

        //信号量控制
        Semaphore sp = new Semaphore(2);

        //遍历
        scs.forEach((SyncCompanyDTO_Select o) -> {

            SyncCompanyDTO_Select sc = o;

            //异步并发
            CompletableFuture.runAsync(() -> {

                try {
                    sp.acquire();
                } catch (InterruptedException e) {
                    log.error("", e);
                }

                srv.pullChangesByCompany(sc.getCorpEndpoint(), sc.getCompanyId());

                sp.release();

            });
        });
    }

    /**
     * 拉取指定组织的变更
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void pullChangesByCompany(String corpEndpoint, String companyId) {

        String setName = RKey.CorpChanges + ":" + corpEndpoint + ":" + companyId;

        //并发锁
        RLock lock = null;

        try {

            //log.info("{}-{} 开始拉取变更", corpEndpoint, companyId);

            lock = redissonClient.getLock("Lock_" + setName);
            lock.lock(10, TimeUnit.SECONDS);

            RSet<String> set = redissonClient.getSet(setName);
            Set<String> changes = set.readAll();

            if (changes.size() > 0) {

                log.info("{}-{} 发现 {} 个变更", corpEndpoint, companyId, changes.size());

                //同步时间点附加10分钟误差，防止服务器端时间不同步
                LocalDateTime syncPoint = LocalDateTime.now().plusMinutes(10);

                //创建任务组
                SyncTaskGroupDO group = new SyncTaskGroupDO();
                group.initEntity();
                group.setCorpEndpoint(corpEndpoint);
                group.setCompanyId(companyId);
                group.setSyncPoint(syncPoint);
                group.setTaskGroupStatus(TaskGroupStatus.WaitRuning);

                ArrayList<SyncTaskDO> tasks = Lists.newArrayList();

                //判断是否存在ALL变更
                if (changes.stream().anyMatch(c -> c.equalsIgnoreCase(SyncCmd.ALL))) {
                    SyncTaskDO cuTask = new SyncTaskDO();
                    cuTask.initEntity();
                    cuTask.setTaskGroupId(group.getId());
                    cuTask.setCorpEndpoint(corpEndpoint);
                    cuTask.setCompanyId(companyId);
                    cuTask.setSyncPoint(syncPoint);
                    cuTask.setSyncCmd(SyncCmd.ALL);

                    //优先级最高
                    cuTask.setSyncPriority(SyncPriority.L1);
                    cuTask.setSyncStatus(SyncStatus.WaitSync);
                    cuTask.setTaskStatus(TaskStatus.WaitRuning);

                    tasks.add(cuTask);
                } else {


                    for (String c : changes) {
                        //判断是不是CU变更
                        if (StringUtils.equalsIgnoreCase(c, SyncCmd.CU)) {

                            SyncTaskDO cuTask = new SyncTaskDO();
                            cuTask.initEntity();
                            cuTask.setTaskGroupId(group.getId());
                            cuTask.setCorpEndpoint(corpEndpoint);
                            cuTask.setCompanyId(companyId);
                            cuTask.setSyncPoint(syncPoint);
                            cuTask.setSyncCmd(SyncCmd.CU);
                            //优先级最高
                            cuTask.setSyncPriority(SyncPriority.L1);
                            cuTask.setSyncStatus(SyncStatus.WaitSync);
                            cuTask.setTaskStatus(TaskStatus.WaitRuning);

                            tasks.add(cuTask);

                            continue;
                        }

                        String[] splits = StringUtils.split(c, ":");
                        String changeType = splits[0];
                        String projectId = splits[1];

                        SyncTaskDO task = new SyncTaskDO();
                        task.initEntity();
                        task.setTaskGroupId(group.getId());
                        task.setCorpEndpoint(corpEndpoint);
                        task.setCompanyId(companyId);
                        task.setProjectId(projectId);
                        task.setSyncPoint(syncPoint);
                        task.setSyncStatus(SyncStatus.WaitSync);
                        task.setTaskStatus(TaskStatus.WaitRuning);

                        switch (changeType.toUpperCase()) {
                            case SyncCmd.PU:
                                task.setSyncCmd(SyncCmd.PU);
                                task.setSyncPriority(SyncPriority.L2);
                                break;
                            case SyncCmd.PT:
                                task.setSyncCmd(SyncCmd.PT);
                                task.setSyncPriority(SyncPriority.L3);
                                break;
                            case SyncCmd.PF:
                                task.setSyncCmd(SyncCmd.PF);
                                task.setSyncPriority(SyncPriority.L3);
                                break;
                        }

                        // 暂时排除PF变更
                        if (!task.getSyncCmd().equalsIgnoreCase(SyncCmd.PF))
                            tasks.add(task);
                    }
                }

                group.setTaskAmount(tasks.size());

                log.info("{}-{} 正在将变更写入本地数据库", corpEndpoint, companyId);
                //写入数据库
                if (syncTaskGroupDao.insert(group) == 1) {
                    if (syncTaskDao.BatchInsert(tasks) == tasks.size()) {
                        log.info("{}-{} 正在从Redis移除已拉取变更", corpEndpoint, companyId);
                        //删除从Redis拉取的数据
                        set.removeAll(changes);
                    }
                }

                log.info("{}-{} 变更拉取完成", corpEndpoint, companyId, changes.size());
            }
        } catch (Exception e) {
            log.error(String.format("%s-%s 拉取变更发生异常", corpEndpoint, companyId), e);
            throw new RuntimeException(String.format("%s-%s 拉取变更发生异常", corpEndpoint, companyId), e);
        } finally {
            if (lock != null)
                lock.unlock();
        }
    }


    /**
     * 从业务系统拉取数据（GET）
     */
    public ApiResult pullFromCorpServer(String url) {
        log.info("pullFromCorpServer {}", CorpClientConfig.CorpServer + url);


        Response res = null;
        try {
            res = OkHttpUtils.get(CorpClientConfig.CorpServer + url);
        } catch (IOException e) {
            throw new RuntimeException("请求CorpServer失败");
        }

        if (res.isSuccessful()) {
            try {
                return JsonUtils.json2pojo(res.body().string(), ApiResult.class);
            } catch (Exception e) {
                log.error("pullFromCorpServer 解析返回结果失败", e);
            }
        }
        try {
            return JsonUtils.json2pojo(res.body().string(), ApiResult.class);
        } catch (Exception e) {
            return ApiResult.failed(res.message(), null);
        }
    }

    /**
     * 从业务系统拉取数据（POST）
     */
    public ApiResult pullFromCorpServer(Map<String, Object> param, String url) {

        Response res = null;
        try {
            if (log.isDebugEnabled())
                log.debug("pullFromCorpServer {} {}", CorpClientConfig.CorpServer + url, JsonUtils.obj2json(param));
            else
                log.info("pullFromCorpServer {}", CorpClientConfig.CorpServer + url);
            res = OkHttpUtils.postJson(CorpClientConfig.CorpServer + url, param);
        } catch (IOException e) {
            throw new RuntimeException("请求CorpServer失败");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (res.isSuccessful()) {
            try {
                return JsonUtils.json2pojo(res.body().string(), ApiResult.class);
            } catch (Exception e) {
                log.error("pullFromCorpServer 解析返回结果失败", e);
            }
        }

        try {
            return JsonUtils.json2pojo(res.body().string(), ApiResult.class);
        } catch (Exception e) {
            return ApiResult.failed(res.message(), null);
        }
    }


    /**
     * 推送到对方协同服务端
     */
    public PushResult postToSOWServer(Object param, String url) throws Exception {
        if (log.isDebugEnabled())
            log.debug("postToSOWServer {} {}", CorpClientConfig.SowServer + url, JsonUtils.obj2json(param));
        else
            log.info("postToSOWServer {}", CorpClientConfig.SowServer + url);
        Response res = OkHttpUtils.postJson(CorpClientConfig.SowServer + url, param);
        if (res.isSuccessful()) {
            try {
                PushResult pushResult = GsonUtils.fromJson(res.body().string(), PushResult.class);
                return pushResult;
            } catch (Exception e) {
                log.error("postToSOWServer 解析返回结果失败", e);
            }
        }
        return PushResult.failed();
    }
}
