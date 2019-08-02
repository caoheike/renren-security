/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.renren.annotation.Login;
import io.renren.annotation.LoginUser;
import io.renren.common.utils.R;
import io.renren.common.utils.RsaUtil;
import io.renren.entity.UserEntity;
import io.renren.service.CreditService;
import io.renren.service.TaoBaoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 测试接口
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/api")
@Api(tags="征信接口")
public class ApiCreditController {

        @Autowired
        private CreditService creditService;

        /**
         * 征信接口
         *
         * @param
         * @param
         * @param request
         * @param response
         * @return
         * @throws Exception
         */

        @PostMapping("creditData")
        @ApiOperation("获取征信数据")
        public String creditData(@RequestBody String sign, HttpServletRequest request, HttpServletResponse response) throws Exception {


            return creditService.creditData(sign);


        }


        @PostMapping("getInfo")
        @ApiOperation("获取征信数据")
        public void getInfo(@RequestBody String data, HttpServletRequest request) throws Exception {


            System.out.println(data);
         creditService.getInfo(JSONObject.parseObject(data), request);


        }

    /**
     * 征信客户查询接口
     * @param data
     * @param request
     * @throws Exception
     */
    @PostMapping("quertyData")
    @ApiOperation("获取征信数据")
    public String quertyData(@RequestBody String data, HttpServletRequest request) throws Exception {



    return   RsaUtil.privateEncrypt(JSON.toJSONString( creditService.quertyData(data)),RsaUtil.getPrivateKey(RsaUtil.PRIVATE_KEY));
    }


    }
