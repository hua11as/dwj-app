package com.lontyu.dwjadmin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.common.utils.Query;
import com.lontyu.dwjadmin.dao.SysConfigDao;
import com.lontyu.dwjadmin.entity.SysConfigEntity;
import com.lontyu.dwjadmin.service.SysConfigService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Map;

@Service("sysConfigService")
public class SysConfigServiceImpl extends ServiceImpl<SysConfigDao, SysConfigEntity> implements SysConfigService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String paramKey = (String)params.get("paramKey");

		Page<SysConfigEntity> page = this.selectPage(
				new Query<SysConfigEntity>(params).getPage(),
				new EntityWrapper<SysConfigEntity>()
					.like(StringUtils.isNotBlank(paramKey),"param_key", paramKey)
					.eq("status", 1)
		);

		return new PageUtils(page);
	}
	
	@Override
	public void save(SysConfigEntity config) {
		this.insert(config);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(SysConfigEntity config) {
		this.updateAllColumnById(config);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateValueByKey(String key, String value) {
		baseMapper.updateValueByKey(key, value);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteBatch(Long[] ids) {
		for(Long id : ids){
			SysConfigEntity config = this.selectById(id);
		}

		this.deleteBatchIds(Arrays.asList(ids));
	}

	@Override
	public String getValue(String key) {
		SysConfigEntity config = baseMapper.queryByKey(key);
		return config == null ? null : config.getParamValue();
	}
	
//	@Override
//	public <T> T getConfigObject(String key, Class<T> clazz) {
//		String value = getValue(key);
//		if(StringUtils.isNotBlank(value)){
//			return new Gson().fromJson(value, clazz);
//		}
//
//		try {
//			return clazz.newInstance();
//		} catch (Exception e) {
//			throw new RRException("获取参数失败");
//		}
//	}
}
