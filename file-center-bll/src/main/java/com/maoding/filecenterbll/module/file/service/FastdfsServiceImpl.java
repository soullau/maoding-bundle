package com.maoding.filecenterbll.module.file.service;

import com.maoding.core.base.BaseService;
import com.maoding.core.bean.ApiResult;
import com.maoding.core.bean.FastdfsUploadResult;
import com.maoding.core.bean.MultipartFileParam;
import com.maoding.fastdfsClient.FdfsClientConstants;
import com.maoding.fastdfsClient.domain.FileInfo;
import com.maoding.fastdfsClient.domain.StorePath;
import com.maoding.fastdfsClient.service.AppendFileStorageClient;
import com.maoding.fastdfsClient.service.FastFileStorageClient;
import com.maoding.utils.StringUtils;
import org.apache.commons.fileupload.FileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by Wuwq on 2017/06/01.
 */
@Service("fastdfsService")
public class FastdfsServiceImpl extends BaseService implements FastdfsService {
    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private AppendFileStorageClient appendStorageClient;

    /**
     * 上传到FastDFS
     */
    @Override
    public FastdfsUploadResult upload(MultipartFileParam param) throws IOException {
        String extName = param.getFileExtName();
        FileItem fileItem = param.getFileItem();
        long size = fileItem.getSize();

        FastdfsUploadResult fuResult;

        //判断是否分片上传
        if (param.isChunked()) {

            //分片大小必须和前端保持一致
            if (param.getChunkPerSize() == null || param.getChunkPerSize().equals(0L))
                throw new UnsupportedOperationException("没有指定分片大小");

            int chunk = param.getChunk();
            if (chunk == 0) {
                //TODO 当FastDFS服务器存在多个组的时候需要分配逻辑
                StorePath storePath = appendStorageClient.uploadAppenderFile(FdfsClientConstants.DEFAULT_GROUP, fileItem.getInputStream(), fileItem.getSize(), extName);
                fuResult=FastdfsUploadResult.parse(param, storePath.getGroup(), storePath.getPath());
                fuResult.setNeedFlow(true);
                return fuResult;
            }

            //分片叠加
            long offset = param.getChunkPerSize() * param.getChunk().longValue();
            String group = param.getFastdfsGroup();
            String path = param.getFastdfsPath();
            appendStorageClient.modifyFile(group, path, fileItem.getInputStream(), size, offset);

            //非最后一块分片
            if (chunk < param.getChunks() - 1) {
                fuResult = FastdfsUploadResult.parse(param, group, path);
                fuResult.setNeedFlow(true);
                return fuResult;
            }

            //TODO 要考虑通过限时缓存来计算文件大小，减少消耗
            FileInfo fileInfo = appendStorageClient.queryFileInfo(group, path);
            fuResult = FastdfsUploadResult.parse(param, group, path);
            fuResult.setFileSize(fileInfo.getFileSize());
        } else {
            //非分片上传
            StorePath storePath = storageClient.uploadFile(fileItem.getInputStream(), size, extName, null);
            fuResult = FastdfsUploadResult.parse(param, storePath.getGroup(), storePath.getPath());
        }
        fuResult.setNeedFlow(false);
        return fuResult;
    }

    @Override
    public ApiResult delete(String group, String path) {
        if (StringUtils.isBlank(path))
            throw new NullPointerException("Path不能为空");

        if (StringUtils.isNotBlank(group))
            storageClient.deleteFile(group, path);
        else if (StringUtils.isBlank(group) && (StringUtils.startsWith(path, "group") || StringUtils.startsWith(path, "/group"))) {
            storageClient.deleteFile(path);
        } else {
            return ApiResult.failed("指定删除文件的参数无效", null);
        }
        return ApiResult.success(null,null);
    }
}
