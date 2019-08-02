package io.renren.modules.job.task;///**
// * Copyright (c) 2016-2019 人人开源 All rights reserved.
// *
// * https://www.renren.io
// *
// * 版权所有，侵权必究！
// */
//
//package io.renren.modules.job.task;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.gargoylesoftware.htmlunit.BrowserVersion;
//import com.gargoylesoftware.htmlunit.WebClient;
//import com.gargoylesoftware.htmlunit.html.HtmlPage;
//import io.renren.common.utils.Result;
//import io.renren.common.utils.SendRequest;
//import io.renren.common.utils.constantUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
///**
// * 测试定时任务(演示Demo，可删除)
// *
// * testTask为spring bean的名称
// *
// * @author Mark sunlightcs@gmail.com
// */
//@Component("GetIpTask")
//public class GetIpTask implements ITask {
//	private Logger logger = LoggerFactory.getLogger(getClass());
//
//	@Override
//	public void run(String params){
//		logger.debug("TestTask定时任务正在执行，参数为：{111111111111}", params);
//		//开始获取代理ip
//
////		try {
////		Result result = result = SendRequest.sendGet("http://http.tiqu.qingjuhe.cn/getip?num=1&type=2&pro=&city=0&yys=0&port=1&pack=22044&ts=1&ys=1&cs=1&lb=1&sb=0&pb=4&mr=0&regions=", null, null, "utf-8");
////		JSONObject jsonObject = JSONObject.parseObject(result.getHtmlContent("utf-8"));
////		String data= jsonObject.getString("data");
////		JSONObject data1=JSONObject.parseObject(JSONObject.parseArray(data).get(0).toString());
////
////
////
////		logger.debug("TestTask定时任务正在执行，参数为：{111111111111}", params);
////		} catch (IOException e) {
////			e.printStackTrace();
////		}
//
//	}
//}
