package com.maoding.yongyoucloud.module.enterprise.service;

import com.maoding.constDefine.yongyoucloud.ApiUrl;
import com.maoding.constDefine.yongyoucloud.EnterpriseType;
import com.maoding.core.base.BaseService;
import com.maoding.utils.BeanUtils;
import com.maoding.utils.GsonUtils;
import com.maoding.utils.JsonUtils;
import com.maoding.utils.StringUtils;
import com.maoding.yongyoucloud.domain.HttpInvoker;
import com.maoding.yongyoucloud.module.enterprise.dao.EnterpriseDAO;
import com.maoding.yongyoucloud.module.enterprise.dao.EnterpriseOrgDAO;
import com.maoding.yongyoucloud.module.enterprise.dao.EnterpriseOrgLinkmanDAO;
import com.maoding.yongyoucloud.module.enterprise.dto.*;
import com.maoding.yongyoucloud.module.enterprise.model.EnterpriseDO;
import com.maoding.yongyoucloud.module.enterprise.model.EnterpriseOrgDO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("enterpriseSearchService")
public class EnterpriseSearchServiceImpl extends BaseService implements EnterpriseSearchService{

    private static final Logger logger = LoggerFactory.getLogger(EnterpriseSearchServiceImpl.class);

    @Autowired
    @Qualifier("httpinvoker")
    private HttpInvoker httpinvoker;

    @Autowired
    private EnterpriseDAO enterpriseDAO;

    @Autowired
    private EnterpriseOrgDAO enterpriseOrgDAO;

    @Autowired
    private EnterpriseOrgLinkmanDAO enterpriseOrgLinkmanDAO;

    @Override
    public Map<String,Object> enterpriseStockHolder(EnterpriseSearchQueryDTO dto) throws Exception {
        Map<String,String> param = new HashMap<>();
        param.put("name", dto.getName());
        String result = httpinvoker.invoker(ApiUrl.enterpriseStockHolderUrl,param);
        return JsonUtils.json2map(result);
    }

    @Override
    public Map<String, Object> queryAutoComplete(EnterpriseSearchQueryDTO dto) throws Exception {
        Map<String,String> param = new HashMap<>();
        param.put("keyword",dto.getName());
        param.put("size",dto.getSize()+"");
        String result = httpinvoker.invoker(ApiUrl.queryAutoCompleteUrl,param);
        return JsonUtils.json2map(result);
    }

