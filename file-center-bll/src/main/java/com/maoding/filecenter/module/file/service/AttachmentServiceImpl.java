package com.maoding.filecenter.module.file.service;

import com.google.common.collect.Lists;
import com.maoding.common.module.companyDisk.service.CompanyDiskService;
import com.maoding.constDefine.companyDisk.FileSizeSumType;
import com.maoding.core.base.BaseService;
import com.maoding.core.bean.ApiResult;
import com.maoding.core.bean.FastdfsUploadResult;
import com.maoding.core.bean.MultipartFileParam;
import com.maoding.constDefine.im.ImGroupType;
import com.maoding.constDefine.netFile.NetFileStatus;
import com.maoding.constDefine.netFile.NetFileType;
import com.maoding.fastdfsClient.conn.FdfsWebServer;
import com.maoding.filecenter.module.file.dao.NetFileDAO;
import com.maoding.filecenter.module.file.dto.DeleteDTO;
import com.maoding.filecenter.module.file.dto.NetFileOrderDTO;
import com.maoding.filecenter.module.file.dto.SaveCompanyLogoDTO;
import com.maoding.filecenter.module.file.model.NetFileDO;
import com.maoding.filecenter.module.im.dao.ImGroupDAO;
import com.maoding.filecenter.module.im.dto.GroupImgUpdateDTO;
import com.mysql.jdbc.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by Wuwq on 2017/4/24.
 */
@Service("attachmentService")
public class AttachmentServiceImpl extends BaseService implements AttachmentService {

    private static final Logger log = LoggerFactory.getLogger(AttachmentServiceImpl.class);

    @Autowired
    private NetFileDAO netFileDAO;

    @Autowired
    private ImGroupDAO imGroupDAO;

    @Autowired
    private FastdfsService fastdfsService;

    @Autowired
    private CompanyDiskService companyDiskService;

    /**
     * 保存公司logo文件（仅Web端）
     */
    @Override
    public ApiResult saveCompanyLogo(SaveCompanyLogoDTO dto){
        //业务系统逻辑
        Example example = new Example(NetFileDO.class);
        example.createCriteria()
                .andCondition("company_id = ", dto.getCompanyId())
                .andCondition("type = ", NetFileType.COMPANY_LOGO_ATTACH)
                .andCondition("status = ", NetFileStatus.Normal);

        List<NetFileDO> logos = netFileDAO.selectByExample(example);

        if (CollectionUtils.isEmpty(logos)) {
            saveNewNetFile(dto.getCompanyId(), dto.getAccountId(), null, NetFileType.COMPANY_LOGO_ATTACH, 0, null, dto);
        } else {
            NetFileDO netFileDO = logos.get(0);

            try {
                //删除原来的
                fastdfsService.delete(netFileDO.getFileGroup(), netFileDO.getFilePath());
            } catch (Exception ex) {
                log.error("FastDFS 删除公司Logo发生异常（group:{} path:{}），{}", netFileDO.getFileGroup(), netFileDO.getFilePath(), ex.getMessage());
            }

            //更新新的Logo存储信息
            netFileDO.setFileGroup(dto.getFastdfsGroup());
            netFileDO.setFilePath(dto.getFastdfsPath());
            netFileDO.setFileSize(dto.getFileSize());
            netFileDO.setFileName(dto.getFileName());
            netFileDO.setFileExtName(dto.getFileExtName());
            netFileDO.setUpdateBy(dto.getAccountId());
            netFileDO.setUpdateDate(LocalDateTime.now());
            netFileDAO.updateByPrimaryKey(netFileDO);
        }

        //更新群组LOGO
        GroupImgUpdateDTO imgUpdateDTO = new GroupImgUpdateDTO();
        imgUpdateDTO.setOrgId(dto.getCompanyId());
        imgUpdateDTO.setGroupType(ImGroupType.COMPANY);
        imgUpdateDTO.setImg(dto.getFastdfsGroup() + "/" + dto.getFastdfsPath());
        imGroupDAO.updateGroupImg(imgUpdateDTO);

        return ApiResult.success("组织logo已上传成功", dto);
    }

