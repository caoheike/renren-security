package io.renren.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.WebClient;
import io.renren.common.utils.DriverUtil;
import io.renren.common.utils.Result;
import io.renren.common.utils.RsaUtil;
import io.renren.common.utils.SendRequest;
import io.renren.service.CreditService;
import io.renren.service.impl.TaoBaoServiceImpl;
import org.apache.http.HttpEntity;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class CredThread extends  Thread {

    @Autowired
    private CreditService creditService;


    private  String callBackUrl;
    private  String custId;
    private  String addre;
    private  JSONObject  data;

    public CredThread( String callBackUrl, String custId,String addre,JSONObject data )
    {

        this.callBackUrl=callBackUrl;
        this.custId=custId;
        this.addre=addre;
        this.data=data;
    }


public void run() {
    System.out.println("开始了");

    try {
        //开始请求征信接口

        Result result = SendRequest.sendJsonString(addre, null, data.toJSONString(), "utf-8");



    } catch (Exception e) {
        creditService.updataStatus("征信","",custId);

    }


}



}
