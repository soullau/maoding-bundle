package com.maoding.filecenterbll.module.file.service;

import com.maoding.core.base.BaseService;
import com.maoding.core.bean.ApiResult;
import com.maoding.core.bean.FastdfsUploadResult;
import com.maoding.core.bean.MultipartFileParam;
import com.maoding.filecenterbll.constDefine.NetFileStatus;
import com.maoding.filecenterbll.constDefine.NetFileType;
import com.maoding.filecenterbll.module.file.dao.NetFileDAO;
import com.maoding.filecenterbll.module.file.model.NetFileDO;
import com.mysql.jdbc.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Wuwq on 2017/4/24.
 */
@Service("attachmentService")
public class AttachmentServiceImpl extends BaseService implements AttachmentService {

    private static final Logger log = LoggerFactory.getLogger(AttachmentServiceImpl.class);

    @Autowired
    private NetFileDAO netFileDAO;

    @Autowired
    private FastdfsService fastdfsService;

    /**
     * 上传公司logo文件
     */
    @Override
    public ApiResult uploadCompanyLogo(HttpServletRequest request) throws Exception {
        MultipartFileParam param = MultipartFileParam.parse(request);
        FastdfsUploadResult fuResult = fastdfsService.upload(param);
        if (fuResult.getNeedFlow())
            return ApiResult.success(null, fuResult);

        //业务系统逻辑
        Example example = new Example(NetFileDO.class);
        example.createCriteria()
                .andCondition("company_id = ", param.getParam().get("companyId"))
                .andCondition("type = ", NetFileType.COMPANY_LOGO_ATTACH);
        List<NetFileDO> companyAttachList = netFileDAO.selectByExample(example);

        if (CollectionUtils.isEmpty(companyAttachList)) {

            String companyId = (String) param.getParam().get("companyId");
            String accountId = (String) param.getParam().get("accountId");
            saveNewNetFile(companyId, accountId, null, NetFileType.COMPANY_LOGO_ATTACH, 0, null, fuResult);

        } else {
            NetFileDO netFileDO = companyAttachList.get(0);
/*            Map<String,Object> objectMap = new HashMap<>();
            objectMap.put("group",netFileDO.getFileGroup());
            objectMap.put("path",netFileDO.getFilePath().replaceAll(netFileDO.getFileGroup()+"/",""));
            //TODO 异步删除上一个头像（暂时忽略是否删除失败的策略）
            String json= JsonUtils.obj2json(param);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),json);
            Request request1 = new Request.Builder()
                    .url(fileCenterUrl+"/fastDelete")
                    .post(body)
                    .build();
            OkHttpUtils.enqueue(request1);*/

            netFileDO.setFileGroup(fuResult.getFastdfsGroup());
            netFileDO.setFilePath(fuResult.getFastdfsPath());
            netFileDO.setFileSize(fuResult.getFileSize());
            netFileDO.setFileName(fuResult.getFileName());
            netFileDO.setFileExtName(fuResult.getFileExtName());
            netFileDAO.updateByPrimaryKey(netFileDO);
        }

        //修改群组
//        ImGroupEntity group = new ImGroupEntity();
//        group.setOrgId(companyId);
//        if (!StringUtil.isNullOrEmpty(filePath)) {
//            group.setImg(fileGroup+"/"+filePath);
//        }
//        try {
//            producerService.sendGroupMessage(updateGroupDestination, group);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return ApiResult.failed(null, null);
    }