    /**
     * 上传公司logo文件
     */
    @Override
    public ApiResult uploadCompanyLogo(HttpServletRequest request) throws Exception {
        MultipartFileParam param = MultipartFileParam.parse(request);
        if (param.isChunked())
            throw new UnsupportedOperationException("该上传不支持分片模式");

        FastdfsUploadResult fuResult = fastdfsService.uploadLogo(param);
        if (fuResult.getNeedFlow())
            return ApiResult.success(null, fuResult);

        String companyId = (String) param.getParam().get("companyId");
        String accountId = (String) param.getParam().get("accountId");
        String targetId = (String) param.getParam().get("targetId");

        if (StringUtils.isNullOrEmpty(companyId))
            return ApiResult.failed("组织ID不能为空", null);

        if (StringUtils.isNullOrEmpty(accountId))
            return ApiResult.failed("账号ID不能为空", null);

        if (StringUtils.isNullOrEmpty(targetId))
            targetId = null;

        //业务系统逻辑
        Example example = new Example(NetFileDO.class);
        example.createCriteria()
                .andCondition("company_id = ", companyId)
                .andCondition("type = ", NetFileType.COMPANY_LOGO_ATTACH)
                .andCondition("status = ", NetFileStatus.Normal);

        List<NetFileDO> logos = netFileDAO.selectByExample(example);

        if (CollectionUtils.isEmpty(logos) || !StringUtils.isNullOrEmpty(targetId)) {
            saveNewNetFile(companyId, accountId, null, NetFileType.COMPANY_LOGO_ATTACH, 0, targetId, fuResult);
        } else {
            NetFileDO netFileDO = logos.get(0);

            try {
                //删除原来的
                fastdfsService.delete(netFileDO.getFileGroup(), netFileDO.getFilePath());
            } catch (Exception ex) {
                log.error("FastDFS 删除公司Logo发生异常（group:{} path:{}），{}", netFileDO.getFileGroup(), netFileDO.getFilePath(), ex.getMessage());
            }

            //更新新的Logo存储信息
            netFileDO.setFileGroup(fuResult.getFastdfsGroup());
            netFileDO.setFilePath(fuResult.getFastdfsPath());
            netFileDO.setFileSize(fuResult.getFileSize());
            netFileDO.setFileName(fuResult.getFileName());
            netFileDO.setFileExtName(fuResult.getFileExtName());
            netFileDO.setUpdateBy(accountId);
            netFileDO.setUpdateDate(LocalDateTime.now());
            netFileDAO.updateByPrimaryKey(netFileDO);
        }

        //更新群组LOGO
        GroupImgUpdateDTO imgUpdateDTO = new GroupImgUpdateDTO();
        imgUpdateDTO.setOrgId(companyId);
        imgUpdateDTO.setGroupType(ImGroupType.COMPANY);
        imgUpdateDTO.setImg(fuResult.getFastdfsGroup() + "/" + fuResult.getFastdfsPath());
        imGroupDAO.updateGroupImg(imgUpdateDTO);

        //producerService.sendGroupMessage(updateGroupDestination, group);

        return ApiResult.success("组织logo已上传成功", fuResult);
    }

    /**
     * 上传公司轮播图片
     */
    @Override
    public ApiResult uploadCompanyBanner(HttpServletRequest request) throws Exception {
        MultipartFileParam param = MultipartFileParam.parse(request);
        if (param.isChunked())
            throw new UnsupportedOperationException("该上传不支持分片模式");

        FastdfsUploadResult fuResult = fastdfsService.uploadLogo(param);
        if (fuResult.getNeedFlow())
            return ApiResult.success(null, fuResult);

        String companyId = (String) param.getParam().get("companyId");
        String accountId = (String) param.getParam().get("accountId");
        String _seq = (String) param.getParam().get("seq");
        int seq = 0;
        if (!StringUtils.isNullOrEmpty(_seq)) {
            seq = Integer.parseInt(_seq);
        }

        if (StringUtils.isNullOrEmpty(companyId))
            return ApiResult.failed("组织ID不能为空", null);

        if (StringUtils.isNullOrEmpty(accountId))
            return ApiResult.failed("账号ID不能为空", null);

        //插入新记录
        ApiResult ar = saveNewNetFile(companyId, accountId, null, NetFileType.COMPANY_BANNER_ATTACH, seq, null, fuResult);
        if (ar.isSuccessful()) {
            //计算剩余空间
            companyDiskService.recalcSizeOnFileAdded(companyId, FileSizeSumType.OTHER, fuResult.getFileSize());
            //给出全路径
            fuResult.setFileFullPath(FdfsWebServer.webServerUrl+fuResult.getFastdfsGroup()+"/"+fuResult.getFastdfsPath());
            return ApiResult.success(null,fuResult);
        }

        return ApiResult.failed(null, null);
    }

