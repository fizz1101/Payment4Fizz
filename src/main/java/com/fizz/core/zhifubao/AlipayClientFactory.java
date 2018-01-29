package com.fizz.core.zhifubao;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.fizz.common.utils.SpringUtils;

/**
 * 支付宝API SDK默认实体类(单例)
 */
public class AlipayClientFactory {

    private static AlipayClient ourInstance = null;

    private static AlipayConf alipayConf;
    static {
        alipayConf = (AlipayConf) SpringUtils.getBean("alipayConf");
    }

    public static AlipayClient getInstance() {
        if (ourInstance == null) {
            ourInstance = new DefaultAlipayClient(alipayConf.URL, alipayConf.APPID, alipayConf.RSA_PRIVATE_KEY, alipayConf.FORMAT, alipayConf.CHARSET, alipayConf.ALIPAY_PUBLIC_KEY, alipayConf.SIGNTYPE);
        }
        return ourInstance;
    }

    private AlipayClientFactory() {
    }
}
