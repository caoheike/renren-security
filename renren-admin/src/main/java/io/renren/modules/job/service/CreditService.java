/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.job.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 用户
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface CreditService {
	//记录爬虫记录
	int  addLog(Map<String, String> map);
	//征信获取数据

	//查询需要推送的数据
	List<Map<String,Object>> getPushData(String type, String userStatus) throws Exception;
	//修改推送状态
	int  updataStatus(String type, String userStatus, String userCard);



}
