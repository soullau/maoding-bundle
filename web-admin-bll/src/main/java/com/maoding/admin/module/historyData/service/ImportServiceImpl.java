package com.maoding.admin.module.historyData.service;

import com.maoding.admin.constDefine.ProjectMapper;
import com.maoding.admin.module.historyData.dao.CompanyDAO;
import com.maoding.admin.module.historyData.dao.ProjectDAO;
import com.maoding.admin.module.historyData.dao.UserDAO;
import com.maoding.admin.module.historyData.dto.ProjectImportDTO;
import com.maoding.admin.module.historyData.dto.ProjectQueryDTO;
import com.maoding.admin.module.historyData.model.ProjectDO;
import com.maoding.core.base.BaseRequest;
import com.maoding.core.base.BaseService;
import com.maoding.core.bean.ApiRequestInsert;
import com.maoding.utils.DateUtils;
import com.maoding.utils.ExcelUtils;
import com.maoding.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Chengliang.zhang on 2017/7/19.
 * 导入文件服务
 */
@Service("importService")
public class ImportServiceImpl extends BaseService implements ImportService {
    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    UserDAO userReadOnlyDAO;

    @Autowired
    ProjectDAO projectDAO;

    /**
     * 导入项目数据
     *
     * @param request 操作请求
     */
    @Override
    public void importProjects(ApiRequestInsert<ProjectImportDTO> request) {
        if ((request == null) || (request.getData() == null)) throw new IllegalArgumentException("importProjects 参数错误");

        final Integer DEFAULT_SHEET_INDEX = -1;
        final Integer DEFAULT_TITLE_ROW = 4;
        List<Map<String,Object>> dataList = ExcelUtils.readFrom(request.getData().getImportFile(),DEFAULT_SHEET_INDEX,DEFAULT_TITLE_ROW);
        if ((dataList == null) || (dataList.isEmpty())) throw new IllegalArgumentException("importProjects 无有效数据");

        for (Map<String,Object> data : dataList){
            ProjectDO project = createProjectDOFrom(data,request);
            if (project == null) continue;
            //检查数据有效性
            if ((project.getProjectNo() == null) && (ProjectMapper.PROJECT_NO.contains("*"))) continue;
            if ((project.getProjectName() == null) && (ProjectMapper.PROJECT_NAME.contains("*"))) continue;
            if ((project.getCompanyId() == null) && (ProjectMapper.PROJECT_COMPANY_NAME.contains("*"))) continue;
            if ((project.getCreateBy() == null) && (ProjectMapper.PROJECT_CREATOR_NAME.contains("*"))) continue;
            if ((project.getCreateDate() == null) && (ProjectMapper.PROJECT_CONTRACT_DATE.contains("*"))) continue;
            //存储项目数据
            ProjectQueryDTO query = new ProjectQueryDTO(project.getCompanyId(),project.getProjectNo(),project.getProjectName(),project.getCreateDate());
            if (projectDAO.getProject(query) == null) {
                if (project.getId() == null) project.setId(StringUtils.buildUUID());
                projectDAO.insert(project);
            }
        }
    }

    /** 转换数据为实体对象 */
    private ProjectDO createProjectDOFrom(Map<String,Object> data, BaseRequest request){
        if (data == null) return null;

        ProjectDO project = new ProjectDO();

        //项目编号
        project.setProjectNo((String)data.get(ProjectMapper.PROJECT_NO));

        //项目名称
        project.setProjectName((String)data.get(ProjectMapper.PROJECT_NAME));

        //立项组织和立项人
        String creatorCompanyName = (String)data.get(ProjectMapper.PROJECT_COMPANY_NAME);
        String creatorUserName = (String)data.get(ProjectMapper.PROJECT_CREATOR_NAME);
        if (StringUtils.isEmpty(creatorCompanyName) || StringUtils.isEmpty(creatorUserName)) return null;

        String creatorCompanyId = companyDAO.getCompanyIdByCompanyNameAndUserName(creatorCompanyName, creatorUserName);
        project.setCompanyId(creatorCompanyId);
        String creatorUserId = userReadOnlyDAO.getUserIdByCompanyNameAndUserName(creatorCompanyName, creatorUserName);
        project.setCreateBy(creatorUserId);

        //合同签订日期
        project.setCreateDate(DateUtils.getLocalDateTime((Date)data.get(ProjectMapper.PROJECT_CONTRACT_DATE)));

        //项目地点-省/直辖市
        project.setProvince((String)data.get(ProjectMapper.PROJECT_PROVINCE));

        //项目地点-市/直辖市区
        project.setCity((String)data.get(ProjectMapper.PROJECT_CITY));

        //项目地点-区/县
        project.setCounty((String)data.get(ProjectMapper.PROJECT_COUNTY));

        //项目地点-详细地址
        project.setDetailAddress((String)data.get(ProjectMapper.PROJECT_DETAIL_ADDRESS));

        //项目状态
        String status = (String)data.get(ProjectMapper.PROJECT_STATUS);
        if ((status != null) && ProjectMapper.STATUS_MAPPER.containsKey(status)){
            project.setStatus(ProjectMapper.STATUS_MAPPER.get(status));
        } else {
            project.setStatus(ProjectMapper.PROJECT_STATUS_FINISHED);
        }

        //甲方
        String aCompanyName = (String)data.get(ProjectMapper.PROJECT_A_NAME);
        if (!StringUtils.isEmpty(aCompanyName)) {
            String aCompanyId = companyDAO.getCompanyIdByCompanyNameForA(aCompanyName);
            project.setConstructCompany(aCompanyId);
        }

        //乙方
        String bCompanyName = (String)data.get(ProjectMapper.PROJECT_B_NAME);
        if (!StringUtils.isEmpty(bCompanyName)) {
            String bCompanyId = companyDAO.getCompanyIdByCompanyNameForB(bCompanyName, creatorCompanyId);
            project.setCompanyBid(bCompanyId);
        }

        return project;
    }
}
