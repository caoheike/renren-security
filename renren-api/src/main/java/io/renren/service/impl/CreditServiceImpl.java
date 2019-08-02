

package io.renren.service.impl;


import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gargoylesoftware.htmlunit.*;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.renren.common.utils.*;
import io.renren.dao.CreditDao;

import io.renren.entity.TokenEntity;
import io.renren.service.CreditService;

import io.renren.thread.CredThread;

import org.apache.commons.io.FileUtils;

import org.openqa.selenium.*;


import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.web.util.NestedServletException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service("creditService")
public class CreditServiceImpl extends ServiceImpl<CreditDao, TokenEntity> implements CreditService {


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
    public int addLog(Map<String, String> map) {
        return 0;
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
            if(check.get("ip")==null||check.get("ip").equals("")){
                return  RsaUtil.privateEncrypt(JSON.toJSONString(check),RsaUtil.getPrivateKey(RsaUtil.PRIVATE_KEY));
            }
            //检查后去请求自身接口
            jsonObject.put("ip",check.get("ip").toString());
            CredThread sc=new CredThread(jsonObject.get("callBackUrl").toString(),jsonObject.get("userCard").toString(),check.get("ip").toString(),jsonObject);
            sc.start();

        } catch (JSONException e) {
            return JSON.toJSONString(constantUtil.dataError());

        }catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return JSON.toJSONString(constantUtil.CUSTOM_ERROR("请求自身接口未知异常"));

        }



        return  RsaUtil.privateEncrypt(JSON.toJSONString(check),RsaUtil.getPrivateKey(RsaUtil.PRIVATE_KEY));
    }


    /**
     * 查询需要推送的征信数据报告
     * @return
     * @throws Exception
     */

    @Override
    public List<Map<String, Object>> getPushData(String type,String userStatus) throws Exception {


        return baseMapper.getPushData(type,userStatus);
    }

    @Override
    public int updataStatus(String type,String userStatus,String userCard) {
        return baseMapper.updataStatus(type,userStatus,userCard);
    }

    @Override
    public Map<String, Object> quertyData(String data)  {


        JSONObject jsonObject = null;
        Map<String,Object> map = null;
        try {
            //接口解密数据

            String message= RsaUtil.privateDecrypt(data,RsaUtil.getPrivateKey(RsaUtil.PRIVATE_KEY));
            jsonObject=JSONObject.parseObject(message);

            if (jsonObject.isEmpty()||jsonObject.size()<=0){

                return constantUtil.CUSTOM_ERROR("参数不能为空");
            }


            map=baseMapper.quertyData(jsonObject.get("userCard").toString(),jsonObject.get("userType").toString());
            if(map.isEmpty()||map.size()<=0){
                return constantUtil.CUSTOM_ERROR("暂无数据");
            }

            map.put("code",0000);
            map.put("code","查询成功");

        }catch (Exception e){
            e.printStackTrace();
            return constantUtil.CUSTOM_ERROR("报文加密error");
        }


        return map;
    }


    /**
     *
     * @param jsonObject
     * @return
     * @throws Exception
     */

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
           if (jsonObject.get("callBackUrl")==null||jsonObject.get("callBackUrl").equals("")){
            return constantUtil.error("0010");
            }

        //获取可用代理ip并且访问
        Map<String, Object> credIp = getCredIp(jsonObject);


        return credIp;

    }

    /**
     * 查询可用ip
     * @return
     */

    synchronized Map<String,Object> getCredIp(JSONObject JSONObject) throws Exception {
     List<Map<String, Object>> ipMap= baseMapper.getCredIp(constantUtil.Flg.TRUE.toString());
     if(ipMap.isEmpty()||ipMap.size()<=0){
         setLog("征信","目前使用人数较多，请稍后再试",JSONObject.get("userCard").toString());
         return constantUtil.credCrowding();
     }
      String  ip= ipMap.get(0).get("ip").toString();
      String prot=ipMap.get(0).get("prot").toString();
      String addre= constantUtil.agreement.HTTP+"://"+ip+":"+prot+"/renren-api/api/getInfo";
         String logIp= constantUtil.agreement.HTTP+"://"+ip+":"+prot+"/renren-api/";
  //    获取成功 锁定ip(开发者模式)
        int  row= baseMapper.lockIp(ip,"false");
        if(row>0){
            JSONObject.put("ip",ip);

        }
      setLog("征信","ip获取成功",JSONObject.get("userCard").toString(),JSONObject.get("userName").toString(),JSONObject.get("userPwd").toString(),"",DriverUtil.getTime(),logIp,JSONObject.get("userCode").toString(),JSONObject.get("callBackUrl").toString());


      return  constantUtil.sb_ok("ip",addre);

    }


    /**
     *  日志状态修改
     * @param type
     * @param msg
     * @param custid
     */
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


    private void setLog(String type, String msg, String custid,String userName,String userPwd,String  Path,String time,String ip,String userCode,String callBackUrl) {
        Map<String,Object> logMap= new HashMap<>();
        logMap.put("type",type);
        logMap.put("userStatus",msg);
        logMap.put("custId",custid);
        logMap.put("userName",userName);
        logMap.put("userPwd",userPwd);
        logMap.put("path",Path);
        logMap.put("time",time);
        logMap.put("ip",ip);
        logMap.put("userCode",userCode);
        logMap.put("callBackUrl",callBackUrl);

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
    public void getInfo(JSONObject sign,HttpServletRequest request) throws Exception {

        //征信登陆
        String loginRest = null;

        try {
            loginRest = creditLogin(sign, request);


        } catch (NestedServletException e) {
            setLog("征信","登录失败控件出现问题",sign.get("userCard").toString());
        } catch (IOException e) {
            setLog("征信","登录失败代理出现问题",sign.get("userCard").toString());
        } catch (Throwable e){

            if(e.toString().contains(" Unable to load library")){
                setLog("征信","控件异常",sign.get("userCard").toString());
            }
            if(e.toString().contains("TimeoutException")){
                setLog("征信","代理或网络异常,已开始进行回调"+e,sign.get("userCard").toString());
                    //递归
                DriverUtil.ieClose();
                    loginRest = creditLogin(sign, request);

            }


        }

        DriverUtil.ieClose();
        }



    private String creditLogin(JSONObject sign,HttpServletRequest request) throws Exception {
        //设置代理
        JSONObject jsonObject = JSONObject.parseObject(setDL(sign.get("userCard").toString()));
        WebDriver ie = DriverUtil.getDriverInstance("ie",jsonObject.get("ip").toString(),Integer.parseInt(jsonObject.get("port").toString() ));
        //设置浏览器打开超时时间
           ie.manage().timeouts().pageLoadTimeout(8, TimeUnit.SECONDS);
        //打开征信
        ie.get("https://ipcrs.pbccrc.org.cn/page/login/loginreg.jsp");
        if( !ie.findElement(By.id("loginname")).isDisplayed()){

         setLog("征信","未找到登录元素",sign.get("userCard").toString());

        }
        ie.findElement(By.id("loginname")).sendKeys(sign.get("userName").toString());
        ie.findElement(By.id("pass")).sendKeys();
          SendKeys.sendStr(sign.get("userPwd").toString());
        WebElement findElement = ie.findElement(By.id("imgrc"));
        // 截图
        String fileName = cutoutImg("imgrc", ie, findElement, request);

        Map<String, Object> imagev = MyCYDMDemo.ImageBySix(fileName);

        //如果打码超时
        if(imagev.get("cid").equals("-3003")||imagev.get("strResult").toString().equals("")){
          setLog("征信","打码超时",sign.get("userCard").toString());
                //递归
            DriverUtil.ieClose();
            creditLogin(sign,request);
        }
        //打码判断 余额是否充足
        if(imagev.get("cid").equals("-1007")){
            setLog("征信","余额不足",sign.get("userCard").toString());
            DriverUtil.ieClose();

        }else{
            dm(ie,imagev,sign,request);
        }




        //释放ip
        System.out.println("释放ip");
        String ips = Matching.reg(sign.get("ip").toString(), "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}");
        baseMapper.lockIp(ips,"true");
        return sign.get("userCard").toString();


    }

    private void dm(WebDriver ie,Map<String,Object> imagev ,JSONObject sign,HttpServletRequest request) throws Exception {
        WebDriverWait wait = new WebDriverWait(ie, 20);


        // 读取图片验证码
        String code = imagev.get("strResult").toString();
        //输入验证码
        ie.findElement(By.id("_@IMGRC@_")).sendKeys(code);

        ie.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        ie.findElement(By.className("btn2")).click();
        //判断是否登录成功
        Thread.sleep(1000);
        if(ie.getPageSource().contains("leftFrame")){
            setLog("征信","登录成功",sign.get("userCard").toString()); //登录成功
            ie.get("https://ipcrs.pbccrc.org.cn/reportAction.do?method=queryReport");
            ie.findElement(By.id("tradeCode")).sendKeys(sign.get("userCode").toString());
            ie.findElement(By.id("radiobutton1")).click();
            ie.findElement(By.id("nextstep")).click();
            //等待校验验证码是否正确
            ie.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
                if(ie.findElement(By.id("codeinfo")).getText().equals("")){
                    Set<String> windows = ie.getWindowHandles();

                    List<String> it = new ArrayList<String>(windows);
                    ie.switchTo().window( it.get(1));
                    String pageHtml="";
                    ie.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
                    if(ie.getTitle().contains("个人信用报告")){
                        pageHtml = TaoBaoutil.updateAndSaveHtmlFile(ie.getPageSource(), request,"Cred");
                        //报告获取成功
                        setLog("征信","报告获取成功",sign.get("userCard").toString(),"","",pageHtml+".html","","","","");

                    }else{
                        //未找到报告获取失败
                        setLog("征信","报告获取失败未找到报告",sign.get("userCard").toString(),"","",pageHtml+".html","","","","");
                    }


                }else{
                setLog("征信","获取报告失败"+":"+ie.findElement(By.id("codeinfo")).getText(),sign.get("userCard").toString());
                  }


        }else {
                      //登录失败
                    ie.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
                    if(ie.findElement(By.id("_error_field_")).getText()!=null&&!ie.findElement(By.id("_error_field_")).getText().equals("")){
                        setLog("征信","登录失败"+":"+ie.findElement(By.id("_error_field_")).getText().toString(),sign.get("userCard").toString());
                    }else if(ie.findElement(By.id("_@MSG@_")).getText()!=null&&!ie.findElement(By.id("_@MSG@_")).getText().equals("")){
                        setLog("征信","登录失败"+":"+ie.findElement(By.id("_@MSG@_")).getText().toString(),sign.get("userCard").toString());
                        //验证码输入错误，重新进行操作！递归
                        DriverUtil.ieClose();
                        creditLogin(sign,request);
                    }else{
                        setLog("征信","登录失败"+":"+"登录出现无法捕捉的信息",sign.get("userCard").toString());
                    }
        }
    }


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
        }while (getBetweenSecond(startDate,endDate)<50);

        time=endDate.toString();
        setLog("征信","代理获取成功",userCord);
        return  JSON.toJSONString(data1);



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

            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
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



}


