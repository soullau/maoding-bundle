package com.maoding.filecenter.module.file.service;

import com.maoding.common.module.companyDisk.service.CompanyDiskService;
import com.maoding.core.base.BaseService;
import com.maoding.core.bean.ApiResult;
import com.maoding.core.bean.FastdfsUploadResult;
import com.maoding.core.bean.MultipartFileParam;
import com.maoding.core.exception.DataNotFoundException;
import com.maoding.filecenter.constDefine.NetFileStatus;
import com.maoding.filecenter.constDefine.NetFileType;
import com.maoding.common.module.dynamic.service.DynamicService;
import com.maoding.filecenter.module.file.dao.NetFileDAO;
import com.maoding.filecenter.module.file.dto.DeleteDTO;
import com.maoding.filecenter.module.file.dto.DirectoryDTO;
import com.maoding.filecenter.module.file.dto.NetFileRenameDTO;
import com.maoding.filecenter.module.file.model.NetFileDO;
import com.maoding.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * Created by Wuwq on 2017/4/24.
 */
@Service("netFileService")
public class NetFileServiceImpl extends BaseService implements NetFileService {

    private static final Logger log = LoggerFactory.getLogger(NetFileServiceImpl.class);

    @Autowired
    private NetFileDAO netFileDAO;

    @Autowired
    private FastdfsService fastdfsService;

    @Autowired
    private DynamicService dynamicService;

    @Autowired
    private CompanyDiskService companyDiskService;

    /**
     * 创建目录
     */
    @Override
    public ApiResult createDirectory(DirectoryDTO dir) throws DataNotFoundException {
        Example selectExample = new Example(NetFileDO.class);
        selectExample.createCriteria()
                .andCondition("pid = ", dir.getPid())
                .andCondition("file_name = ", dir.getFileName());
        if (netFileDAO.selectCountByExample(selectExample) > 0)
            return ApiResult.failed("同级目录下已存在名称重复的项", null);

        NetFileDO netFileDO = new NetFileDO();
        BeanUtils.copyProperties(dir, netFileDO);

        netFileDO.initEntity();
        netFileDO.setCreateBy(dir.getAccountId());
        netFileDO.setUpdateBy(dir.getAccountId());
        netFileDO.setStatus(NetFileStatus.Normal.toString());
        netFileDO.setType(NetFileType.DIRECTORY);
        netFileDO.setIsCustomize(0);

        //根据父节点调整IDPath
        if (StringUtils.isBlank(dir.getPid())) {
            netFileDO.setSkyDrivePath(netFileDO.getId());
        } else {
            NetFileDO parent = netFileDAO.selectByPrimaryKey(dir.getPid());
            if (parent == null)
                throw new DataNotFoundException(String.format("找不到父目录：%s", dir.getPid()));
            netFileDO.setSkyDrivePath(String.format("%s-%s", parent.getSkyDrivePath(), netFileDO.getId()));
        }

        if (netFileDAO.insert(netFileDO) > 0) {
            //添加项目动态
            dynamicService.addDynamic(dynamicService.createDynamicFrom(netFileDO));
            return ApiResult.success(null, null);
        }
        return ApiResult.failed(null, null);
    }

    /**
     * 上传文件
     */
    @Override
    public ApiResult uploadFile(HttpServletRequest request) throws Exception {
        MultipartFileParam param = MultipartFileParam.parse(request);
        //return ApiResult.failed("超出容量",FastdfsUploadResult.parse(param,null,null));


        FastdfsUploadResult fuResult = fastdfsService.upload(param);
        if (fuResult.getNeedFlow())
            return ApiResult.success(null, fuResult);

        String accountId = (String) param.getParam().get("accountId");
        String companyId = (String) param.getParam().get("companyId");
        String projectId = (String) param.getParam().get("projectId");

        if(com.mysql.jdbc.StringUtils.isNullOrEmpty(companyId))
            return ApiResult.failed("组织ID不能为空", null);

        if(com.mysql.jdbc.StringUtils.isNullOrEmpty(accountId))
            return ApiResult.failed("账号ID不能为空", null);

        if(com.mysql.jdbc.StringUtils.isNullOrEmpty(projectId))
            return ApiResult.failed("项目ID不能为空", null);

        //业务系统逻辑
        NetFileDO netFileDO = new NetFileDO();
        netFileDO.initEntity();
        netFileDO.setCreateBy(accountId);
        netFileDO.setUpdateBy(accountId);
        netFileDO.setCompanyId(companyId);
        netFileDO.setProjectId(projectId);
        netFileDO.setStatus(NetFileStatus.Normal.toString());
        netFileDO.setType(NetFileType.FILE);
        netFileDO.setIsCustomize(0);
        netFileDO.setFileGroup(fuResult.getFastdfsGroup());
        netFileDO.setFilePath(fuResult.getFastdfsPath());
        netFileDO.setFileSize(fuResult.getFileSize());
        netFileDO.setFileName(fuResult.getFileName());
        netFileDO.setFileExtName(fuResult.getFileExtName());

        netFileDO.setPid((String) param.getParam().get("pid"));
        //根据父节点调整IDPath
        if (StringUtils.isBlank(netFileDO.getPid())) {
            netFileDO.setSkyDrivePath(netFileDO.getId());
        } else {
            NetFileDO parent = netFileDAO.selectByPrimaryKey(netFileDO.getPid());
            if (parent == null)
                throw new DataNotFoundException(String.format("找不到父目录：%s", netFileDO.getPid()));
            netFileDO.setSkyDrivePath(String.format("%s-%s", parent.getSkyDrivePath(), netFileDO.getId()));
        }

        if (netFileDAO.insert(netFileDO) > 0) {
            //计算剩余空间
            companyDiskService.recalcSizeOnAddFile(companyId, fuResult.getFileSize());
            //添加项目动态
            dynamicService.addDynamic(dynamicService.createDynamicFrom(netFileDO));
            return ApiResult.success(null, fuResult);
        }
        return ApiResult.failed(null, null);
    }