    /**
     * 调整公司轮播图片顺序
     */
    @Override
    public ApiResult orderCompanyBanner(NetFileOrderDTO dto) throws Exception {
        if (StringUtils.isNullOrEmpty(dto.getCompanyId()))
            return ApiResult.failed("组织ID不能为空", null);

        if (StringUtils.isNullOrEmpty(dto.getAccountId()))
            return ApiResult.failed("账号ID不能为空", null);

        Example selectExample = new Example(NetFileDO.class);
        selectExample.createCriteria()
                .andCondition("company_id = ", dto.getCompanyId())
                .andCondition("type = ", NetFileType.COMPANY_BANNER_ATTACH)
                .andCondition("status = ", NetFileStatus.Normal);

        List<NetFileDO> files = netFileDAO.selectByExample(selectExample);
        List<String> fileIds = dto.getFileIds();
        List<NetFileDO> changes = Lists.newArrayList();
        for (int i = 0; i < fileIds.size(); i++) {
            String id = fileIds.get(i);
            Optional<NetFileDO> o = files.stream().filter(f -> f.getId().equalsIgnoreCase(id)).findFirst();
            if (o.isPresent()) {
                NetFileDO file = o.get();
                //只更新变更顺序的
                if (!file.getParam4().equals(i)) {
                    file.setParam4(i);
                    changes.add(file);
                }
            }
        }

        for (NetFileDO f : changes) {
            f.setUpdateBy(dto.getAccountId());
            f.resetUpdateDate();
            netFileDAO.updateByPrimaryKey(f);
        }

        return ApiResult.success(null, null);
    }

    @Override
    public ApiResult uploadOrgAuthenticationAttach(HttpServletRequest request) throws Exception {
        MultipartFileParam param = MultipartFileParam.parse(request);
        if (!param.getParam().containsKey("type"))
            return ApiResult.failed("传递参数错误", null);

        if (param.isChunked())
            throw new UnsupportedOperationException("该上传不支持分片模式");

        FastdfsUploadResult fuResult = fastdfsService.uploadLogo(param);
        if (fuResult.getNeedFlow())
            return ApiResult.success(null, fuResult);

        String companyId = (String) param.getParam().get("companyId");
        String accountId = (String) param.getParam().get("accountId");

        if (StringUtils.isNullOrEmpty(companyId))
            return ApiResult.failed("组织ID不能为空", null);

        if (StringUtils.isNullOrEmpty(accountId))
            return ApiResult.failed("账号ID不能为空", null);

        Integer type = new Integer((String) param.getParam().get("type")) ;
        //业务系统逻辑
        Example example = new Example(NetFileDO.class);
        example.createCriteria()
                .andCondition("company_id = ", companyId)
                .andCondition("type = ", type)
                .andCondition("status = ", NetFileStatus.Normal);
        List<NetFileDO> attach = netFileDAO.selectByExample(example);
        if (CollectionUtils.isEmpty(attach)) {
            saveNewNetFile(companyId, accountId, null, type, 0, null, fuResult);
        } else {
            NetFileDO netFileDO = attach.get(0);
            try {
                //删除原来的
                fastdfsService.delete(netFileDO.getFileGroup(), netFileDO.getFilePath());
            } catch (Exception ex) {
                log.error("FastDFS 删除文件发生异常（group:{} path:{}），{}", netFileDO.getFileGroup(), netFileDO.getFilePath(), ex.getMessage());
            }

            //更新新的Logo存储信息
            netFileDO.setFileGroup(fuResult.getFastdfsGroup());
            netFileDO.setFilePath(fuResult.getFastdfsPath());
            netFileDO.setFileSize(fuResult.getFileSize());
            netFileDO.setFileName(fuResult.getFileName());
            netFileDO.setFileExtName(fuResult.getFileExtName());
            netFileDO.setUpdateBy(accountId);
            netFileDO.setUpdateDate(LocalDateTime.now());
            netFileDAO.updateByPrimaryKey(netFileDO);
        }

        return ApiResult.success("文件已上传成功", fuResult);
    }


