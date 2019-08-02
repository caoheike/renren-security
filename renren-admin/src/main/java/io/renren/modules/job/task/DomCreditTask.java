

package io.renren.modules.job.task;

import com.alibaba.fastjson.JSONObject;
import io.renren.common.utils.CreditAnalysis;

import io.renren.common.utils.Result;
import io.renren.common.utils.RsaUtil;
import io.renren.common.utils.SendRequest;
import io.renren.modules.job.service.CreditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 征信数据推送类
 */
@Component("DomCreditTask")
public class DomCreditTask implements ITask {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CreditService creditService;


	@Override
	public void run(String params) {
		logger.debug("TestTask定时任务正在执行，征信数解析以及推送", params);
		//开始解析数据
		String id="";
		try {

			List<Map<String,Object>> userMap = new ArrayList<Map<String, Object>>();
			JSONObject jsonObjects=new JSONObject();
			userMap=	creditService.getPushData("征信","报告获取成功");

			if(userMap!=null&&userMap.size()>0){
				Map<String, Object> dataMap = new HashMap<String, Object>();
				for (int i = 0; i <userMap.size() ; i++) {
					String Http="http:";
					String ip=userMap.get(i).get("ip").toString();
					String path=userMap.get(i).get("path").toString();
					id=userMap.get(i).get("userNum").toString();
					dataMap.put("repor",ip+path);
					JSONObject jsonObject = CreditAnalysis.analysisCredit(dataMap);
					jsonObjects.put("data",jsonObject);
					jsonObjects.put("custId",userMap.get(i).get("userNum").toString());
					jsonObjects.put("code","0000");
					jsonObjects.put("msg","查询成功");
					//开始推送
					logger.debug("以下是报文", params);
					System.out.println(RsaUtil.privateEncrypt(jsonObjects.toJSONString(),RsaUtil.getPrivateKey(RsaUtil.PRIVATE_KEY)));
					Result rest = SendRequest.sendJsonString(userMap.get(i).get("callBackUrl").toString(), null, RsaUtil.privateEncrypt(jsonObjects.toJSONString(),RsaUtil.getPrivateKey(RsaUtil.PRIVATE_KEY)), "utf-8");
					System.out.println(rest.getHttpEntity().getContent());


					creditService.updataStatus("征信","推送成功",id);
					logger.debug("解析并推送成功", params);
					logger.debug("解析地址"+userMap.get(i).get("callBackUrl").toString(), params);
				}

			}else{
				logger.debug("征信无可推送数据", params);

			}




		} catch (Exception e) {
			//解析错误
			creditService.updataStatus("征信","推送失败"+e,id);
			e.printStackTrace();

		}



	}


}
