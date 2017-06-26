package com.maoding.corp.module.corpclient.dto;

/**
 * Created by Wuwq on 2017/4/24.
 */
public class PullResult {
    private Boolean appendData;

    public void setAppendData(Boolean appendData) {
        this.appendData = appendData;
    }

    public boolean isSuccessful() {
        return this.appendData == true;
    }
}
