

package io.renren.modules.job.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.renren.common.utils.*;

import io.renren.modules.job.dao.CreditDao;

import io.renren.modules.job.entity.ScheduleJobLogEntity;
import io.renren.modules.job.service.CreditService;


import org.springframework.stereotype.Service;



import java.util.*;


@Service("creditService")
public class CreditServiceImpl extends ServiceImpl<CreditDao, ScheduleJobLogEntity> implements CreditService {


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


    /**
     *
     * @param jsonObject
     * @return
     * @throws Exception
     */



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
      //获取成功 锁定ip(开发者模式)
//        int  row= baseMapper.lockIp(ip);
//        if(row>0){
//            JSONObject.put("ip",ip);
//
//        }
    setLog("征信","ip获取成功",JSONObject.get("userCard").toString(),JSONObject.get("userName").toString(),JSONObject.get("userPwd").toString(),"",DriverUtil.getTime(),ip,JSONObject.get("userCode").toString(),JSONObject.get("callBackUrl").toString());


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








}


