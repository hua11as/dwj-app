package com.lontyu.dwjadmin.controller;


import com.lontyu.dwjadmin.common.utils.PageUtils;
import com.lontyu.dwjadmin.common.utils.R;
import com.lontyu.dwjadmin.service.MoneyRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 *  用户交易记录
 * 
 */
@RestController
@RequestMapping("/user/record")
public class UserFlowRecordsController extends AbstractController {

	@Autowired
	private MoneyRecordService moneyRecordService;

	/**
	 * 交易记录列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = moneyRecordService.queryPage(params);

		return R.ok().put("page", page);
	}
	
}
