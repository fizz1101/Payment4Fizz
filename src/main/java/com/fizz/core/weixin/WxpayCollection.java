package com.fizz.core.weixin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class WxpayCollection {

    private static Logger logger = LoggerFactory.getLogger(WxpayCollection.class);

    private static ConcurrentHashMap<String, WxpayOrder> map_wxpay = new ConcurrentHashMap<>();

    public static void putWxpayMap(WxpayOrder wxpayOrder) {
        try {
            map_wxpay.put(wxpayOrder.getOut_trade_no(), wxpayOrder);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
        }
    }

    public static WxpayOrder getWxpay(String key) {
        return map_wxpay.get(key);
    }

    public static void removeWxpay(String key) {
        map_wxpay.remove(key);
    }

}
