/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.controller;

import io.renren.service.TaoBaoService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.renren.annotation.Login;
import io.renren.common.utils.R;
import io.renren.common.utils.RsaUtil;
import io.renren.common.validator.ValidatorUtils;
import io.renren.form.LoginForm;
import io.renren.service.TokenService;
import io.renren.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import netscape.javascript.JSObject;
import org.apache.commons.codec.binary.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 淘宝接口
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/apis")
@Api(tags="淘宝接口")
public class ApiTaoBaoController {
    @Autowired
    private TaoBaoService taobaoService;



    @PostMapping("getH5")
    @ApiOperation("生成淘宝H5页面")
    public String GetH5(@RequestBody String  callBackUrl,HttpServletRequest request)throws Exception{

        return RsaUtil.privateEncrypt(taobaoService.generatePage(request,callBackUrl), RsaUtil.getPrivateKey(RsaUtil.PRIVATE_KEY));

    }

    @PostMapping("getData")
    @ApiOperation("淘宝登录获取数据")
    public String Login(@RequestParam Map<String, String> map, HttpSession session, HttpServletRequest request, HttpServletResponse response)throws Exception{

        return  taobaoService.getData(map,session,request,response,  map.get( "callBackUrl" ));

    }










}
