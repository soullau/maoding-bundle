package com.maoding.corpbll.module.corpserver.service;

import com.google.common.collect.Lists;
import com.maoding.core.base.BaseService;
import com.maoding.core.bean.ApiResult;
import com.maoding.corpbll.constDefine.RKey;
import com.maoding.corpbll.module.corpserver.dao.SyncCompanyDao;
import com.maoding.corpbll.module.corpserver.dto.SyncCompanyDto_Create;
import com.maoding.corpbll.module.corpserver.dto.SyncCompanyDto_Select;
import com.maoding.corpbll.module.corpserver.dto.SyncCompanyDto_Update;
import com.maoding.corpbll.module.corpserver.model.SyncCompany;
import com.maoding.utils.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Wuwq on 2017/2/10.
 */
@Service("syncCompanyServise")
public class SyncCompanyServiseImpl extends BaseService implements SyncCompanyServise {

    private static final Logger logger = LoggerFactory.getLogger(SyncCompanyServiseImpl.class);

    @Autowired
    private SyncCompanyDao syncCompanyDao;

    @Autowired
    private RedissonClient redissonClient;

    //增加redis记录（读写锁）
    private void redisAdd(SyncCompany obj) {
        RReadWriteLock lock = redissonClient.getReadWriteLock("Lock_" + RKey.CorpCompanies);
        RLock r = lock.writeLock();
        r.lock(5, TimeUnit.SECONDS);

        RSet<String> set = redissonClient.getSet(RKey.CorpCompanies);
        set.add(obj.getCorpEndpoint() + ':' + obj.getCompanyId());

        r.unlock();
    }

    //删除redis记录（读写锁）
    private void redisDelete(String corpEndpoint, String companyId) {
        RReadWriteLock lock = redissonClient.getReadWriteLock("Lock_" + RKey.CorpCompanies);
        RLock r = lock.writeLock();
        r.lock(5, TimeUnit.SECONDS);

        RSet<String> set = redissonClient.getSet(RKey.CorpCompanies);
        set.remove(corpEndpoint + ':' + companyId);

        r.unlock();
    }


    @Override
    public ApiResult create(SyncCompanyDto_Create dto) {
        SyncCompany entity = new SyncCompany();
        BeanUtils.copyProperties(dto, entity);
        entity.initEntity();

        Example example = new Example(SyncCompany.class, true);
        example.createCriteria()
                .andCondition("corp_endpoint = ", dto.getCorpEndpoint())
                .andCondition("company_id = ", dto.getCompanyId());
        int existCount = syncCompanyDao.selectCountByExample(example);
        if (existCount > 0)
            return ApiResult.failed("创建失败，重复记录", null);

        if (syncCompanyDao.insert(entity) > 0) {
            //增加redis记录（读写锁）
            //TODO 待考虑失败的情况
            redisAdd(entity);

            return ApiResult.success(null, entity);
        }

        return ApiResult.failed(null, null);
    }

    @Override
    public ApiResult update(SyncCompanyDto_Update dto) {
        SyncCompany entity = new SyncCompany();
        BeanUtils.copyProperties(dto, entity);
        entity.resetUpdateDate();

        if (syncCompanyDao.updateByPrimaryKeySelective(entity) > 0)
            return ApiResult.success(null, null);

        return ApiResult.failed(null, null);
    }

    @Override
    public ApiResult delete(String corpEndpoint, String companyId) {
        Example example = new Example(SyncCompany.class);
        example.createCriteria()
                .andCondition("corp_endpoint = ", corpEndpoint)
                .andCondition("company_id = ", companyId);
        if (syncCompanyDao.deleteByExample(example) > 0) {
            //删除redis记录（读写锁）
            //TODO 待考虑失败的情况
            redisDelete(corpEndpoint, companyId);
            return ApiResult.success(null, null);
        }
        return ApiResult.failed(null, null);
    }

    @Override
    public ApiResult delete(String id) {
        SyncCompany sc = syncCompanyDao.selectByPrimaryKey(id);
        if (syncCompanyDao.deleteByPrimaryKey(id) > 0) {
            //删除redis记录（读写锁）
            //TODO 待考虑失败的情况
            redisDelete(sc.getCorpEndpoint(), sc.getCompanyId());
            return ApiResult.success(null, null);
        }
        return ApiResult.failed(null, null);
    }

    @Override
    public ApiResult select(String corpEndpoint) {
        List<SyncCompanyDto_Select> dtos;
        if (StringUtils.isNotBlank(corpEndpoint))
            dtos = syncCompanyDao.selectSyncCompany(corpEndpoint);
        else
            dtos = syncCompanyDao.selectSyncCompany(null);

        return ApiResult.success(null, dtos);
    }


    /**
     * 同步组织到Redis
     */
    @Override
    public ApiResult syncToRedis() {
        List<SyncCompanyDto_Select> dtos = syncCompanyDao.selectSyncCompany(null);

        List<String> vals = Lists.newArrayList();
        dtos.forEach(sc -> vals.add(sc.getCorpEndpoint() + ":" + sc.getCompanyId()));

        RReadWriteLock lock = redissonClient.getReadWriteLock("Lock_" + RKey.CorpCompanies);
        RLock r = lock.readLock();
        r.lock(5, TimeUnit.SECONDS);

        RSet<String> set = redissonClient.getSet(RKey.CorpCompanies);
        set.clear();
        set.addAll(vals);

        r.unlock();

        return ApiResult.success(null, dtos);
    }

    /**
     * 推送组织同步指令
     */
    @Override
    public ApiResult pushSyncCmd(String syncCompanyId) {
        return null;
    }
}
