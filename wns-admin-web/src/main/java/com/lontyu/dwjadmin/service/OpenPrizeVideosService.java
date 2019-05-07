package com.lontyu.dwjadmin.service;

import com.baomidou.mybatisplus.service.IService;
import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.entity.BjlOpenprizeVideo;
import com.lontyu.dwjadmin.entity.VipMember;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


/**
 * 开奖视频
 * 
 */
public interface OpenPrizeVideosService extends IService<BjlOpenprizeVideo> {

	PageUtils queryPage(Map<String, Object> params);
	
	/**
	 * 开奖视频
	 */
	void update(BjlOpenprizeVideo video);

	void save(BjlOpenprizeVideo video);

	void deleteBatch(Long[] jobIds);

	void upload(MultipartFile file);
}
