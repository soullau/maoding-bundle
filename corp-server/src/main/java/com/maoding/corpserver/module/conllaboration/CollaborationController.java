package com.maoding.corpserver.module.conllaboration;

import com.maoding.core.base.BaseController;
import com.maoding.core.bean.ApiResult;
import com.maoding.corpbll.module.corpserver.dto.CoCompanyDTO;
import com.maoding.corpbll.module.corpserver.dto.CoProjectPhaseDTO;
import com.maoding.corpbll.module.corpserver.dto.CoUserDTO;
import com.maoding.corpbll.module.corpserver.dto.ProjectDTO;
import com.maoding.corpbll.module.corpserver.service.CollaborationService;
import com.mysql.jdbc.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/corpserver/collaboration")
public class CollaborationController extends BaseController {

    @Autowired
    private CollaborationService collaborationService;


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult login(@RequestBody Map<String, Object> map) throws Exception {
        return collaborationService.login(map);
    }

    @RequestMapping(value = "/listCompanyByIds", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult listCompanyByIds(@RequestBody Map<String, Object> map) {
        List<String> companyIds = (List<String>) map.get("companyIds");
        List<CoCompanyDTO> companies = collaborationService.listCompanyByIds(companyIds);
        return ApiResult.success("获取组织列表成功！", companies);
    }

    @RequestMapping(value = "/listUserByCompanyId", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult listUserByCompanyId(@RequestBody Map<String, Object> map) {
        String companyId = (String) map.get("companyId");
        //String syncDate = (String) map.get("syncDate");
        List<CoUserDTO> accounts = collaborationService.listUserByCompanyId(companyId);
        return ApiResult.success("获取查询员工成功！", accounts);
    }

    @RequestMapping(value = "/getProjectById/{projectId}", method = RequestMethod.GET)
    @ResponseBody
    public ApiResult getProjectById(@PathVariable String projectId) {
        ProjectDTO projectEntity = collaborationService.getProjectById(projectId);
        return ApiResult.success("获取项目成功！", projectEntity);
    }

    @RequestMapping(value = "/listProjectByCompanyId", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult listProjectByCompanyId(@RequestBody Map<String, Object> param) throws Exception {
        String companyId = (String) param.get("companyId");
        String syncDate = (String) param.get("syncDate");
        if (!StringUtils.isNullOrEmpty(syncDate)) {
            syncDate = param.get("syncDate").toString();
        }
        List<ProjectDTO> projects = collaborationService.listProjectByCompanyId(companyId, syncDate);
        return ApiResult.success("获取项目列表成功！！", projects);
    }

    /**
     * 获取项目节点（含任务成员状态信息）
     */
    @RequestMapping(value = "/listNode", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult listNode(@RequestBody Map<String, Object> param) throws Exception {
        String companyId = (String) param.get("companyId");
        String projectId = (String) param.get("projectId");
        List<CoProjectPhaseDTO> nodes = collaborationService.listNode(companyId, projectId);
        return ApiResult.success("获取项目节点成功！", nodes);
    }

    /**
     * 修改节点状态
     */
    @RequestMapping(value = "/handleMyTaskByProjectNodeId", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult handleMyTaskByProjectNodeId(@RequestBody Map<String, Object> param) throws Exception {
        String nodeId = (String) param.get("nodeId");
        int status = -1;
        if (param.get("status") != null)
            status = (int) param.get("status");

        switch (status) {
            case 1:
                return collaborationService.finishMyTask(nodeId);
            case 0:
                return collaborationService.activeMyTask(nodeId);
            default:
                return ApiResult.failed("未指定节点状态", null);
        }
    }

    /**
     * 根据组织Id统计文档库大小
     */
    @RequestMapping(value = "/sumDocmgrSizeByCompanyId/{companyId}", method = RequestMethod.GET)
    @ResponseBody
    public ApiResult sumDocmgrSizeByCompanyId(@PathVariable String companyId) {
        return collaborationService.sumDocmgrSizeByCompanyId(companyId);
    }

    /**
     * 根据组织ID获取公司网盘容量信息
     */
    @RequestMapping(value = "/getCompanyDiskInfo", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult getCompanyDiskInfo(@RequestBody Map<String, Object> param) {
        String companyId = (String) param.get("companyId");
        return collaborationService.getCompanyDiskInfo(companyId);
    }

    /**
     * 根据组织ID更新协同占用空间
     */
    @RequestMapping(value = "/updateCorpSizeOnCompanyDisk", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult updateCorpSizeOnCompanyDisk(@RequestBody Map<String, Object> param) {
        String companyId = (String) param.get("companyId");
        Long corpSize = Long.parseLong((String) param.get("corpSize"));
        return collaborationService.updateCorpSizeOnCompanyDisk(companyId, corpSize);
    }
}
