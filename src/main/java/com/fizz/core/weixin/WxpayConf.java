package com.fizz.core.weixin;

import com.github.wxpay.sdk.WXPayConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * 微信支付参数
 */
@Component
@ConfigurationProperties(prefix = "wxpay")
@PropertySource("classpath:conf/wxpay.yml")
public class WxpayConf implements WXPayConfig {

    //公众账号ID
    @Value("${APPID}")
    public String APPID;
    //商户号
    @Value("${MCH_ID}")
    public String MCH_ID;
    //秘钥
    @Value("${KEY}")
    public String KEY;
    //通知地址
    @Value("${NOTIFY_URL}")
    public String NOTIFY_URL;
    //交易类型
    @Value("${TRADE_TYPE}")
    public String TRADE_TYPE;
    //币种类型(单位：分)
    @Value("${FEE_TYPE}")
    public String FEE_TYPE="CNY";

    //请求超时
    @Value("${HTTP_CONNECT_TIMEOUT}")
    private Integer HTTP_CONNECT_TIMEOUT;
    //读取超时
    @Value("${HTTP_READ_TIMEOUT}")
    private Integer HTTP_READ_TIMEOUT;

    //证书
    private byte[] certData;

    public WxpayConf() throws Exception {
        String certPath = "/path/to/apiclient_cert.p12";
        File file = new File(certPath);
        if (file != null && file.exists()) {
            InputStream certStream = new FileInputStream(file);
            this.certData = new byte[(int) file.length()];
            certStream.read(this.certData);
            certStream.close();
        }
    }

    @Override
    public String getAppID() {
        return APPID;
    }

    @Override
    public String getMchID() {
        return MCH_ID;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public InputStream getCertStream() {
        return null;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return 0;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return 0;
    }

}
