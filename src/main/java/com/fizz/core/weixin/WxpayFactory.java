package com.fizz.core.weixin;

import com.fizz.common.utils.SpringUtils;
import com.github.wxpay.sdk.WXPay;

public class WxpayFactory {

    private static WXPay ourInstance;

    private static WxpayConf wxpayConf;
    static {
        wxpayConf = (WxpayConf) SpringUtils.getBean("wxpayConf");
    }

    public static WXPay getInstance() {
        if (ourInstance == null) {
            ourInstance = new WXPay(wxpayConf);
        }
        return ourInstance;
    }

    private WxpayFactory() {
    }
}
