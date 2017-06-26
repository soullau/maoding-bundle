package com.maoding.filecenter.module.dynamic.service;

import com.maoding.filecenter.Application;
import com.maoding.filecenter.module.dynamic.dao.ZUserDAO;
import com.maoding.filecenter.module.dynamic.dto.ZUserDTO;
import com.maoding.filecenter.module.file.dao.NetFileDAO;
import com.maoding.filecenter.module.file.model.NetFileDO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by Wuwq on 2017/06/08.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class DynamicServiceImplTest {
    @Autowired
    private ZUserDAO zUserDAO;

    @Autowired
    private NetFileDAO netFileDAO;

    @Test
    public void testGetCompanyUserId(){
        String companyId="5339147901b34a0b99e16b6ac7054d64";
        String userId="8394a028dcfc453e80054973c3cb8c1b";
        ZUserDTO user = zUserDAO.getUserByCompanyIdAndUserId(companyId,userId);
        Assert.assertNotNull(user);
    }

    @Test
    public void test2(){
        String userId="8394a028dcfc453e80054973c3cb8c1b";

        Example example=new Example(NetFileDO.class);
        example.createCriteria().andCondition("create_by = ",userId);
        List<NetFileDO> netFileDOS = netFileDAO.selectByExample(example);
        Assert.assertNotNull(netFileDOS);
    }
}