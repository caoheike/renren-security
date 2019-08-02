/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gargoylesoftware.htmlunit.*;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import io.renren.common.utils.*;

import io.renren.dao.TaobaoDao;

import io.renren.entity.TokenEntity;

import io.renren.service.TaoBaoService;


import io.renren.thread.CredThread;
import io.renren.thread.TaobaoThread;
import netscape.javascript.JSObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.NestedServletException;


import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service("taoBaoService")
public class TaoBaoServiceImpl extends ServiceImpl<TaobaoDao, TokenEntity> implements TaoBaoService {




    @Override
    public String generatePage(HttpServletRequest request, String data) throws Exception {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> datas = new HashMap<>();
        String decodedData = RsaUtil.privateDecrypt(data, RsaUtil.getPrivateKey("MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAItRxewPPxqvJO2R3LDV4-IMYukr7WJ-Fyv07n4XkYvsvkXWHk5sqZuxOA54UTTGEADmEqWvGcT5PAVHFEeD7ozQ-E-m0GOJqZbMgD0Du-aX_e3mTLvSb1P0NCnKWL-vLlLHkqV_HDs5tYfPrbypfvSx0SmnrMPAR8HkO7pFjTtzAgMBAAECgYA00fD3GaS0KjkW9Rk11jIKzuVlP-lFUUbZvIf7OFZPNZfCBHcDBsGbLKpzGfy3xGvm21Owi116x3-RBKzUnFmBa-T1Z3TieXZuS14O6Qru77MmYFXLHO32GsFfTf6hKHRVQSTdDR5PG3L893MTMYrgr8YDUJ37k_ztMS9nRqJRyQJBANJLdtdnCPf_zm5_e97-BM7BHfXjg0M5ABfWS-K8ux7i6B_HGpyHGlWIV14f8Olsvf2zBJ1BU8v3vFD-DvLyikcCQQCpmVRxTOsGSNK5ThN2wXJx20B5cJ0pSyuZcYOdgrOfOp2G6rUOQONtAAbTNLA-vHWBGaGL77IUZB8Fxhj3py91AkBgBsGfnpcD17WF04TW0JuVZa2uqFM6EP8v41UHljLD6c0hJaPLMg4eXIG4o1E45cTj4ikLPddr3hYJzdk5qvLlAkAno_T-vnG4eFD4iu01tqVfQ1XgjJfPTQiVxthyelgtgW_MVxOWj0gY9AakWw5Ou5HjaPA_WvkOlxBBMzomuGb5AkBwmQ3U5pRKBLrUCppXztOR5SZO1AB66wtmi_NOENSSVclyIlXNC6oHTyL9-r-0i-YY920LbdsdhL_8XtfTBmPn"));
        JSONObject parse = JSONObject.parseObject(decodedData, JSONObject.class);
        System.out.println(parse.toJSONString());
        if (parse.size() <= 0) {
            map.put("code", "0001");
            map.put("msg", "回调地址不能为空");
            return JSON.toJSONString(map);
        }

        if (parse.get("custId") == null || parse.get("custId").equals("")) {
            map.put("code", "0002");
            map.put("msg", "custId不能为空");
            return JSON.toJSONString(map);
        }

        //获取登录页面文本
        Document doc = Jsoup.parse((new URL("https://login.m.taobao.com/login.htm?_input_charset=utf-8").openStream()), "UTF-8", "https://login.m.taobao.com/login.htm?_input_charset=utf-8");
        doc.title("淘宝授权bigyoung数据采集");
        doc.getElementsByClass("am-footer")
                .get(0)
                .attr("style", "width:100%")
                .html("<p><input type=\"checkbox\" value=\"同意授权bigyoung数据采集\" checked=\"checked\"/>同意授权bigyoung数据采集</p>");
        // doc.getElementsByClass("am-field").get(2).empty();
        doc.getElementById("username").attr("placeholder", "手机号/邮箱/会员名(不包含中文)");
        doc.getElementById("loginForm").prepend("<input type='hidden' name='callBackUrl' value='" + parse.get("callBackUrl") + "' />");
        doc.getElementById("loginForm").prepend("<input type='hidden' name='custId' value='" + parse.get("custId") + "' />");
        doc.select("meta[charset=utf-8]").get(0)
                .attr("http-equiv", "content-type")
                .attr("content", "text/html; charset=utf-8");
        doc.getElementById("loginForm").attr("action", "apis/getData");
        doc.getElementById("username").attr("style", "width:90%");
        doc.getElementById("password").attr("style", "width:90%");
        doc.getElementsByClass("am-fieldBottom").get(0)
                .attr("style", "width:90%");
        doc.getElementsByClass("am-list").get(0).attr("style", "width:90%");
        doc.getElementsByClass("other-link").get(0)
                .attr("style", "width:90%;margin:0 auto;");
        datas.put("url", "http://172.19.238.160:8081/renren-api/" + TaoBaoutil.updateAndSaveHtmlFile(doc.outerHtml(), request) + ".html");
        map.put("msg", "获取成功");
        map.put("code", "0000");
        map.put("data", datas);
        //数据日志增加
        getStatus("H5获取成功",parse.get("custId").toString());
        System.err.println(datas.get("url"));
        return JSON.toJSONString(map);

    }

