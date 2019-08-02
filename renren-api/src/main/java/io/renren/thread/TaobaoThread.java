package io.renren.thread;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.WebClient;
import io.renren.common.utils.Result;
import io.renren.common.utils.RsaUtil;
import io.renren.common.utils.SendRequest;
import io.renren.service.impl.TaoBaoServiceImpl;
import org.apache.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TaobaoThread extends  Thread {


    private JSONObject data;
    private  JSONObject datas;
    private  WebClient webClient;
    private  String callBackUrl;
    private  String custId;

    public TaobaoThread(JSONObject datas, JSONObject data, WebClient webClient,String callBackUrl,String custId)
    {
        this.datas = datas;
        this.data = data;
        this.webClient = webClient;
        this.callBackUrl=callBackUrl;
        this.custId=custId;
    }


public void run() {
        try {

            System.err.println("开始了");

            TaoBaoServiceImpl aa=new TaoBaoServiceImpl();
            //登录成功获取 地址数据
            JSONArray address = aa.getAddress( webClient );


            //获取个人信息
            JSONObject userInfo = aa.getUserInfo( webClient );
            //获取交易详情
            JSONArray dealInfo = aa.UserDeal( webClient );

            datas.put( "code","0000" );
            datas.put( "msg","获取成功" );
            datas.put( "userName",userInfo.get( "userName" ) );
            datas.put( "userCard",userInfo.get( "userCard" ).toString().replaceAll( "已认证" ,"").trim());
            datas.put( "custId",custId);
            data.put("dealInfo",userInfo);
            data.put("address",address);
            data.put( "dealInfo",dealInfo );
            datas.put( "data",data );
            //数据推送
            System.err.println( JSON.toJSONString(datas) );

          String encodedData = RsaUtil.privateEncrypt(JSON.toJSONString(datas),RsaUtil.getPrivateKey(RsaUtil.PRIVATE_KEY));

         System.err.println(callBackUrl);
         Result result = SendRequest.sendJsonString( callBackUrl, null, encodedData, "utf-8" );
            HttpEntity httpEntity = result.getHttpEntity();
            System.out.println(httpEntity.getContent()+"--------------------------");
                if(httpEntity.getContent().toString().contains( "0000" )){
                    System.err.println("推送成功 ");
                }else{

                    System.err.println("推送失败 ");

                }


        }catch (Exception e){
            System.out.println(e);
        }


}



}