    /**
     * 方法描述：上传公司轮播图片
     * 作者：MaoSF
     * 日期：2017/6/1
     * @param request
     * @param:
     * @return:
     */
    @Override
    public ApiResult uploadCompanyBanner(HttpServletRequest request) throws Exception {
        MultipartFileParam param = MultipartFileParam.parse(request);
        FastdfsUploadResult fuResult = fastdfsService.upload(param);
        if (fuResult.getNeedFlow())
            return ApiResult.success(null, fuResult);

        String companyId = (String) param.getParam().get("companyId");
        String accountId = (String) param.getParam().get("accountId");
        String _seq = (String) param.getParam().get("seq");
        int seq = 0;
        if (!StringUtils.isNullOrEmpty(_seq)) {
            seq = Integer.parseInt(_seq);
        }

        //上传成功后，数据保存到数据库
        Example example = new Example(NetFileDO.class);
        example.createCriteria()
                .andCondition("company_id = ", companyId)
                .andCondition("type = ", NetFileType.COMPANY_BANNER_ATTACH)
                .andCondition("param4 = ", seq);
        if (!StringUtils.isNullOrEmpty(companyId)) {
            //先删除原有的数据
            netFileDAO.deleteByExample(example);
        }
        //保存
        NetFileDO netFileDO = saveNewNetFile(companyId, accountId, null, NetFileType.COMPANY_BANNER_ATTACH, seq, null, fuResult);

        //返回数据
        Map<String, Object> reMap = new HashMap<>();
        //reMap.put("fastdfsUrl", this.fileCenterUrl);
        reMap.put("attachEntity", netFileDO);
        return ApiResult.success(null, reMap);
    }

    /**
     * 方法描述：上传报销附件
     * 作者：MaoSF
     * 日期：2017/6/1
     * @param request
     * @param:
     * @return:
     */
    @Override
    public ApiResult uploadExpenseAttach(HttpServletRequest request) throws Exception {
        MultipartFileParam param = MultipartFileParam.parse(request);
        FastdfsUploadResult fuResult = fastdfsService.upload(param);
        if (fuResult.getNeedFlow())
            return ApiResult.success(null, fuResult);

        String companyId = (String) param.getParam().get("companyId");
        String accountId = (String) param.getParam().get("accountId");
        String mainId = (String) param.getParam().get("mainId");
        String id = (String) param.getParam().get("id");

        if (StringUtils.isNullOrEmpty(id)) {
            //保存
            NetFileDO netFileDO = saveNewNetFile(companyId, accountId, null, NetFileType.COMPANY_BANNER_ATTACH, 0, mainId, fuResult);
        } else {
            NetFileDO netFileDO = new NetFileDO();
            netFileDO.setId(id);
            netFileDO.setFileGroup(fuResult.getFastdfsGroup());
            netFileDO.setFilePath(fuResult.getFastdfsPath());
            netFileDO.setFileSize(fuResult.getFileSize());
            netFileDO.setFileName(fuResult.getFileName());
            netFileDO.setFileExtName(fuResult.getFileExtName());
            netFileDAO.updateByPrimaryKey(netFileDO);
        }
        return ApiResult.success(null, null);
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

        //先逻辑删除已有的（正常情况下只有一个）
        //TODO 计划清除被逻辑删除的物理文件
        Example selectExample = new Example(NetFileDO.class);
        selectExample.createCriteria()
                .andCondition("company_id = ", companyId)
                .andCondition("project_id = ",projectId)
                .andCondition("type = ", NetFileType.PROJECT_CONTRACT_ATTACH)
                .andCondition("status = ",NetFileStatus.Normal);
        NetFileDO updateObj=new NetFileDO();
        updateObj.setStatus(NetFileStatus.Deleted.toString());
        updateObj.setUpdateBy(accountId);
        updateObj.setUpdateDate(LocalDateTime.now());
        int count=netFileDAO.updateByExampleSelective(updateObj,selectExample);

        //插入新记录
        NetFileDO newObj=saveNewNetFile(companyId,accountId,projectId,NetFileType.PROJECT_CONTRACT_ATTACH,null,null,fuResult);

        return ApiResult.success(null, fuResult);
    }

    /**
     * 插入新的NetFile纪录
     */
    private NetFileDO saveNewNetFile(String companyId, String accountId, String projectId, Integer netFileType, Integer seq, String targetId, FastdfsUploadResult fuResult) {
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
        netFileDAO.insert(netFileDO);
        return netFileDO;
    }
}