    /**
     * 上传报销附件
     */
    @Override
    public ApiResult uploadExpenseAttach(HttpServletRequest request) throws Exception {
        MultipartFileParam param = MultipartFileParam.parse(request);
        FastdfsUploadResult fuResult = fastdfsService.upload(param);
        if (fuResult.getNeedFlow())
            return ApiResult.success(null, fuResult);

        String companyId = (String) param.getParam().get("companyId");
        String accountId = (String) param.getParam().get("accountId");
        String targetId = (String) param.getParam().get("targetId");

        if (StringUtils.isNullOrEmpty(companyId))
            return ApiResult.failed("组织ID不能为空", null);

        if (StringUtils.isNullOrEmpty(accountId))
            return ApiResult.failed("账号ID不能为空", null);

        if (StringUtils.isNullOrEmpty(targetId))
            return ApiResult.failed("报销单ID不能为空", null);

        //插入新记录
        ApiResult ar = saveNewNetFile(companyId, accountId, null, NetFileType.EXPENSE_ATTACH, 0, targetId, fuResult);
        if (ar.isSuccessful()) {
            //TODO 要考虑清理一些新建但没有提交的报销单附件空间
            //计算剩余空间
            companyDiskService.recalcSizeOnFileAdded(companyId, FileSizeSumType.OTHER, fuResult.getFileSize());
            return ApiResult.success(null,fuResult);
        }

        return ApiResult.failed(null, null);
    }

    @Override
    public ApiResult uploadNoticeAttach(HttpServletRequest request) throws Exception {
        MultipartFileParam param = MultipartFileParam.parse(request);
        FastdfsUploadResult fuResult = fastdfsService.upload(param);
        if (fuResult.getNeedFlow())
            return ApiResult.success(null, fuResult);

        String companyId = (String) param.getParam().get("companyId");
        String accountId = (String) param.getParam().get("accountId");
        String targetId = (String) param.getParam().get("targetId");

        if (StringUtils.isNullOrEmpty(companyId))
            return ApiResult.failed("组织ID不能为空", null);

        if (StringUtils.isNullOrEmpty(accountId))
            return ApiResult.failed("账号ID不能为空", null);

        if (StringUtils.isNullOrEmpty(targetId))
            return ApiResult.failed("通知公告ID不能为空", null);

        //插入新记录
        ApiResult ar = saveNewNetFile(companyId, accountId, null, NetFileType.NOTICE_ATTACH, 0, targetId, fuResult);
        if (ar.isSuccessful()) {
            //计算剩余空间
            companyDiskService.recalcSizeOnFileAdded(companyId, FileSizeSumType.OTHER, fuResult.getFileSize());
            return ApiResult.success(null,fuResult);
        }

        return ApiResult.failed(null, null);
    }

    /**
     * 上传项目合同扫描件
     */
    @Override
    public ApiResult uploadProjectContract(HttpServletRequest request) throws Exception {
        MultipartFileParam param = MultipartFileParam.parse(request);
        FastdfsUploadResult fuResult = fastdfsService.upload(param);
        if (fuResult.getNeedFlow())
            return ApiResult.success(null, fuResult);

        String companyId = (String) param.getParam().get("companyId");
        String accountId = (String) param.getParam().get("accountId");
        String projectId = (String) param.getParam().get("projectId");
        Boolean replacePrev = (Boolean) param.getParam().get("replacePrev");

        if (StringUtils.isNullOrEmpty(companyId))
            return ApiResult.failed("组织ID不能为空", null);

        if (StringUtils.isNullOrEmpty(accountId))
            return ApiResult.failed("账号ID不能为空", null);

        if (StringUtils.isNullOrEmpty(projectId))
            return ApiResult.failed("项目ID不能为空", null);

        /** 只在明确覆盖时删除之前的合同文件，保留多个文件 */
        if ((replacePrev != null) && (replacePrev == true)) {
            Example selectExample = new Example(NetFileDO.class);
            selectExample.createCriteria()
                    .andCondition("company_id = ", companyId)
                    .andCondition("project_id = ", projectId)
                    .andCondition("type = ", NetFileType.PROJECT_CONTRACT_ATTACH)
                    .andCondition("status = ", NetFileStatus.Normal)
                    .andCondition("file_name = ",fuResult.getFileName());

            List<NetFileDO> oldAttachs = netFileDAO.selectByExample(selectExample);
            if (oldAttachs != null && oldAttachs.size() > 0) {
                //累计扣减空间
                long subFileSize = 0L;
                for (NetFileDO oa : oldAttachs) {
                    oa.setStatus(NetFileStatus.Deleted.toString());
                    oa.setUpdateBy(accountId);
                    oa.setUpdateDate(LocalDateTime.now());
                    if (netFileDAO.updateByPrimaryKey(oa) > 0) {
                        subFileSize += oa.getFileSize();
                    }
                }
                //计算剩余空间
                if (subFileSize > 0L)
                    companyDiskService.recalcSizeOnFileRemoved(companyId, FileSizeSumType.OTHER, subFileSize);
            }
        }

        //插入新记录
        ApiResult ar = saveNewNetFile(companyId, accountId, projectId, NetFileType.PROJECT_CONTRACT_ATTACH, null, null, fuResult);
        if (ar.isSuccessful()) {
            //计算剩余空间
            companyDiskService.recalcSizeOnFileAdded(companyId, FileSizeSumType.OTHER, fuResult.getFileSize());
            return ApiResult.success(null,fuResult);
        }

        return ApiResult.failed(null, null);
    }

