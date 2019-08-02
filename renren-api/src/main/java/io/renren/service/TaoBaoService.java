/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.entity.UserEntity;
import io.renren.form.LoginForm;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * 用户
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface TaoBaoService  {
	//生成淘宝h5页面
	String generatePage(HttpServletRequest request,String data) throws Exception;
	//发包获取淘宝数据
	String getData(Map<String, String> map, HttpSession session, HttpServletRequest request, HttpServletResponse response,String callBackUrl) throws Exception;
	//记录爬虫记录
	int  addLog(Map<String,String> map);
	//征信获取数据
	 String creditData(String sign) throws Exception;
	//爬取
	String getInfo(JSONObject sign,HttpServletRequest request) throws Exception;




}
