package com.maoding.corp.module.corpserver.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.maoding.constDefine.corp.RKey;
import com.maoding.constDefine.corp.SyncCmd;
import com.maoding.core.base.BaseService;
import com.maoding.core.bean.ApiResult;
import com.maoding.corp.module.corpserver.dao.CollaborationDAO;
import com.maoding.corp.module.corpserver.dao.MyTaskDAO;
import com.maoding.corp.module.corpserver.dao.ProcessNodeDAO;
import com.maoding.corp.module.corpserver.dao.ProjectTaskDAO;
import com.maoding.corp.module.corpserver.dto.*;
import com.maoding.corp.module.corpserver.model.AccountDO;
import com.maoding.corp.module.corpserver.model.MyTaskDO;
import com.maoding.corp.module.corpserver.model.ProjectTaskDO;
import com.maoding.utils.MD5Helper;
import com.maoding.utils.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Idccapp21 on 2017/2/8.
 */
@Service("collaborationService")
public class CollaborationServiceImpl extends BaseService implements CollaborationService {

    private static final Logger log = LoggerFactory.getLogger(CollaborationServiceImpl.class);

    @Autowired
    private CollaborationDAO collaborationDao;

    @Autowired
    private MyTaskDAO myTaskDao;

    @Autowired
    private ProcessNodeDAO processNodeDao;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ProjectTaskDAO projectTaskDao;

    @Override
    public List<CoCompanyDTO> listCompanyByIds(List<String> companyIds) {
        return collaborationDao.listCompanyByIds(companyIds);
    }

    @Override
    public List<ProjectDTO> listProjectByCompanyId(String companyId, String syncDate) throws Exception {
        List<ProjectDTO> projects = collaborationDao.listProjectByCompanyId(companyId, null);
        return projects;
    }

    @Override
    public List<CoUserDTO> listUserByCompanyId(String companyId) {
        List<CoUserDTO> coUsers = collaborationDao.listUserByCompanyId(companyId);
        return coUsers;
    }


    @Override
    public ProjectDTO getProjectById(String projectId) {
        return collaborationDao.getProjectById(projectId);
    }


    /**
     * 获取项目节点（含任务成员状态信息）
     */
    @Override
    public List<CoProjectPhaseDTO> listNode(String companyId, String projectId) throws Exception {

        //读取协同组织
        Set<String> syncCompanies = Sets.newHashSet();
        try {
            RReadWriteLock cLock = redissonClient.getReadWriteLock("Lock_" + RKey.CorpCompanies);
            RLock r = cLock.readLock();
            r.lock(5, TimeUnit.SECONDS);
            RSet<String> set_companies = redissonClient.getSet(RKey.CorpCompanies);
            syncCompanies = set_companies.readAll();
            r.unlock();
        } catch (Exception e) {
            log.error("读取协同组织发生异常", e);
        }

        List<CoProjectPhaseDTO> result = Lists.newArrayList();
        List<ProjectTaskDO> tasks = collaborationDao.listProjectTask(projectId, null);
        if (tasks.size() > 0) {
            //筛选出阶段
            List<ProjectTaskDO> phases = tasks.stream().filter(t -> t.getTaskType() == 1).sorted(Comparator.comparingInt(ProjectTaskDO::getSeq)).collect(Collectors.toList());

            for (ProjectTaskDO phase : phases) {
                //项目阶段
                CoProjectPhaseDTO coPhase = new CoProjectPhaseDTO();
                coPhase.setStatus(phase.getTaskStatus().equals("0") ? 1 : 0);
                coPhase.setProjectPhaseId(phase.getId());
                coPhase.setProjectId(projectId);
                coPhase.setPhaseName(phase.getTaskName());
                coPhase.setLevel(phase.getTaskLevel());
                coPhase.setSeq(phase.getSeq());

                coPhase.setTasks(Lists.newArrayList());
                result.add(coPhase);

                //根据路径筛选阶段子任务(按层级排序）
                List<ProjectTaskDO> childTasks = tasks.stream().filter(t -> t.getTaskType() != 1 && StringUtils.startsWithIgnoreCase(t.getTaskPath(), phase.getId())).sorted(Comparator.comparingLong(c -> c.getTaskLevel() * 100000L + Timestamp.valueOf(c.getCreateDate()).getTime())).collect(Collectors.toList());

                if (childTasks.size() <= 0)
                    continue;

                for (ProjectTaskDO ct : childTasks) {
                    //创建子任务
                    CoTaskDTO coTask = new CoTaskDTO();
                    coTask.setStatus(ct.getTaskStatus().equals("0") ? 1 : 0);
                    coTask.setId(ct.getId());
                    coTask.setProjectPhaseID(phase.getId());
                    coTask.setCode(ct.getCompanyId());
                    coTask.setName(ct.getTaskName());
                    coTask.setLevel(ct.getTaskLevel());
                    coTask.setCompanyId(ct.getCompanyId());
                    coTask.setSeq(ct.getSeq());
                    if (ct.getTaskPid() != null && !ct.getTaskPid().equalsIgnoreCase(phase.getId()))
                        coTask.setParentID(ct.getTaskPid());

                    coTask.setMembers(Lists.newArrayList());
                    coPhase.addTask(coTask);

                    //填充同步组织任务成员
                    if (companyId.equalsIgnoreCase(ct.getCompanyId()) || syncCompanies.stream().anyMatch(c -> StringUtils.endsWithIgnoreCase(c, ":" + ct.getCompanyId())))
                        fillMember(coTask);
                }
            }
        }

        return result;
    }