    @Override
    public ApiResult uploadGroupImg(HttpServletRequest request) throws Exception {
        MultipartFileParam param = MultipartFileParam.parse(request);
        FastdfsUploadResult fuResult = fastdfsService.upload(param);
        if (fuResult.getNeedFlow())
            return ApiResult.success(null, fuResult);

//        String companyId = (String) param.getParam().get("companyId");
//        String accountId = (String) param.getParam().get("accountId");
        String orgId = (String) param.getParam().get("orgId");

//        if (StringUtils.isNullOrEmpty(companyId))
//            return ApiResult.failed("组织ID不能为空", null);
//
//        if (StringUtils.isNullOrEmpty(accountId))
//            return ApiResult.failed("账号ID不能为空", null);

        if (StringUtils.isNullOrEmpty(orgId))
            return ApiResult.failed("群组orgId不能为空", null);

        //更新群组LOGO
        GroupImgUpdateDTO imgUpdateDTO = new GroupImgUpdateDTO();
        imgUpdateDTO.setOrgId(orgId);
        imgUpdateDTO.setGroupType(ImGroupType.CUSTOM);
        imgUpdateDTO.setImg(fuResult.getFastdfsGroup() + "/" + fuResult.getFastdfsPath());
        imGroupDAO.updateGroupImg(imgUpdateDTO);

        return ApiResult.success("文件已上传成功", fuResult);

    }

    /**
     * 删除附件
     */
    @Override
    public ApiResult delete(DeleteDTO dto) {
        if (StringUtils.isNullOrEmpty(dto.getId()))
            return ApiResult.failed("ID不能为空", null);

        NetFileDO netFileDO = netFileDAO.selectByPrimaryKey(dto.getId());
        if (netFileDO == null)
            return ApiResult.failed("找不到要删除的项", null);

        //TODO  是逻辑删除，需要定时任务清理

        NetFileDO updateObj = new NetFileDO();
        updateObj.setId(dto.getId());
        updateObj.setStatus(NetFileStatus.Deleted.toString());
        updateObj.setUpdateBy(dto.getAccountId());
        updateObj.setUpdateDate(LocalDateTime.now());

        if (netFileDAO.updateByPrimaryKeySelective(updateObj) > 0) {
            //计算剩余空间
            companyDiskService.recalcSizeOnFileRemoved(netFileDO.getCompanyId(),FileSizeSumType.OTHER, netFileDO.getFileSize());
            return ApiResult.success(null, null);
        }
        return ApiResult.failed(null, null);
    }


    /**
     * 插入新的NetFile纪录
     */
    private ApiResult saveNewNetFile(String companyId, String accountId, String projectId, Integer netFileType, Integer seq, String targetId, FastdfsUploadResult fuResult) {
        NetFileDO netFileDO = new NetFileDO();
        netFileDO.initEntity();
        netFileDO.setCreateBy(accountId);
        netFileDO.setUpdateBy(accountId);
        netFileDO.setCompanyId(companyId);
        netFileDO.setProjectId(projectId);
        netFileDO.setStatus(NetFileStatus.Normal.toString());
        netFileDO.setType(netFileType);
        netFileDO.setIsCustomize(0);
        netFileDO.setFileGroup(fuResult.getFastdfsGroup());
        netFileDO.setFilePath(fuResult.getFastdfsPath());
        netFileDO.setFileSize(fuResult.getFileSize());
        netFileDO.setFileName(fuResult.getFileName());
        netFileDO.setFileExtName(fuResult.getFileExtName());
        netFileDO.setParam4(seq);
        netFileDO.setTargetId(targetId);
        if (netFileDAO.insert(netFileDO) > 0) {
            fuResult.setNetFileId(netFileDO.getId());
            fuResult.setNetFileSeq(seq);
            return ApiResult.success(null, netFileDO);
        }
        return ApiResult.failed(null, null);
    }
}
