/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.job.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.renren.modules.job.entity.ScheduleJobLogEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 用户Token
 *
 * @author Mark sunlightcs@gmail.com
 */
@Mapper
public interface CreditDao extends BaseMapper<ScheduleJobLogEntity> {
    int addLog(Map<String, Object> map);
    int getStatus(Map<String, Object> map);
    int editStatus(Map<String, Object> map);

    //征信获取ip
    List<Map<String, Object>> getCredIp(String flg);
    //锁定ip

    int lockIp(String ip);

    List<Map<String,Object>> getPushData(String type, String userStatus);
    //修改征信推送状态
    int updataStatus(String type, String userStatus, String userCard);


}
