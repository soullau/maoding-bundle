package com.maoding.corpbll.module.corpclient.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.maoding.core.base.BaseService;
import com.maoding.core.bean.ApiResult;
import com.maoding.corpbll.constDefine.CorpServer;
import com.maoding.corpbll.constDefine.SowServer;
import com.maoding.corpbll.module.corpclient.dto.*;
import com.maoding.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Created by Wuwq on 2017/4/24.
 */
@Service("coService")
public class CoServiceImpl extends BaseService implements CoService {

    private static final Logger log = LoggerFactory.getLogger(CoServiceImpl.class);

    @Autowired
    private SyncService syncService;

    /**
     * 登录接口
     */
    @Override
    public ApiResult login(Map<String, Object> param) throws Exception {
        ApiResult apiResult = syncService.pullFromCorpServer(param, CorpServer.URL_LOGIN);
        if (apiResult.isSuccessful()) {
            return apiResult;
        }
        return ApiResult.failed(null, null);
    }

    /**
     * 设置节点完成状态
     */
    @Override
    public ApiResult handleMyTaskByProjectNodeId(Map<String, Object> param) throws Exception {
        ApiResult apiResult = syncService.pullFromCorpServer(param, CorpServer.URL_HANDLE_MY_TASK_BY_PROJECT_NODE_ID);
        return apiResult;
    }