    @Override
    public Map<String, Object> queryDetail(EnterpriseSearchQueryDTO dto) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        //首先从本地数据库获取，如果没有，则从用友接口中获取，并且插入到数据库中
        EnterpriseDO enterpriseDO = enterpriseDAO.selectByPrimaryKey(dto.getEnterpriseId());
        String enterpriseOrgId = null;
        if(enterpriseDO == null){
            Map<String,String> param = new HashMap<>();
            param.put("id", dto.getEnterpriseId());//工商记录的id
            String result = httpinvoker.invoker(ApiUrl.queryDetailUrl,param);
            EnterpriseDetailDTO detailDTO = JsonUtils.json2pojo(result, EnterpriseDetailDTO.class);
            if(detailDTO != null && detailDTO.getTotal()>0){
                //由于通过id查询和通过name查询后，插入到数据库 中的id是不一致，会导致 数据库中name重名的数据，所以在此再次过滤
                dto.setName(detailDTO.getDetails().get(0).getCorpname());
                enterpriseDO = enterpriseDAO.getEnterpriseByName(dto); //查查当前公司是否存在当前组织的甲方
                if(enterpriseDO==null){
                    enterpriseDO = detailDTO.getDetails().get(0);
                    enterpriseDO.setId(dto.getEnterpriseId());
                    //插入到数据库
                    enterpriseDO.setEnterpriseType(EnterpriseType.ENTERPRISE_QUERY);
                    enterpriseDAO.insert(enterpriseDO);
                }
            }
        }else {
            resultMap.put("linkman",enterpriseOrgLinkmanDAO.getLinkman(dto));
        }
        if(enterpriseDO!=null && dto.isSave() && !StringUtils.isNullOrEmpty(dto.getCompanyId())){
            enterpriseOrgId =this.saveEnterpriseOrgDO(enterpriseDO,dto.getCompanyId());
        }
        resultMap.put("enterpriseOrgId",enterpriseOrgId);
        resultMap.put("enterpriseDO",enterpriseDO);
        return resultMap;
    }

    @Override
    public Map<String, Object> queryFull(EnterpriseSearchQueryDTO dto) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        EnterpriseDO enterpriseDO = enterpriseDAO.getEnterpriseByName(dto); //查查当前公司是否存在当前组织的甲方
        String enterpriseOrgId = null;
        if(enterpriseDO==null) {
            Map<String, String> param = new HashMap<>();
            param.put("fullname", dto.getName().trim());
            String result = httpinvoker.invoker(ApiUrl.queryFullUrl, param);
            EnterpriseDetailDTO detailDTO = null;
            try{
                detailDTO = JsonUtils.json2pojo(result,EnterpriseDetailDTO.class);
            }catch (Exception e){
                e.printStackTrace();
            }

            if(detailDTO != null && detailDTO.getTotal()>0){
                enterpriseDO = detailDTO.getDetails().get(0);
                //插入到数据库
                if(StringUtils.isNullOrEmpty(enterpriseDO.getId())){
                    enterpriseDO.initEntity();
                }
                enterpriseDO.setEnterpriseType(EnterpriseType.ENTERPRISE_QUERY);
                enterpriseDAO.insert(enterpriseDO);
            }else {
                if(dto.isSave()){ // 如果是客户端传递名字来，工商数据没有获取到，则添加到数据库中
                    enterpriseDO = new EnterpriseDO();
                    enterpriseDO.initEntity();
                    enterpriseDO.setCorpname(dto.getName());
                    enterpriseDO.setEnterpriseType(EnterpriseType.ENTERPRISE_CUSTOM);
                    enterpriseDAO.insert(enterpriseDO);
                }
            }
        }else {
            resultMap.put("linkman",enterpriseOrgLinkmanDAO.getLinkman(dto));//只有保存在数据库中的数据才可能存在联系人
        }
        //此段代码必须放在此
        if(enterpriseDO!=null && dto.isSave() && !StringUtils.isNullOrEmpty(dto.getCompanyId())){
            enterpriseOrgId =this.saveEnterpriseOrgDO(enterpriseDO,dto.getCompanyId());
        }
        resultMap.put("enterpriseOrgId",enterpriseOrgId);
        resultMap.put("enterpriseDO",enterpriseDO);
        return resultMap;
    }

    private String saveEnterpriseOrgDO(EnterpriseDO enterpriseDO,String companyId){
        EnterpriseOrgDO t = new EnterpriseOrgDO();
        t.setEnterpriseId(enterpriseDO.getId());
        t.setCompanyId(companyId);
        //先查询
        EnterpriseOrgDO enterpriseOrgDO = enterpriseOrgDAO.selectOne(t);
        if(enterpriseOrgDO == null){
            enterpriseOrgDO = new EnterpriseOrgDO();
            enterpriseOrgDO.initEntity();
            enterpriseOrgDO.setEnterpriseId(enterpriseDO.getId());
            enterpriseOrgDO.setDeleted(0);
            enterpriseOrgDO.setCompanyId(companyId);
            enterpriseOrgDAO.insert(enterpriseOrgDO);
        }
        return enterpriseOrgDO.getId();
    }

    @Override
    public List<EnterpriseDTO> getEnterprise(EnterpriseSearchQueryDTO dto) throws Exception {
        return enterpriseDAO.getEnterprise(dto);
    }

    @Override
    public Map<String, Object> queryEnterpriseDetail(EnterpriseSearchQueryDTO dto) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        //查询甲方的关联信息
        EnterpriseOrgDO enterpriseOrgDO = enterpriseOrgDAO.selectByPrimaryKey(dto.getEnterpriseOrgId());
        if(enterpriseOrgDO == null) {
            return resultMap;
        }
        EnterpriseDO enterpriseDO = enterpriseDAO.selectByPrimaryKey(enterpriseOrgDO.getEnterpriseId());
        if(enterpriseDO == null){
            return resultMap;
        }
        dto.setCompanyId(enterpriseOrgDO.getCompanyId());
        dto.setEnterpriseId(enterpriseOrgDO.getEnterpriseId());
        List<EnterpriseProjectLinkmanDTO> list = enterpriseOrgLinkmanDAO.getLinkman(dto);
        for(EnterpriseProjectLinkmanDTO linkmanDTO:list){
            if(enterpriseOrgLinkmanDAO.getProjectEditRole(dto)>0 || enterpriseOrgLinkmanDAO.getProjectEditRole2(dto)>0 ){
                linkmanDTO.setEditFlag(1);
            }
        }
        resultMap.put("linkman",list);
        resultMap.put("enterpriseOrgId",dto.getEnterpriseOrgId());
        resultMap.put("enterpriseDO",enterpriseDO);
        return resultMap;
    }

}
