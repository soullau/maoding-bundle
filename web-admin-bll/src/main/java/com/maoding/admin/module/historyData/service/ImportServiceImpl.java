package com.maoding.admin.module.historyData.service;

import com.maoding.admin.constDefine.ProjectMapper;
import com.maoding.admin.module.historyData.dao.CompanyDAO;
import com.maoding.admin.module.historyData.dto.ProjectImportDTO;
import com.maoding.admin.module.historyData.model.ProjectDO;
import com.maoding.core.base.BaseService;
import com.maoding.core.bean.ApiRequestInsert;
import com.maoding.utils.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chengliang.zhang on 2017/7/19.
 */
@Service("importService")
public class ImportServiceImpl extends BaseService implements ImportService {
    @Autowired
    CompanyDAO companyDAO;

    /**
     * 导入项目数据
     *
     * @param request 操作请求
     */
    @Override
    public void importProjects(ApiRequestInsert<ProjectImportDTO> request) {
        if ((request == null) || (request.getData() == null)) throw new IllegalArgumentException("importProjects 参数错误");
        List<Map<Short,Object>> dataList = ExcelUtils.readFrom(request.getData().getImportFile(),-1,4,6,null,null);
        if ((dataList == null) || (dataList.isEmpty())) throw new IllegalArgumentException("importProjects 无法导入数据");

        Map<String,Short> mapper = getProjectTitleMapper(dataList.get(0));
        for (Integer i=1; i<dataList.size(); i++){
            ProjectDO entity = createProjectDOFrom(dataList.get(i),mapper,request.getCompanyId());
        }
    }

    /** 获取字段所对应的列编号 */
    private Map<String,Short> getProjectTitleMapper(Map<Short,Object> title){
        if (title == null) return null;

        Map<String,Short> mapper = new HashMap<>();
        title.entrySet().stream().filter(entry -> ProjectMapper.TITLE_MAPPER.containsKey(entry.getValue())).forEach(entry -> {
            mapper.put(ProjectMapper.TITLE_MAPPER.get(entry.getValue()), entry.getKey());
        });
        return mapper;
    }

    /** 转换数据为实体对象 */
    private ProjectDO createProjectDOFrom(Map<Short,Object> data, Map<String,Short> mapper, String currentCompanyId){
        if ((data == null) || (mapper == null)) return null;

        ProjectDO project = new ProjectDO();
        if (mapper.containsKey(ProjectMapper.PROJECT_NO)){
            project.setProjectNo((String)data.get(mapper.get(ProjectMapper.PROJECT_NO)));
        }
        if (mapper.containsKey(ProjectMapper.PROJECT_NAME)){
            project.setProjectName((String)data.get(mapper.get(ProjectMapper.PROJECT_NAME)));
        }
        if (mapper.containsKey(ProjectMapper.PROJECT_CITY)){
            project.setCity((String)data.get(mapper.get(ProjectMapper.PROJECT_CITY)));
        }
        if (mapper.containsKey(ProjectMapper.PROJECT_COUNTY)){
            project.setCounty((String)data.get(mapper.get(ProjectMapper.PROJECT_COUNTY)));
        }
        if (mapper.containsKey(ProjectMapper.PROJECT_DETAIL_ADDRESS)){
            project.setDetailAddress((String)data.get(mapper.get(ProjectMapper.PROJECT_DETAIL_ADDRESS)));
        }
        if (mapper.containsKey(ProjectMapper.PROJECT_STATUS)){
            String status = (String)data.get(mapper.get(ProjectMapper.PROJECT_STATUS));
            if (ProjectMapper.STATUS_MAPPER.containsKey(status)){
                project.setStatus(ProjectMapper.STATUS_MAPPER.get(status));
            } else {
                project.setStatus(ProjectMapper.PROJECT_STATUS_FINISHED);
            }
        }
        if (mapper.containsKey(ProjectMapper.PROJECT_BID)){
            String companyName = (String)data.get(mapper.get(ProjectMapper.PROJECT_BID));
            String companyId = companyDAO.getCompanyIdByCompanyName(companyName,currentCompanyId);
        }

        return project;
    }
}
