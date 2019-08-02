package io.renren.common.utils;

import java.util.HashMap;

public class constantUtil extends HashMap<String, Object> {

    //征信
    public   static  final  String  CRED_0005="当前使用人数过多，请稍后再试";
    public   static  final  String  CRED_USERNAME_NULL="账号不能为空";
    public   static  final  String  CRED_USERPWD_NULL="密码不能为空";
    public   static  final  String  CRED_USERCODE_NULL="验证码不能为空";
    public   static  final  String  CRED_USERCARD_NULL="身份证不能为空";

    public   static  final  String  CRED_CALLBANKURL_NULL="回调地址不能为空";
    public   static  final  String  CRED_SUCCESS="查询成功";
    public   static  final  String  DATA_ERROR="数据异常";
    public   static  final  String  DL_ERROE="代理ip异常";

    public   static  final  String  SUB_OK="提交成功";
    public   static  final  String  YS_ERROR="找不到元素";




    public enum Flg {
        TRUE,FALSE
    }
    public enum agreement {
        HTTP
    }

    public static constantUtil error(String code){
        constantUtil cu=new constantUtil();
        if(code.equals("0006")){
            cu.put("msg",CRED_USERNAME_NULL);
            cu.put("code",code);
        }
        if(code.equals("0007")){
            cu.put("msg",CRED_USERPWD_NULL);
            cu.put("code","0007");
        }
        if(code.equals("0008")){
            cu.put("msg",CRED_USERCODE_NULL);
            cu.put("code","0008");
        }
        if(code.equals("0009")){
            cu.put("msg",CRED_USERCARD_NULL);
            cu.put("code","0009");
        }
        if(code.equals("0010")){
            cu.put("msg",CRED_CALLBANKURL_NULL);
            cu.put("code","0010");
        }
        return  cu;

    }

    public static constantUtil success(){
        constantUtil cu=new constantUtil();
        cu.put("msg",CRED_SUCCESS);
        cu.put("code","0000");
        return  cu;

    }

    public static constantUtil sb_ok(){
        constantUtil cu=new constantUtil();
        cu.put("msg",SUB_OK);
        cu.put("code","1111");
        return  cu;

    }
    public static constantUtil sb_ok(String key ,String val){
        constantUtil cu=new constantUtil();
        cu.put("msg",SUB_OK);
        cu.put("code","1111");
        cu.put(key,val);

        return  cu;

    }



    public static constantUtil dataError(){
        constantUtil cu=new constantUtil();
        cu.put("msg",DATA_ERROR);
        cu.put("code","0010");

        return  cu;

    }

    public static constantUtil credCrowding(){
        constantUtil cu=new constantUtil();
        cu.put("msg",CRED_0005);
        cu.put("code","0011");

        return  cu;

    }


    public static constantUtil dLerror(){
        constantUtil cu=new constantUtil();
        cu.put("msg",DL_ERROE);
        cu.put("code","0012");

        return  cu;

    }


    public static constantUtil YS_ERROR(){
        constantUtil cu=new constantUtil();
        cu.put("msg",YS_ERROR);
        cu.put("code","0013");

        return  cu;

    }
//custom兹定于
    public static constantUtil  CUSTOM_ERROR(String msg){
        DriverUtil.ieClose();
        constantUtil cu=new constantUtil();
        cu.put("msg",msg);
        cu.put("code","0014");

        return  cu;

    }





    @Override
    public  constantUtil put(String key, Object value) {
        super.put(key, value);
        return this;
    }

}