    //填充任务成员
    private void fillMember(CoTaskDTO coTask) {
        //获取所有的流程节点
        List<CoProjectProcessDTO> processes = collaborationDao.listProjectProcessByTaskId(coTask.getId(), null);
        for (CoProjectProcessDTO process : processes) {

            //处理设计人员
            List<CoProjectProcessNodeDTO> ppNodes = processNodeDao.listProcessNodeByProcessId(process.getId(), null);
            for (CoProjectProcessNodeDTO node : ppNodes) {

                CoTaskMemberDTO coMember = new CoTaskMemberDTO();
                coMember.setId(node.getAccountId());
                coMember.setRole(node.getNodeName());
                coMember.setPeerID(node.getId());
                coMember.setState(StringUtils.isBlank(node.getCompleteTime()) ? 0 : 1);

                coTask.addMember(coMember);
            }

            //处理任务负责人
            CoUserDTO user = collaborationDao.getTaskPrincipal(coTask.getId());
            if (user != null) {
                CoTaskMemberDTO coMember = new CoTaskMemberDTO();
                coMember.setId(user.getAccountId());
                coMember.setRole("任务负责人");
                coMember.setPeerID("");
                coMember.setState(0);

                coTask.addMember(coMember);
            }
        }
    }

    @Override
    public ApiResult login(Map<String, Object> map) throws Exception {
        String loginname = map.get("loginName").toString();
        String password = MD5Helper.getMD5For32(map.get("password").toString());
        AccountDO user = collaborationDao.getAccountByCellphone(loginname);
        if (user == null) {
            return ApiResult.failed("用户不存在！", null);
        }
        if (user.getPassword().equals(password)) {
//            String userId = user.getId();
//            String token = TokenProcessor.getInstance().generateTokeCode(request);
//            Map<String,Object> resultMap=new HashMap<String,Object>();
//            resultMap.put()
            return ApiResult.success("登录成功！", null);

        } else {
            return ApiResult.failed("用户名或密码错误！", null);
        }
    }

    /**
     * 完成我的任务
     */
    @Override
    public ApiResult finishMyTask(String processNodeId) {
        CoProjectProcessNodeDTO node = processNodeDao.getProcessNodeById(processNodeId);
        if (node == null)
            return ApiResult.failed("设校审节点不存在！", null);
        if (!StringUtils.isBlank(node.getCompleteTime()))
            return ApiResult.success("设校审节点原来已是完成状态！", null);

        MyTaskDO myTask = myTaskDao.getMyTaskByProcessNodeId(processNodeId);
        if (myTask == null)
            return ApiResult.failed("设校审任务不存在！", null);
        if (myTask.getStatus().equals("1"))
            return ApiResult.success("设校审任务原来已是完成状态！", null);

        int r1 = myTaskDao.updateMyTaskAsFinished(myTask.getId());
        int r2 = processNodeDao.updateProcessNodeAsFinished(processNodeId);

        return ApiResult.success("节点成功设为已完成！", null);
    }