    /**
     * 重命名文件
     */
    @Override
    public ApiResult rename(NetFileRenameDTO dto) {
        if(com.mysql.jdbc.StringUtils.isNullOrEmpty(dto.getId()))
            return ApiResult.failed("ID不能为空", null);

        NetFileDO netFileDO = netFileDAO.selectByPrimaryKey(dto.getId());
        if (netFileDO == null)
            return ApiResult.failed("找不到要重命名的项", null);

        Example selectExample = new Example(NetFileDO.class);
        selectExample.createCriteria()
                .andCondition("pid = ", netFileDO.getPid())
                .andCondition("file_name = ", dto.getFileName());
        if (netFileDAO.selectCountByExample(selectExample) > 0)
            return ApiResult.failed("同级目录下已存在名称重复的项", null);

        NetFileDO updateObj = new NetFileDO();
        updateObj.setId(dto.getId());
        updateObj.setFileName(dto.getFileName());
        updateObj.setUpdateBy(dto.getAccountId());
        updateObj.setUpdateDate(LocalDateTime.now());

        if (netFileDAO.updateByPrimaryKeySelective(updateObj) > 0) {
            //添加项目动态
            dynamicService.addDynamic(dynamicService.createDynamicFrom(updateObj, netFileDO));
            return ApiResult.success(null, null);
        }
        return ApiResult.failed(null, null);
    }

    /**
     * 删除文件
     */
    @Override
    public ApiResult delete(DeleteDTO dto) {
        if(com.mysql.jdbc.StringUtils.isNullOrEmpty(dto.getId()))
            return ApiResult.failed("ID不能为空", null);

        NetFileDO netFileDO = netFileDAO.selectByPrimaryKey(dto.getId());
        if (netFileDO == null)
            return ApiResult.failed("找不到要删除的项", null);

        if (netFileDO.getType().equals(NetFileType.DIRECTORY)) {
            if (netFileDO.getIsCustomize().equals(1))
                return ApiResult.failed("固定目录不允许删除", null);
            else {
                if (!netFileDO.getCreateBy().equalsIgnoreCase(dto.getAccountId()))
                    return ApiResult.failed("你没有权限进行该操作", null);
            }
        } else {
            if (!netFileDO.getCreateBy().equalsIgnoreCase(dto.getAccountId()))
                return ApiResult.failed("你没有权限进行该操作", null);
        }

        //TODO  因为是逻辑删除，需要定时任务清理
        /*ApiResult ar=fastdfsService.delete(netFileDO.getFileGroup(),netFileDO.getFilePath());
        if(!ar.isSuccessful())
            return ar;*/

        NetFileDO updateObj = new NetFileDO();
        updateObj.setId(dto.getId());
        updateObj.setStatus(NetFileStatus.Deleted.toString());
        updateObj.setUpdateBy(dto.getAccountId());
        updateObj.setUpdateDate(LocalDateTime.now());

        if (netFileDAO.updateByPrimaryKeySelective(updateObj) > 0) {
            //计算剩余空间
            companyDiskService.recalcSizeOnRemoveFile(netFileDO.getCompanyId(), netFileDO.getFileSize());
            //添加项目动态
            dynamicService.addDynamic(dynamicService.createDynamicFrom(netFileDO, null, dto.getAccountId()));
            return ApiResult.success(null, null);
        }
        return ApiResult.failed(null, null);
    }
}
