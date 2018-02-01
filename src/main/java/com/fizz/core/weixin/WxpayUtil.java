package com.fizz.core.weixin;

import com.fizz.common.utils.SpringUtils;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付接口工具类
 * (详情见微信商户平台API https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1)
 */
public class WxpayUtil {

    private static Logger logger = LoggerFactory.getLogger(WxpayUtil.class);

    private static WxpayConf wxpayConf;
    static {
        wxpayConf = (WxpayConf) SpringUtils.getBean("wxpayConf");
    }

    private final static String REQUEST_SUCCESS = "SUCCESS";    //请求成功状态标识
    private final static String REQUEST_FAIL = "FAIL";    //请求失败状态标识

    /**
     * 创建订单
     * @param out_trade_no  商户订单号(确保唯一性)
     * @param body  商品描述
     * @param total_fee 订单金额
     * @param spbill_create_ip  终端ip
     */
    public static Map<String, String> createOrder(String out_trade_no, String body, int total_fee, String spbill_create_ip) {
        Map<String, String> resMap = new HashMap<>();
        WXPay wxpay = WxpayFactory.getInstance();
        Map<String, String> data = new HashMap<>();
        data.put("out_trade_no", out_trade_no); //商户订单号
        data.put("body", body); //商品描述
        data.put("fee_type", wxpayConf.FEE_TYPE); //币种类型(默认：CNY 人民币)
        data.put("total_fee", String.valueOf(total_fee));   //订单金额
        data.put("spbill_create_ip", spbill_create_ip);   //终端ip
        data.put("notify_url", wxpayConf.NOTIFY_URL); //异步通知地址
        data.put("trade_type", wxpayConf.TRADE_TYPE); //交易类型(JSAPI，NATIVE，APP)
        try {
            Map<String, String> resp = wxpay.unifiedOrder(data);
            if (checkResult(resp, "下单")) {
                resMap = resp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("微信下单接口报错：" + e);
        }
        return resMap;
    }

    /**
     * 查询订单
     * @param out_trade_no  商户订单号
     */
    public static Map<String, String> queryOrder(String out_trade_no) {
        Map<String, String> resMap = new HashMap<>();
        WXPay wxPay = WxpayFactory.getInstance();
        Map<String, String> data = new HashMap<>();
        data.put("out_trade_no", out_trade_no);
        try {
            Map<String, String> resp = wxPay.orderQuery(data);
            if (checkResult(resp, "查询订单")) {
                resMap = resp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("微信查询订单接口报错：" + e);
        }
        return resMap;
    }

    /**
     * 申请退款
     * @param out_trade_no  商户订单号
     * @param out_refund_no 商户退款单号
     * @param total_fee 订单总金额
     * @param refund_fee    本次退款金额
     * @return
     */
    public static Map<String, String> refund(String out_trade_no, String out_refund_no, int total_fee, int refund_fee, String refund_desc) {
        Map<String, String> resMap = new HashMap<>();
        WXPay wxPay = WxpayFactory.getInstance();
        Map<String, String> data = new HashMap<>();
        data.put("out_trade_no", out_trade_no);
        data.put("out_refund_no", out_refund_no);
        data.put("total_fee", String.valueOf(total_fee));
        data.put("refund_fee", String.valueOf(refund_fee));
        data.put("refund_desc", refund_desc);
        try {
            Map<String, String> resp = wxPay.refund(data);
            if (checkResult(resp, "退款")) {
                resMap = resp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("微信退款接口报错：" + e);
        }
        return resMap;
    }

    /**
     * 查询退款
     * @param out_trade_no  //商户订单号(优先级：out_trade_no<transaction_id<out_refund_no<refund_id)
     * @param transaction_id    //微信订单号
     * @param out_refund_no //商户退款号
     * @param refund_id //微信退款号
     * @return
     */
    public static Map<String, String> queryRefund(String out_trade_no, String transaction_id, String out_refund_no, String refund_id) {
        Map<String, String> resMap = new HashMap<>();
        WXPay wxPay = WxpayFactory.getInstance();
        Map<String, String> data = new HashMap<>();
        data.put("out_trade_no", out_trade_no);
        try {
            Map<String, String> resp = wxPay.refundQuery(data);
            if (checkResult(resp, "查询退款")) {
                resMap = resp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("微信退款查询接口报错：" + e);
        }
        return resMap;
    }

    /**
     * 关闭订单
     * @param out_trade_no  //商户订单号
     * @return
     */
    public static Map<String, String> closeOrder(String out_trade_no) {
        Map<String, String> resMap = new HashMap<>();
        WXPay wxPay = WxpayFactory.getInstance();
        Map<String, String> data = new HashMap<>();
        data.put("out_trade_no", out_trade_no);
        try {
            Map<String, String> resp = wxPay.closeOrder(data);
            if (checkResult(resp, "关闭订单")) {
                resMap = resp;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("关闭订单接口报错：" + e);
        }
        return resMap;
    }

    /**
     * 支付结果通知
     * @param xmlData   异步通知参数
     */
    public static Map<String, String> notifyPay(String xmlData) {
        Map<String, String> resMap = new HashMap<>();
        resMap.put("return_code", REQUEST_FAIL);
        try {
            Map<String, String> notifyMap = WXPayUtil.xmlToMap(xmlData);
            if (checkSign(notifyMap)) {
                // TODO 处理接收到的数据
                if (checkResult(notifyMap, "支付通知")) {
                    String out_trade_no = notifyMap.get("out_trade_no");
                    WxpayOrder wxpayOrder = WxpayCollection.getWxpay(out_trade_no);
                    if (wxpayOrder != null) {
                        Integer total_fee = Integer.parseInt(notifyMap.get("total_fee"));
                        if (wxpayOrder.getTotal_fee() == total_fee) {
                            resMap.put("return_code", "SUCCESS");
                            resMap.put("return_msg", "OK");
                        } else {
                            resMap.put("return_msg", "订单金额有误");
                        }
                    }
                } else {
                    String return_msg = notifyMap.get("return_msg") + "/" + notifyMap.get("err_code_des");
                    resMap.put("return_msg", return_msg);
                }
            } else {
                resMap.put("return_msg", "签名校验不通过");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("微信支付结果通知报错：" + e);
            resMap.put("return_mag", "服务器错误");
        }
        return resMap;
    }

    /**
     * 退款结果通知
     * @param xmlData   //异步通知参数
     * @return
     */
    public static Map<String, String> notifyRefund(String xmlData) {
        Map<String, String> resMap = new HashMap<>();
        resMap.put("return_code", REQUEST_FAIL);
        try {

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("微信退款结果通知报错：" + e);
            resMap.put("return_msg", "服务器错误");
        }
        return resMap;
    }

    /**
     * 验签
     * @param notifyMap   //异步通知参数
     * @return
     */
    public static boolean checkSign(Map<String, String> notifyMap) {
        boolean signVerified = false;
        WXPay wxPay = WxpayFactory.getInstance();
        try {
            signVerified = wxPay.isPayResultNotifySignatureValid(notifyMap);
            if (!signVerified) {
                logger.error("微信通知签名错误或无sign字段");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("微信通知验签报错：" + e);
        }
        return signVerified;
    }

    /**
     * 判断接口返回结果
     * @param resMap    接口返回内容
     * @param method    接口名称
     * @return
     */
    private static boolean checkResult(Map<String, String> resMap, String method) {
        boolean flag = false;
        logger.info("微信{}请求返回：body={}", method, resMap);
        if (REQUEST_SUCCESS.equals(resMap.get("return_code"))) {
            if (REQUEST_SUCCESS.equals(resMap.get("result_code"))) {
                flag = true;
            } else {
                logger.error("微信{}业务处理失败，code={}，msg={}", method, resMap.get("err_code"), resMap.get("err_code_des"));
            }
        } else {
            logger.error("微信{}请求失败，msg={}", method, resMap.get("return_msg"));
        }
        return flag;
    }

}