    /**
     * 根据组织Id获取人员信息
     */
    @Override
    public ApiResult pushUserByCompanyId(String companyId, LocalDateTime syncDate) throws Exception {
        Map<String, Object> param = Maps.newHashMap();
        param.put("companyId", companyId);
        if (syncDate != null)
            param.put("syncDate", syncDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        ApiResult apiResult = syncService.pullFromCorpServer(param, CorpServer.URL_LIST_USER_BY_COMPANY_ID);
        if (apiResult.isSuccessful()) {

            // SetUsers(int[] statuss, string[] ids, string[] names, string[] loginIds, string[] passwords = null, string[] descriptions = null, string[] specialtyIds = null)

            List<CoUser> users = JsonUtils.json2list(JsonUtils.obj2json(apiResult.getData()), CoUser.class);

            if (users.size() == 0)
                return ApiResult.success(null, null);


            List<String> statuss = Lists.newArrayList();
            List<String> ids = Lists.newArrayList();
            List<String> names = Lists.newArrayList();
            List<String> loginIds = Lists.newArrayList();

            for (int i = 0; i < users.size(); i++) {
                CoUser user = users.get(i);
                statuss.add(user.getStatus().equals("0") ? "1" : "0");
                ids.add(user.getAccountId());
                names.add(user.getAccountName());
                loginIds.add(user.getCellphone());
            }

            CoUsers coUsers = new CoUsers();
            coUsers.setStatuss(statuss);
            coUsers.setIds(ids);
            coUsers.setNames(names);
            coUsers.setLoginIds(loginIds);

            //推送到对方协同服务端
            PushResult pushResult = syncService.pushToSOWServer(coUsers, SowServer.URL_SET_USERS);
            if (!pushResult.isSuccessful()) {
                log.error("setUsers 失败 ，postData:{} pushResult:{}", JsonUtils.json2map(JsonUtils.obj2json(coUsers)), JsonUtils.obj2json(pushResult));
            }

            return ApiResult.success(null, null);
        }
        return ApiResult.failed(null, null);
    }

    /**
     * 通过组织Id获取组织的项目
     */
    @Override
    public ApiResult pushProjectByCompanyId(String companyId, LocalDateTime syncDate) throws Exception {
        Map<String, Object> param = Maps.newHashMap();
        param.put("companyId", companyId);
        if (syncDate != null)
            param.put("syncDate", syncDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        ApiResult apiResult = syncService.pullFromCorpServer(param, CorpServer.URL_LIST_PROJECT_BY_COMPANY_ID);
        if (apiResult.isSuccessful()) {
            List<ProjectDTO> projects = JsonUtils.json2list(JsonUtils.obj2json(apiResult.getData()), ProjectDTO.class);

            for (ProjectDTO p : projects) {
                CoProject coProject = new CoProject();
                coProject.setId(p.getProjectId());
                coProject.setCode(p.getPstatus() + "nnn");
                coProject.setName(p.getProjectName());
                //coProject.setOwerUserId(p.getCreateBy());
                coProject.setStatus(p.getPstatus().equals("0") ? 1 : 0);

                //推送到对方协同服务端
                PushResult pushResult = syncService.pushToSOWServer(coProject, SowServer.URL_SET_PROJECT);
                if (pushResult.isSuccessful()) {

                    //判断项目是否有效，推送子节点信息
                    if (coProject.getStatus() == 1)
                        setProjectNodes(companyId, p.getProjectId(), syncDate);

                } else {
                    log.error("setProject 失败 ，postData:{} pushResult:{}", JsonUtils.obj2json(coProject), JsonUtils.obj2json(pushResult));
                }
            }

            return ApiResult.success(null, null);
        }
        return ApiResult.failed(null, null);
    }

    /**
     * 设置项目
     */
    @Override
    public ApiResult setProject(String companyId, String projectId, LocalDateTime syncDate) throws Exception {
       /* Map<String, Object> param = Maps.newHashMap();
        param.put("companyId", companyId);
        param.put("projectId", projectId);
        if (syncDate != null)
            param.put("syncDate", syncDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));*/
        ApiResult apiResult = syncService.pullFromCorpServer(CorpServer.URL_GET_PROJECT_BY_ID + '/' + projectId);
        if (!apiResult.isSuccessful()) {
            log.error("拉取项目（{}）失败：{}", projectId, apiResult.getMsg());
            return ApiResult.failed(null, null);
        }

        ProjectDTO resultDTO = JsonUtils.obj2pojo(apiResult.getData(), ProjectDTO.class);

        CoProject coProject = new CoProject();
        coProject.setId(resultDTO.getProjectId());
        coProject.setCode(resultDTO.getPstatus() + "nnn");
        coProject.setName(resultDTO.getProjectName());
           /* if (!StringUtils.isBlank(resultDTO.getCreateBy()))
                coProject.setOwerUserId(resultDTO.getCreateBy());*/
        coProject.setStatus(resultDTO.getPstatus().equals("0") ? 1 : 0);

        //推送到对方协同服务端
        PushResult pushResult = syncService.pushToSOWServer(coProject, SowServer.URL_SET_PROJECT);
        if (pushResult.isSuccessful()) {

            //判断项目是否有效，推送子节点信息
            if (coProject.getStatus() == 1)
                return setProjectNodes(companyId, projectId, syncDate);

            return ApiResult.success(null, null);

        } else {
            log.error("setProject 失败 ，postData:{} pushResult:{}", JsonUtils.obj2json(coProject), JsonUtils.obj2json(pushResult));
            return ApiResult.failed(null, null);
        }
    }

    /**
     * 设置项目节点（含任务成员状态信息）
     */
    @Override
    public ApiResult setProjectNodes(String companyId, String projectId, LocalDateTime syncDate) throws Exception {
        Map<String, Object> param = Maps.newHashMap();
        param.put("companyId", companyId);
        param.put("projectId", projectId);
        if (syncDate != null)
            param.put("syncDate", syncDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        ApiResult apiResult = syncService.pullFromCorpServer(param, CorpServer.URL_LIST_NODE);
        if (apiResult.isSuccessful()) {
            List<CoProjectPhase> nodes = JsonUtils.json2list(JsonUtils.obj2json(apiResult.getData()), CoProjectPhase.class);
            if (nodes.size() > 0) {
                for (CoProjectPhase node : nodes) {

                    //项目阶段
                    CoProjectPhase coPhase = new CoProjectPhase();
                    BeanUtils.copyProperties(node, coPhase);
                    coPhase.setTasks(null);

                    //推送阶段
                    PushResult pushResult = syncService.pushToSOWServer(coPhase, SowServer.URL_SET_PROJECT_PHASE);
                    if (pushResult.isSuccessful()) {

                        //判断阶段是否有效，推送子任务
                        if (coPhase.getStatus() == 1)
                            setTasks(projectId, node.getTasks());

                    } else {
                        log.error("setProjectPhase 失败 ，postData:{} pushResult:{}", JsonUtils.obj2json(coPhase), JsonUtils.obj2json(pushResult));
                    }
                }
                //发布项目（同步任务文件夹）
                return publicProject(projectId);
            }
        }
        return ApiResult.failed(null, null);
    }

    /**
     * 设置任务
     */
    private ApiResult setTasks(String projectId, List<CoTask> coTasks) throws Exception {
        if (coTasks == null || coTasks.size() == 0)
            return ApiResult.success(null, null);

        Map<String, Object> param = Maps.newHashMap();
        param.put("tasks", coTasks);

        //推送到对方协同服务端
        PushResult pushResult = syncService.pushToSOWServer(param, SowServer.URL_SET_PROJECT_TASKS);
        if (pushResult.isSuccessful()) {
            return ApiResult.success(null, null);
        } else {
            log.error("setTasks 失败 ，projectId:{} postData:{} pushResult:{}", projectId, JsonUtils.obj2json(param), JsonUtils.obj2json(pushResult));
        }

        return ApiResult.failed(null, null);
    }

    /**
     * 发布项目（同步任务文件夹）
     */
    public ApiResult publicProject(String projectId) throws Exception {
        Map<String, Object> param = Maps.newHashMap();
        param.put("projectId", projectId);

        //推送到对方协同服务端
        PushResult pushResult = syncService.pushToSOWServer(param, SowServer.URL_PUBLIC_PROJECT);
        if (pushResult.isSuccessful()) {
            return ApiResult.success(null, null);
        } else {
            log.error("publicProject 失败 ，projectId:{} pushResult:{}", projectId, JsonUtils.obj2json(pushResult));
        }

        return ApiResult.failed(null, null);
    }
}
