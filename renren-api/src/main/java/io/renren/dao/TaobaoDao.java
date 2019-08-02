/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.common.utils.constantUtil;
import io.renren.entity.TokenEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 用户Token
 *
 * @author Mark sunlightcs@gmail.com
 */
@Mapper
public interface TaobaoDao extends BaseMapper<TokenEntity> {
    int addLog(Map<String,Object> map);
    int getStatus(Map<String,Object> map);
    int editStatus(Map<String,Object> map);

    //征信获取ip
    List<Map<String, Object>> getCredIp(String flg);
    //锁定ip

    int lockIp(String ip);

}
