package com.maoding.admin.module.historyData.service;

import com.maoding.admin.constDefine.ProjectMapper;
import com.maoding.admin.module.historyData.dao.CompanyDAO;
import com.maoding.admin.module.historyData.dao.ProjectDAO;
import com.maoding.admin.module.historyData.dao.UserDAO;
import com.maoding.admin.module.historyData.dto.ImportResultDTO;
import com.maoding.admin.module.historyData.dto.ProjectImportDTO;
import com.maoding.admin.module.historyData.dto.ProjectQueryDTO;
import com.maoding.admin.module.historyData.model.ProjectDO;
import com.maoding.core.base.BaseRequest;
import com.maoding.core.base.BaseService;
import com.maoding.core.bean.ApiRequestInsert;
import com.maoding.utils.DateUtils;
import com.maoding.utils.ExcelUtils;
import com.maoding.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    /** 日志对象 */
    private static final Logger log = LoggerFactory.getLogger(ImportServiceImpl.class);

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
    public ImportResultDTO importProjects(ApiRequestInsert<ProjectImportDTO> request) {
        if ((request == null) || (request.getData() == null)) throw new IllegalArgumentException("importProjects 参数错误");

        final Integer DEFAULT_SHEET_INDEX = -1;
        final Integer DEFAULT_TITLE_ROW = 4;
        List<Map<String,Object>> dataList = ExcelUtils.readFrom(request.getData().getImportFile(),DEFAULT_SHEET_INDEX,DEFAULT_TITLE_ROW);
        if ((dataList == null) || (dataList.isEmpty())) throw new IllegalArgumentException("importProjects 无有效数据");

        ImportResultDTO result = new ImportResultDTO();
        for (Map<String,Object> data : dataList){
            result.addTotalCount();

            ProjectDO project = createProjectDOFrom(data,request);
            //检查数据有效性
            if ((project == null)
                    || ((project.getProjectNo() == null) && (ProjectMapper.PROJECT_NO.contains("*")))
                    || ((project.getProjectName() == null) && (ProjectMapper.PROJECT_NAME.contains("*")))
                    || ((project.getCompanyId() == null) && (ProjectMapper.PROJECT_COMPANY_NAME.contains("*")))
                    || ((project.getCreateBy() == null) && (ProjectMapper.PROJECT_CREATOR_NAME.contains("*")))
                    || ((project.getCreateDate() == null) && (ProjectMapper.PROJECT_CONTRACT_DATE.contains("*")))){
                result.addFailed(data);
                continue;
            }
            //存储项目数据
            ProjectQueryDTO query = new ProjectQueryDTO(project.getCompanyId(),project.getProjectNo(),project.getProjectName(),project.getCreateDate());
            if (projectDAO.getProject(query) == null) {
                try{
                    insertProject(project);
                }catch (Exception e){
                    log.error("添加数据时产生错误",e);
                    result.addFailed(data);
                    continue;
                }
            } else {
                result.addFailed(data);
                continue;
            }
        }
        return result;
    }

    /** 添加项目 */
    private void insertProject(ProjectDO project){
        //添加项目数据
        if (project.getId() == null) project.setId(StringUtils.buildUUID());
        if (project.getPstatus() == null) project.setPstatus("0");
        projectDAO.insert(project);
        //添加项目负责人
        //添加乙方项目负责人
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
            String aCompanyId = companyDAO.getCompanyIdByCompanyNameForA(aCompanyName,creatorCompanyId);
            project.setConstructCompany(aCompanyId);
        }

        //乙方
        String bCompanyName = (String)data.get(ProjectMapper.PROJECT_B_NAME);
        if (!StringUtils.isEmpty(bCompanyName)) {
            String bCompanyId = companyDAO.getCompanyIdByCompanyNameForB(bCompanyName,creatorCompanyId);
            project.setCompanyBid(bCompanyId);
        }

        //建筑类型
        project.setProjectType("1c6f48757e684b3cb059b94021e12baa");

        return project;
    }
}