    /**
     *
     * @param
     * @param
     */
    private   void getStatus(String logInfo, String custId) {
        //查询数据是否已经存在（若存在就只做更新操作）

        Map<String, Object> addLog = new HashMap<>();
        addLog.put("userStatus", logInfo);
        addLog.put("custId", custId);
        int getStatus = baseMapper.getStatus(addLog);

        if(getStatus<=0){

            baseMapper.addLog(addLog);

        }else{
            baseMapper.editStatus(addLog);
        }



    }


    @Override
    public String getData(Map<String, String> map, HttpSession session, HttpServletRequest request, HttpServletResponse responses, String callBackUrl) throws Exception {
        JSONObject datas = new JSONObject();
        JSONObject data = new JSONObject();
        WebRequest WebRequest = new WebRequest(new URL("https://login.m.taobao.com/login.htm?_input_charset=utf-8&sdkInterceptType=&ttid=h5%40iframe"));
        //设置head
        Map<String, String> headMap = new HashMap<String, String>();
        List reqParam = new ArrayList();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            reqParam.add(new NameValuePair(entry.getKey(), entry.getValue()));

        }
        headMap.put("Content-Type", "application/x-www-form-urlencoded");
        headMap.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.3; en-us; SM-N900T Build/JSS15J) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
        WebRequest.setAdditionalHeaders(headMap);
        WebRequest.setRequestParameters(reqParam);
        WebRequest.setHttpMethod(HttpMethod.POST);
        WebClient webClient = new WebClient();
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        HtmlPage page = webClient.getPage(WebRequest);
        if (page.getTitleText().contains("我的淘宝")) {
            //数据日志增加
            getStatus("登录成功",map.get("custId"));
            //开始处理数据以及推送
            TaobaoThread ts = new TaobaoThread(datas, data, webClient, callBackUrl, map.get("custId").toString());
            ts.start();

        } else {
            //登录异常返回
            loginError(page, request, responses, map.get("custId").toString(), map.get("callBackUrl").toString(), webClient);

        }


        return "提交完成";
    }

    @Override
    public int addLog(Map<String, String> map) {
        return 0;
    }


    /**
     * 登录出错
     * @param page
     * @param request
     * @param response
     * @throws Exception
     */

    private void loginError(HtmlPage page, HttpServletRequest request, HttpServletResponse response, String custId, String url, WebClient webClient) throws Exception {
        //处理安全验证
        if (page.asText().contains("我们已经发送了校验码到你的手机") || page.asText().contains("正在检测")) {
            getStatus("正在进行安全监测",custId);
            //重定向url
            String attribute = page.getElementById("verifyForm").getAttribute("action");
            //参数
            String tag = "8";
            String htoken = page.getElementsByName("htoken").get(0).getAttribute("value");
            String firstIn = "true";
            //组装url
            String urls = attribute + "?tag=" + tag + "&htoken" + htoken + "&firstIn=" + firstIn;
            HtmlPage page1 = webClient.getPage(urls);
            Document doc = Jsoup.parse(page1.asXml());
            doc.getElementById("J_Form").attr("action", "apis/getData");
            String pageHtml = TaoBaoutil.updateAndSaveHtmlFile(doc.html(), request);
            response.reset();
            PrintWriter out = response.getWriter();
            out.write("<script> location.href='http://172.19.238.160:8081/renren-api/" + pageHtml);
            out.write(".html'</script>");
            out.flush();
            out.close();


        }else{
            Document doc = Jsoup.parse(page.asXml());

            getStatus(  doc.getElementsByTag("div").get(1).text(),custId);



            doc.title("淘宝授权Bigyoung数据采集");
            doc.getElementsByClass("am-footer")
                    .get(0)
                    .attr("style", "width:100%")
                    .html("<p><input type=\"checkbox\" value=\"同意授权Bigyoung数据采集\" checked=\"checked\"/>同意授权bigyoung数据采集</p>");
//         doc.getElementsByClass("am-field").get(2).empty();
            doc.select("meta[charset=utf-8]").get(0)
                    .attr("http-equiv", "content-type")
                    .attr("content", "text/html; charset=utf-8");
            doc.getElementById("loginForm").attr("action", "apis/getData");
            doc.getElementById("loginForm").prepend("<input type='hidden' name='callBackUrl' value='" + url + "' />");
            doc.getElementById("loginForm").prepend("<input type='hidden' name='custId' value='" + custId + "' />");
            doc.getElementById("username").attr("style", "width:90%");
            doc.getElementById("password").attr("style", "width:90%");
            doc.getElementsByClass("am-fieldBottom").get(0)
                    .attr("style", "width:90%");
            doc.getElementsByClass("am-list").get(0).attr("style", "width:90%");
            doc.getElementsByClass("other-link").get(0)
                    .attr("style", "width:90%;margin:0 auto;");
            String pageHtml = TaoBaoutil.updateAndSaveHtmlFile(doc.outerHtml(), request);
            response.reset();

            PrintWriter out = response.getWriter();

            out.write("<script> location.href='http://172.19.238.160:8081/renren-api/" + pageHtml);

            out.write(".html'</script>");
            out.flush();
            out.close();
        }



    }

    public JSONArray UserDeal(WebClient webClient) throws Exception {
        webClient.getOptions().setJavaScriptEnabled(true);
        HtmlPage page = webClient.getPage("https://buyertrade.taobao.com/trade/itemlist/list_bought_items.htm?spm=" + System.currentTimeMillis());

        String dataResult = page.executeJavaScript("JSON.stringify(data)")
                .getJavaScriptResult().toString();

        JSONObject jsonObject = JSONObject.parseObject(dataResult, JSONObject.class);
        JSONArray mainOrders = JSONArray.parseArray(jsonObject.get("mainOrders").toString());
        JSONArray details = new JSONArray();
        for (int i = 0; i < mainOrders.size(); i++) {
            JSONObject detail = new JSONObject();

            detail.put("dealDetail", mainOrders.getJSONObject(i)
                    .getJSONArray("subOrders").getJSONObject(0)
                    .getJSONObject("itemInfo").getString("title"));
            detail.put("dealAmount", mainOrders.getJSONObject(i)
                    .getJSONArray("subOrders").getJSONObject(0)
                    .getJSONObject("priceInfo").getString("realTotal"));
            detail.put("dealDate", mainOrders.getJSONObject(i)
                    .getJSONObject("orderInfo").getString("createDay"));
            details.add(detail);
        }

        return details;

    }

    /**
     * 获取用户个人信息
     * @param webClient
     * @throws Exception
     */
    public JSONObject getUserInfo(WebClient webClient) throws Exception {
        JSONObject map = new JSONObject();
        webClient.getOptions().setJavaScriptEnabled(false);
        HtmlPage page = webClient.getPage("https://member1.taobao.com/member/fresh/account_management.htm?spm=a1z08.1.0.0.6cec978brdsEYe");
        //此处可能有异常
        List<HtmlElement> byXPath = page.getByXPath("//*[@id=\"main-content\"]/div/div[2]/table/tbody/tr[2]/td[1]");
        //拆分
        String[] userInfo = byXPath.get(0).asText().split("\\|");
        //姓名
        String userName = userInfo[0];
        //身份证
        String userCard = userInfo[1];
        map.put("userName", userName);
        map.put("userCard", userCard);

        return map;

    }

    /**
     * 地址数据清洗
     * @param webClient
     * @throws Exception
     */
    public JSONArray getAddress(WebClient webClient) throws Exception {
        UnexpectedPage page1 = webClient.getPage("https://api.m.taobao.com/h5/mtop.msp.qianggou.queryitembybatchid/3.1/");
        long time = System.currentTimeMillis();
//        System.err.println(page1.getWebResponse().getContentAsString()+"--");  此处有风险，可能会封ip
//        System.err.println(webClient.getCookieManager().getCookie("_m_h5_tk")+"--");
        String tb_token_ = webClient.getCookieManager().getCookie("_m_h5_tk").getValue().substring(0, webClient.getCookieManager().getCookie("_m_h5_tk").getValue().indexOf("_"));
        String datas = "{\"sn\":\"suibianchuan\"}";
        String signstat = tb_token_ + "&" + time + "&12574478&" + datas;
        String sign = DigestUtils.md5Hex(signstat);
        UnexpectedPage pageadders = webClient.getPage("http://h5api.m.taobao.com/h5/mtop.taobao.mbis.getdeliveraddrlist/1.0/?jsv=2.4.2&appKey=12574478&t=" + time + "&sign=" + sign + "&api=mtop.taobao.mbis.getDeliverAddrList&v=1.0&ecode=1&needLogin=true&dataType=jsonp&type=jsonp&callback=mtopjsonp4&data=" + URLEncoder.encode(datas));
        String addressData = pageadders.getWebResponse().getContentAsString().replace(" mtopjsonp4(", "").replace(")", "");
        //地址数据清洗
        JSONObject jsonObject = JSONObject.parseObject(addressData, JSONObject.class);
        //获取data数据
        String data = jsonObject.get("data").toString();
        //获得最终地址

        String addressDatas = JSONObject.parseObject(data, JSONObject.class).get("returnValue").toString();
        JSONArray address = JSONArray.parseArray(addressDatas);

        return address;
    }


    /**
     * 征信
     * @param
     */


    @Override
    public String creditData(String sign) throws Exception {

        Map<String,Object> check=null;
        try {
            //解密sign
            JSONObject jsonObject = JSONObject.parseObject(RsaUtil.privateDecrypt(sign, RsaUtil.getPrivateKey(RsaUtil.PRIVATE_KEY)), JSONObject.class);
            //校验CRED_USERNAME_NULL
            check=checkUser(jsonObject);
        } catch (JSONException e) {

            return JSON.toJSONString(constantUtil.dataError());

        }



        return  JSON.toJSONString(check) ;
    }



    private Map<String, Object> checkUser(JSONObject jsonObject) throws Exception {



            if (jsonObject.get("userName")==null||jsonObject.get("userName").equals("")){
                return constantUtil.error("0006");
            }
            if (jsonObject.get("userPwd")==null||jsonObject.get("userPwd").equals("")){
                return constantUtil.error("0007");
            }
            if (jsonObject.get("userCard")==null||jsonObject.get("userCard").equals("")){
                return constantUtil.error("0008");
            }
            if (jsonObject.get("userCode")==null||jsonObject.get("userCode").equals("")){
                return constantUtil.error("0009");
            }
           if (jsonObject.get("CallBankUrl")==null||jsonObject.get("CallBankUrl").equals("")){
            return constantUtil.error("0010");
            }

        //获取可用代理ip并且访问
        Map<String, Object> credIp = getCredIp(jsonObject);


        //开始执行线程 请求登录
        CredThread cr=new CredThread("",jsonObject.get("userCard").toString(),credIp.get("ip").toString(),jsonObject);
        cr.start();
        return credIp;

    }

    /**
     * 查询可用ip
     * @return
     */

    synchronized Map<String,Object> getCredIp(JSONObject JSONObject) throws Exception {
     List<Map<String, Object>> ipMap= baseMapper.getCredIp(constantUtil.Flg.TRUE.toString());

     if(ipMap.isEmpty()||ipMap.size()<=0){
         setLog("征信","目前使用人数较多，请稍后再试",JSONObject.get("userCode").toString());
         return constantUtil.credCrowding();
     }
      String  ip= ipMap.get(0).get("ip").toString();
      String prot=ipMap.get(0).get("prot").toString();
      String addre= constantUtil.agreement.HTTP+"://"+ip+":"+prot+"/renren-api/apis/getInfo";
      //获取成功 锁定ip(开发者模式)
//        int  row= baseMapper.lockIp(ip);
//        if(row>0){
//            JSONObject.put("ip",ip);
//
//        }

        setLog("征信","ip获取成功",JSONObject.get("userCode").toString());


      return  constantUtil.sb_ok("ip",addre);

    }






    private void setLog(String type, String msg, String custid) {
        Map<String,Object> logMap= new HashMap<>();
        logMap.put("type",type);
        logMap.put("userStatus",msg);
        logMap.put("custId",custid);

        if(  baseMapper.getStatus(logMap)<=0){
            baseMapper.addLog(logMap);
        }else{
            baseMapper.editStatus(logMap);
        }
    }

    private void setLog(String type, String msg, String custid,String userName,String userPwd,String path,String time) {
        Map<String,Object> logMap= new HashMap<>();
        logMap.put("type",type);
        logMap.put("userStatus",msg);
        logMap.put("custId",custid);

        if(  baseMapper.getStatus(logMap)<=0){
            baseMapper.addLog(logMap);
        }else{
            baseMapper.editStatus(logMap);
        }
    }

    /**
     * 根据账号登陆（征信）
     * @param sign
     * @return
     */
    @Override
    public String getInfo(JSONObject sign,HttpServletRequest request) {

        //征信登陆
        String loginRest = null;

        try {
            loginRest = creditLogin(sign, request);

            if(loginRest.contains("success")){

                return "success";
            }
            return "error";
        } catch (NoSuchElementException e) {
            return constantUtil.CUSTOM_ERROR("登录失败:未找到错误提示").toString();
        } catch (NestedServletException e) {
            return constantUtil.CUSTOM_ERROR("登录失败:控件出错").toString();
        } catch (IOException e) {
            return constantUtil.CUSTOM_ERROR("登录失败:代理有问题").toString();
        } catch (Throwable e){

            if(e.toString().contains(" Unable to load library")){
                return constantUtil.CUSTOM_ERROR("控件异常").toString();
            }
            if(e.toString().contains("TimeoutException")){
                return constantUtil.CUSTOM_ERROR("代理或网络异常"+e).toString();
              }
            return constantUtil.CUSTOM_ERROR("未知异常"+e).toString();
        }

        }




    private String creditLogin(JSONObject sign,HttpServletRequest request) throws Exception {

        JSONObject jsonObject = JSONObject.parseObject(setDL(sign.get("userCard").toString()));
        WebDriver ie = DriverUtil.getDriverInstance("ie",jsonObject.get("ip").toString(),Integer.parseInt(jsonObject.get("port").toString() ));
        ie.manage().timeouts().pageLoadTimeout(9, TimeUnit.SECONDS);
        ie.get("https://ipcrs.pbccrc.org.cn/page/login/loginreg.jsp");
        if( !ie.findElement(By.id("loginname")).isDisplayed()){
        return  constantUtil.YS_ERROR;
        }
        ie.findElement(By.id("loginname")).sendKeys(sign.get("userName").toString());
        ie.findElement(By.id("pass")).sendKeys();
          SendKeys.sendStr(sign.get("userPwd").toString());
        WebElement findElement = ie.findElement(By.id("imgrc"));
        // 截图
        String fileName = cutoutImg("imgrc", ie, findElement, request);

        Map<String, Object> imagev = MyCYDMDemo.ImageBySix(fileName);
        // 读取图片验证码
        String code = imagev.get("strResult").toString();
        //输入验证码
        ie.findElement(By.id("_@IMGRC@_")).sendKeys(code);
        ie.findElement(By.className("btn2")).click();
        //判断是否登录成功
        if(ie.getPageSource().contains("leftFrame")){
            setLog("征信","登录成功",sign.get("userCode").toString()); //登录成功
            ie.get("https://ipcrs.pbccrc.org.cn/reportAction.do?method=queryReport");
            ie.findElement(By.id("tradeCode")).sendKeys("bruqf8");
            ie.findElement(By.id("radiobutton1")).click();
            ie.findElement(By.id("nextstep")).click();
            Set<String> windows = ie.getWindowHandles();

            List<String> it = new ArrayList<String>(windows);
            ie.switchTo().window( it.get(1));
            if(ie.getTitle().contains("个人信用报告")){
            setLog("征信","报告获取成功",sign.get("userCode").toString());
            String pageHtml = TaoBaoutil.updateAndSaveHtmlFile(ie.getPageSource(), request,"Cred");
             setLog("征信","报告保存成功",sign.get("userCode").toString(),"","","","");
            return "success"; //报告获取成功
            }
            return "error";  //未找到报告获取失败

        }
        //不成功

            setLog("征信","登录失败",sign.get("userCode").toString());
            return constantUtil.CUSTOM_ERROR(ie.findElement(By.id("_error_field_")).getText()).toString();




    }


    /**
     * 设置代理
     * @return
     */
    private String setDL(String userCord) throws Exception {

        Date startDate;
        Date endDate;
        String time="";
        JSONObject data1;
        do {
            Result result = SendRequest.sendGet("http://http.tiqu.qingjuhe.cn/getip?num=1&type=2&pro=&city=0&yys=0&port=1&pack=22044&ts=1&ys=1&cs=1&lb=1&sb=0&pb=4&mr=0&regions=", null, null, "utf-8");
            JSONObject jsonObject = JSONObject.parseObject(result.getHtmlContent("utf-8"));
            String data= jsonObject.getString("data");
             data1=JSONObject.parseObject(JSONObject.parseArray(data).get(0).toString());


            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


            startDate = format.parse(DriverUtil.getTime());
            endDate = format.parse(data1.get("expire_time").toString());
            System.err.println("不符合");
        }while (getBetweenSecond(startDate,endDate)>50);

            time=endDate.toString();
        setLog("征信","代理获取成功",userCord);
        return  JSON.toJSONString(data1);


//        WebClient webClient=new WebClient(BrowserVersion.CHROME,data1.get("ip").toString(),Integer.parseInt(data1.get("port").toString()));
//        webClient.getOptions().setCssEnabled(false);
//        webClient.getOptions().setJavaScriptEnabled(false);
//        HtmlPage page = webClient.getPage("https://ipcrs.pbccrc.org.cn/");
//        if(page.getTitleText().equals("个人信用信息服务平台")){
//            setLog("征信","代理获取成功",userCord);
//            return  JSON.toJSONString(data1);
//        }else{
//            setLog("征信","代理获取失败",userCord);
//            return  JSON.toJSONString(constantUtil.dLerror());
//        }




    }


    public static Integer getBetweenSecond(Date startDate, Date endDate) {
        Integer seconds = 0;
        try {
            if(startDate!=null&&endDate!=null) {
                long ss = 0;
                if(startDate.before(endDate)) {
                    ss = endDate.getTime() - startDate.getTime();
                }else {
                    ss = startDate.getTime() - endDate.getTime();
                }
                seconds = Integer.valueOf((int) (ss/(1000))) ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seconds;
    }


    /**
     * 截取图片验证码
     *
     * @param str
     * @param driver
     * @param findElement
     * @throws IOException
     * @Description:
     */
    private static String cutoutImg(String str, WebDriver driver, WebElement findElement, HttpServletRequest request)
            throws IOException {
        String fileName = "";


            // 验证码路径
            String verifyImages = "C:/image/zhengxinImage/";
            File file = new File(verifyImages);
            if (!file.exists()) {
                file.mkdir();
            }
            String filename = System.currentTimeMillis() + ".png";

            java.io.File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            BufferedImage fullImg = ImageIO.read(screenshot);
            Point point = findElement.getLocation();
            int eleWidth = findElement.getSize().getWidth();
            int eleHeight = findElement.getSize().getHeight();
            BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);
            ImageIO.write(eleScreenshot, "png", screenshot);

            File screenshotLocation = new File(verifyImages + filename);
            FileUtils.copyFile(screenshot, screenshotLocation);

            fileName = verifyImages + filename;


        return fileName;
    }

    public static void main(String[] args)    {


        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDate;
            Date endDate;

                startDate = format.parse("2017-11-14 21:30:4");
                endDate = format.parse("2017-11-14 21:30:5");

//            SendKeys.sendStr("FANGFANGgaga21");

           System.out.println(getBetweenSecond(startDate,endDate));


        } catch (Exception e) {

        }

    }


}


