package com.fizz.core.zhifubao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 支付宝支付参数
 */
@Component
@ConfigurationProperties(prefix = "alipay")
@PropertySource("classpath:conf/alipay.yml")
public class AlipayConf {

    // 商户appid
    @Value("${APPID}")
    public String APPID = "";
    // 私钥 pkcs8格式的
    @Value("${RSA_PRIVATE_KEY}")
    public String RSA_PRIVATE_KEY = "";
    // 支付宝公钥
    @Value("${ALIPAY_PUBLIC_KEY}")
    public String ALIPAY_PUBLIC_KEY = "";
    // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    @Value("${NOTIFY_URL}")
    public String NOTIFY_URL = "";
    // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
    @Value("${RETURN_URL}")
    public String RETURN_URL = "";
    // 请求网关地址
    @Value("${URL}")
    public String URL = "https://openapi.alipay.com/gateway.do";
    // 编码
    @Value("${CHARSET}")
    public String CHARSET = "UTF-8";
    // 返回格式
    @Value("${FORMAT}")
    public String FORMAT = "json";
    // 签名加密方式
    @Value("${SIGNTYPE}")
    public String SIGNTYPE = "RSA2";

    //以下为自定义参数
    //支付超时时间
    @Value("${PAY_TIMEOUT}")
    public Long PAY_TIMEOUT = 1800000L;
    //请求失败次数
    @Value("${FAIL_COUNT}")
    public Integer FAIL_COUNT = 3;

}