    /**
     * 激活我的任务
     */
    @Override
    public ApiResult activeMyTask(String processNodeId) {
        CoProjectProcessNodeDTO node = processNodeDao.getProcessNodeById(processNodeId);
        if (node == null)
            return ApiResult.failed("设校审节点不存在！", null);
        if (StringUtils.isBlank(node.getCompleteTime()))
            return ApiResult.success("设校审节点原来已是未完成状态！", null);

        MyTaskDO myTask = myTaskDao.getMyTaskByProcessNodeId(processNodeId);
        if (myTask == null)
            return ApiResult.failed("设校审任务不存在！", null);
        if (myTask.getStatus().equals("0"))
            return ApiResult.success("设校审任务原来已是未完成状态！", null);

        ProjectTaskDO pTask = projectTaskDao.selectByPrimaryKey(myTask.getParam1());
        if (pTask == null)
            return ApiResult.failed("设校审任务所在生产节点不存在！", null);

        ProjectTaskDO pTaskParent = projectTaskDao.selectByPrimaryKey(pTask.getTaskPid());
        if (pTaskParent != null && pTaskParent.getCompleteDate() != null)
            return ApiResult.failed("父生产节点已处于已完成状态，不允许激活！", null);

        //任务务负责人
        CoUserDTO taskPrincipal = collaborationDao.getTaskPrincipal(pTask.getId());

        //是否任务负责人
        if (taskPrincipal != null && taskPrincipal.getAccountId().equals(node.getAccountId())) {

            if (pTask.getCompleteDate() != null) {

                //生产根任务
                if (pTask.getTaskType() == 2 && pTaskParent != null) {
                    MyTaskDO mt = new MyTaskDO();
                    mt.setTargetId(pTaskParent.getId());
                    mt.setStatus("0");
                    mt.setTaskType(22);
                    MyTaskDO rootDesignTask = myTaskDao.selectOne(mt);
                    if (rootDesignTask != null) {
                        //无效化设计负责人的任务
                        myTaskDao.updateMyTaskAsInvalid(rootDesignTask.getId());
                    }
                }

                //激活生产任务
                projectTaskDao.updateProcessTaskAsActived(pTask.getId());
            }

            //激活设校审节点
            processNodeDao.updateProcessNodeAsActived(processNodeId);

            //激活任务负责人任务
            MyTaskDO principalTask = myTaskDao.getPrincipalTaskByProjectTaskId(pTask.getId());
            if (principalTask != null)
                myTaskDao.updateMyTaskAsActived(principalTask.getId());

            //激活设校审任务
            myTaskDao.updateMyTaskAsActived(myTask.getId());

        } else {
            if (pTask.getCompleteDate() != null) {
                return ApiResult.failed("生产任务已完成，不允许激活！", null);
            }

            //激活设校审节点
            processNodeDao.updateProcessNodeAsActived(processNodeId);

            //激活设校审任务
            myTaskDao.updateMyTaskAsActived(myTask.getId());
        }

        //推送同步指令
        pushChanges(pTask.getCompanyId(), SyncCmd.PT, pTask.getProjectId());
        return ApiResult.success("节点成功设为未完成！", null);
    }

    //推送同步指令
    @Override
    public void pushChanges(String companyId, String syncCmd, String projectId) {
        CompletableFuture.runAsync(() -> {
            try {
                //读取协同组织
                RReadWriteLock cLock = redissonClient.getReadWriteLock("Lock_" + RKey.CorpCompanies);
                RLock r = cLock.readLock();
                r.lock(5, TimeUnit.SECONDS);
                RSet<String> set_companies = redissonClient.getSet(RKey.CorpCompanies);
                Set<String> companies = set_companies.readAll();
                r.unlock();

                if (companies.size() == 0)
                    return;

                //匹配组织
                List<String> matches = Lists.newArrayList();
                companies.forEach(c -> {
                    if (StringUtils.endsWithIgnoreCase(c, ":" + companyId))
                        matches.add(c);
                });

                if (matches.size() == 0)
                    return;

                //写入变更
                matches.forEach(sc -> {

                    String setName = RKey.CorpChanges + ":" + sc;
                    RLock sLock = redissonClient.getLock("Lock_" + setName);
                    sLock.lock(5, TimeUnit.SECONDS);

                    RSet<Object> changes = redissonClient.getSet(setName);

                    String change;
                    if (StringUtils.equalsIgnoreCase(syncCmd, SyncCmd.ALL) || StringUtils.equalsIgnoreCase(syncCmd, SyncCmd.CU))
                        change = syncCmd;
                    else
                        change = syncCmd + ":" + projectId;

                    changes.add(change);

                    log.debug("{} 添加协同变更: {}", setName, change);

                    sLock.unlock();

                });

            } catch (Exception e) {
                log.error("推送协同变更发生异常", e);
                //TODO 考虑失败重试
            }
        });
    }
}
