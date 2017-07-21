package com.maoding.admin.module.historyData.dto;

import java.io.InputStream;

/**
 * Created by Chengliang.zhang on 2017/7/19.
 */
public class ProjectImportDTO {
    /** 导入文件 */
    String importFile;
    InputStream importStream;

    public String getImportFile() {
        return importFile;
    }

    public void setImportFile(String importFile) {
        this.importFile = importFile;
    }

    public InputStream getImportStream() {
        return importStream;
    }

    public void setImportStream(InputStream importStream) {
        this.importStream = importStream;
    }
}
