package com.maoding.filecenter.constDefine;

/**
 * Created by Wuwq on 2017/2/15.
 */
public class NetFileType {
    /**
     * 目录
     */
    public static final Integer DIRECTORY = 0;

    /**
     * 文件
     */
    public static final Integer FILE = 1;

    /**
     * 项目附件（除项目文档库以为的附件） 附件类型(3.合同附件)
     */
    public static final Integer PROJECT_CONTRACT_ATTACH = 3;

    /**
     * 组织附件类型(4.公司logo,6.移动端上传轮播图片,7:公司邀请二维码)
     */
    public static final Integer COMPANY_LOGO_ATTACH = 4;

    /**
     * 组织附件类型(6.移动端上传轮播图片)
     */
    public static final Integer COMPANY_BANNER_ATTACH = 6;

    /**
     * 组织附件类型(7:公司邀请二维码)
     */
    public static final Integer COMPANY_QR_CODE_ATTACH = 7;

    /**
     * 报销附件类型（20）
     */
    public static final Integer EXPENSE_ATTACH = 20;

}
