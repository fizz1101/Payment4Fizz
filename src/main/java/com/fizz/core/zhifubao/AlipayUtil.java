package com.fizz.core.zhifubao;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayResponse;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.fizz.common.utils.SpringUtils;
import com.fizz.core.utils.DateUtils;
import com.fizz.core.utils.RegexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Map;

/**
 * 支付宝接口工具类
 * (详情见支付宝交易开发者平台API https://docs.open.alipay.com/api_1)
 */
public class AlipayUtil {

    private static Logger logger = LoggerFactory.getLogger(AlipayUtil.class);
    private static AlipayConf alipayConf;
    static {
        alipayConf = (AlipayConf) SpringUtils.getBean("alipayConf");
    }

    private static final String VERSION = "1.0";

    /**
     * 接口：alipay.trade.wap.pay
     * 创建订单form(手机端)
     * @param out_trade_no  商户订单号
     * @param subject   订单标题
     * @param total_amount  订单金额
     * @return 下单请求所需的完整的form表单html
     */
    public static String createOrderFormPhone(String out_trade_no, String subject, Double total_amount) {
        //AlipayClient alipayClient = new DefaultAlipayClient(alipayConf.URL, alipayConf.APPID, alipayConf.RSA_PRIVATE_KEY, alipayConf.FORMAT, alipayConf.CHARSET, alipayConf.ALIPAY_PUBLIC_KEY, alipayConf.SIGNTYPE);
        AlipayClient alipayClient = AlipayClientFactory.getInstance();
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();    //创建API对应的request
        alipayRequest.setNotifyUrl(alipayConf.NOTIFY_URL);
        alipayRequest.setReturnUrl(alipayConf.RETURN_URL);
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", out_trade_no);
        bizContent.put("subject", subject);
        bizContent.put("total_amount", total_amount);
        alipayRequest.setBizContent(bizContent.toString()); //填充业务参数
        String form = "";
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody();   //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("创建订单form(phone)报错" + e);
        }
        return form;
    }

    /**
     * 接口：alipay.trade.page.pay
     * 创建订单form(PC端)
     * @param out_trade_no  商户订单号
     * @param subject   订单标题
     * @param total_amount  订单金额
     * @return 下单请求所需的完整的form表单html
     */
    public static String createOrderFormPC(String out_trade_no, String subject, Double total_amount) {
        AlipayClient alipayClient = AlipayClientFactory.getInstance();
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setNotifyUrl(alipayConf.NOTIFY_URL);
        alipayRequest.setReturnUrl(alipayConf.RETURN_URL);
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", out_trade_no);
        bizContent.put("subject", subject);
        bizContent.put("total_amount", total_amount);
        alipayRequest.setBizContent(bizContent.toString());
        String form = "";
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody();   //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("创建订单form(PC)报错" + e);
        }
        return form;
    }

    /**
     * 接口：alipay.trade.create
     * 创建订单
     * @param out_trade_no  //商户订单号
     * @param total_amount  //订单总金额
     * @param subject   //订单标题
     * @return
     */
    public static JSONObject createOrder(String out_trade_no, Double total_amount, String subject) {
        JSONObject res = new JSONObject();
        AlipayClient alipayClient = AlipayClientFactory.getInstance();
        AlipayTradeCreateRequest alipayRequest = new AlipayTradeCreateRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", out_trade_no);
        bizContent.put("total_amount", total_amount);
        bizContent.put("subject", subject);
        alipayRequest.setBizContent(bizContent.toString());
        try {
            AlipayTradeCreateResponse alipayResponse = alipayClient.execute(alipayRequest);
            logger.info("" + alipayResponse.getBody());
            res = returnDefaultParam(alipayResponse);
            if (alipayResponse.isSuccess()) {
                res.put("out_trade_no", alipayResponse.getOutTradeNo());    //商户订单号
                res.put("trade_no", alipayResponse.getTradeNo());   //支付宝交易号
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("" + e);
        }
        return res;
    }

    /**
     * 接口：alipay.trade.precreate
     * 交易预创建(生成二维码，扫码支付)
     * @param out_trade_no  //商户订单号
     * @param total_amount  //订单总金额
     * @param subject   //订单标题
     * @return
     */
    public static JSONObject precreate(String out_trade_no, Double total_amount, String subject) {
        JSONObject res = new JSONObject();
        AlipayClient alipayClient = AlipayClientFactory.getInstance();
        AlipayTradePrecreateRequest alipayRequest = new AlipayTradePrecreateRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", out_trade_no);
        bizContent.put("total_amount", total_amount);
        bizContent.put("subject", subject);
        alipayRequest.setBizContent(bizContent.toString());
        try {
            AlipayTradePrecreateResponse alipayResponse = alipayClient.execute(alipayRequest);
            logger.info("支付预创建接口返回Body：" + alipayResponse.getBody());
            res = returnDefaultParam(alipayResponse);
            if (alipayResponse.isSuccess()) {
                res.put("out_trade_no", alipayResponse.getOutTradeNo());    //商户订单号
                res.put("qr_code", alipayResponse.getQrCode()); //当前预下单请求生成的二维码码串(二维码请自行生成)
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("支付预创建接口报错：" + e);
        }
        return res;
    }

    /**
     * 接口：alipay.trade.query
     * 查询订单(当发生异常，未收到通知时，调用此接口查询交易状态)
     * @param out_trade_no  商户订单号
     * @param trade_no  支付宝28位交易号(与trade_no二者至少传一个,优先级高)
     * @return
     */
    public static JSONObject queryOrder(String out_trade_no, String trade_no) {
        JSONObject res = new JSONObject();
        AlipayClient alipayClient = AlipayClientFactory.getInstance();
        AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("trade_no", trade_no);
        bizContent.put("out_trade_no", out_trade_no);
        alipayRequest.setBizContent(bizContent.toString());
        try {
            AlipayTradeQueryResponse alipayResponse = alipayClient.execute(alipayRequest);
            logger.info("查询订单接口返回Body：" + alipayResponse.getBody());
            res = returnDefaultParam(alipayResponse);
            if (alipayResponse.isSuccess()) {
                res.put("trade_no", alipayResponse.getTradeNo());   //支付宝交易号
                res.put("out_trade_no", alipayResponse.getOutTradeNo());    //商户订单号
                res.put("trade_status", alipayResponse.getTradeStatus());   //交易状态
                res.put("total_amount", alipayResponse.getTotalAmount());   //交易金额
                res.put("buyer_user_id", alipayResponse.getBuyerUserId());  //买家支付宝id
                res.put("buyer_logon_id", alipayResponse.getBuyerLogonId());    //买家支付宝账号
                res.put("fund_bill_list", alipayResponse.getFundBillList());    //交易支付使用的资金渠道
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("查询订单接口报错：" + e);
        }
        return res;
    }

    /**
     * 接口：alipay.trade.pay
     * 支付(使用扫码设备，将二维码或条码信息/声波信息通过本接口上送至支付宝发起支付；或者交易发生异常时，手动调用此接口执行支付)
     * @param out_trade_no  商户订单号
     * @param scene 支付场景(条码/声波)
     * @param auth_code 支付授权码
     * @param subject   订单标题
     * @return
     */
    public static JSONObject payOrder(String out_trade_no, String scene, String auth_code, String subject) {
        JSONObject res = new JSONObject();
        AlipayClient alipayClient = AlipayClientFactory.getInstance();
        AlipayTradePayRequest alipayRequest = new AlipayTradePayRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", out_trade_no);
        bizContent.put("scene", scene);
        bizContent.put("auth_code", auth_code);
        bizContent.put("subject", subject);
        alipayRequest.setBizContent(alipayRequest.toString());
        try {
            AlipayTradePayResponse alipayResponse = alipayClient.execute(alipayRequest);
            logger.info("支付接口返回Body：" + alipayResponse.getBody());
            res = returnDefaultParam(alipayResponse);
            if (alipayResponse.isSuccess()) {
                res.put("trade_no", alipayResponse.getTradeNo());   //支付宝交易号
                res.put("out_trade_no", alipayResponse.getOutTradeNo());    //商户订单号
                res.put("total_amount", alipayResponse.getTotalAmount());   //交易金额
                res.put("receipt_amount", alipayResponse.getReceiptAmount());   //实收金额
                res.put("gmt_payment", alipayResponse.getGmtPayment()); //交易支付时间
                res.put("buyer_user_id", alipayResponse.getBuyerUserId());  //买家支付宝id
                res.put("buyer_logon_id", alipayResponse.getBuyerLogonId());    //买家支付宝账号
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("支付接口报错：" + e);
        }
        return res;
    }

    /**
     * 接口：alipay.trade.refund
     * 退款
     * @param out_trade_no  商户订单很号
     * @param trade_no  支付宝28位交易号(与trade_no二者至少传一个,优先级高)
     * @param out_request_no    本次退款请求流水号
     * @param refund_amount 本次退款金额
     * @return
     */
    public static JSONObject refund(String out_trade_no, String trade_no, String out_request_no, String refund_amount) {
        JSONObject res = new JSONObject();
        AlipayClient alipayClient = AlipayClientFactory.getInstance();
        AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("trade_no", trade_no);
        bizContent.put("out_trade_no", out_trade_no);
        bizContent.put("out_request_no", out_request_no);
        bizContent.put("refund_amount", refund_amount);
        alipayRequest.setBizContent(bizContent.toString());
        try {
            AlipayTradeRefundResponse alipayResponse = alipayClient.execute(alipayRequest);
            logger.info("退款接口返回Body：" + alipayResponse.getBody());
            res = returnDefaultParam(alipayResponse);
            if (alipayResponse.isSuccess()) {
                res.put("trade_no", alipayResponse.getTradeNo());   //支付宝交易号
                res.put("out_trade_no", alipayResponse.getOutTradeNo());    //商户订单号
                res.put("fund_change", alipayResponse.getFundChange()); //本次退款是否发生资金变化
                res.put("refund_fee", alipayResponse.getRefundFee());   //退款总金额
                res.put("gmt_refund_pay", alipayResponse.getGmtRefundPay());    //退款支付时间
                res.put("buyer_user_id", alipayResponse.getBuyerUserId());  //买家支付宝id
                res.put("buyer_logon_id", alipayResponse.getBuyerLogonId());    //买家支付宝账号
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("退款接口报错：" + e);
        }
        return res;
    }

    /**
     * 接口：alipay.trade.fastpay.refund.query
     * 交易退款查询
     * @param out_trade_no  商户订单很号
     * @param trade_no  支付宝28位交易号(与trade_no二者至少传一个,优先级高)
     * @param out_request_no    本次退款请求流水号
     * @return
     */
    public static JSONObject queryRefund(String out_trade_no, String trade_no, String out_request_no) {
        JSONObject res = new JSONObject();
        AlipayClient alipayClient = AlipayClientFactory.getInstance();
        AlipayTradeFastpayRefundQueryRequest alipayRequest = new AlipayTradeFastpayRefundQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("trade_no", trade_no);
        bizContent.put("out_trade_no", out_trade_no);
        bizContent.put("out_request_no", out_request_no);
        alipayRequest.setBizContent(bizContent.toString());
        try {
            AlipayTradeFastpayRefundQueryResponse alipayResponse = alipayClient.execute(alipayRequest);
            logger.info("退款查询接口返回Body：" + alipayResponse.getBody());
            res = returnDefaultParam(alipayResponse);
            if (alipayResponse.isSuccess()) {
                res.put("trade_no", alipayResponse.getTradeNo());   //支付宝交易号
                res.put("out_trade_no", alipayResponse.getOutTradeNo());    //商户订单号
                res.put("out_request_no", alipayResponse.getOutRequestNo());    //本次退款请求流水号
                res.put("total_amount", alipayResponse.getTotalAmount());   //该笔退款所属订单总金额
                res.put("refund_amount", alipayResponse.getRefundAmount()); //退款金额
                res.put("refund_reason", alipayResponse.getRefundReason()); //退款原因
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("退款查询接口报错：" + e);
        }
        return res;
    }

    /**
     * 接口：alipay.trade.cancel
     * 撤销交易(只有发生支付系统超时或者支付结果未知时可调用撤销)
     * @param out_trade_no  商户订单号
     * @param trade_no  支付宝28位交易号(与trade_no二者至少传一个,优先级高)
     * @return
     */
    public static JSONObject cancelTrade(String out_trade_no, String trade_no) {
        JSONObject res = new JSONObject();
        AlipayClient alipayClient = AlipayClientFactory.getInstance();
        AlipayTradeCancelRequest alipayRequest = new AlipayTradeCancelRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", out_trade_no);
        bizContent.put("trade_no", trade_no);
        alipayRequest.setBizContent(bizContent.toString());
        try {
            AlipayTradeCancelResponse alipayResponse = alipayClient.execute(alipayRequest);
            logger.info("撤销交易接口返回Body：" + alipayResponse.getBody());
            res = returnDefaultParam(alipayResponse);
            if (alipayResponse.isSuccess()) {
                res.put("trade_no", alipayResponse.getTradeNo());   //支付宝交易号
                res.put("out_trade_no", alipayResponse.getOutTradeNo());    //商户订单号
                res.put("retry_flag", alipayResponse.getRetryFlag());   //是否需要重试(Y/N)
                res.put("action", alipayResponse.getAction());  //本次撤销触发的交易动作(close/refund)
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("撤销交易接口报错：" + e);
        }
        return res;
    }

    /**
     * 接口：alipay.trade.order.settle
     * 结算订单(用于在线下场景交易支付后，进行结算)
     * @param out_request_no    结算请求流水号
     * @param trade_no  商户订单号
     * @return
     */
    public static JSONObject settleOrder(String out_request_no,String trade_no) {
        JSONObject res = new JSONObject();
        AlipayClient alipayClient = AlipayClientFactory.getInstance();
        AlipayTradeOrderSettleRequest alipayRequest = new AlipayTradeOrderSettleRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_request_no", out_request_no);
        bizContent.put("trade_no", trade_no);
        alipayRequest.setBizContent(bizContent.toString());
        try {
            AlipayTradeOrderSettleResponse alipayResponse = alipayClient.execute(alipayRequest);
            logger.info("结算订单接口返回Body：" + alipayResponse.getBody());
            res = returnDefaultParam(alipayResponse);
            if (alipayResponse.isSuccess()) {
                res.put("trade_no", alipayResponse.getTradeNo());   //支付宝交易号
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("结算订单接口报错" + e);
        }
        return res;
    }

    /**
     * 接口：alipay.trade.close
     * 关闭交易(用户在一定时间内未进行支付，可调用该接口直接将未付款的交易进行关闭)
     * @param out_trade_no  商户订单很号
     * @param trade_no  支付宝28位交易号(与trade_no二者至少传一个,优先级高)
     * @param operator_id   卖家端自定义的的操作员 ID
     * @return
     */
    public static JSONObject closeTrade(String out_trade_no, String trade_no, String operator_id) {
        JSONObject res = new JSONObject();
        AlipayClient alipayClient = AlipayClientFactory.getInstance();
        AlipayTradeCloseRequest alipayRequest = new AlipayTradeCloseRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", out_trade_no);
        bizContent.put("trade_no", trade_no);
        bizContent.put("operator_id", operator_id);
        alipayRequest.setBizContent(bizContent.toString());
        try {
            AlipayTradeCloseResponse alipayResponse = alipayClient.execute(alipayRequest);
            logger.info("关闭交易接口返回Body：" + alipayResponse.getBody());
            res = returnDefaultParam(alipayResponse);
            if (alipayResponse.isSuccess()) {
                res.put("trade_no", alipayResponse.getTradeNo());   //支付宝交易号
                res.put("out_trade_no", alipayResponse.getOutTradeNo());    //商户订单号
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("关闭交易接口报错：" + e);
        }
        return res;
    }

    /**
     * 接口：alipay.data.dataservice.bill.downloadurl.query
     * 获取账单地址
     * @param bill_type 固定传入trade
     * @param bill_data 账单日期(最晚是昨天)
     * @return
     */
    public static String billAddress(String bill_type, String bill_data) {
        String res = "";
        AlipayClient alipayClient = AlipayClientFactory.getInstance();
        AlipayDataDataserviceBillDownloadurlQueryRequest alipayRequest = new AlipayDataDataserviceBillDownloadurlQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("bill_typw", bill_type);
        bizContent.put("bill_data", bill_data);
        alipayRequest.setBizContent(bizContent.toString());
        try {
            AlipayDataDataserviceBillDownloadurlQueryResponse alipayResponse = alipayClient.execute(alipayRequest);
            logger.info("获取账单地址接口返回Body：" + alipayResponse.getBody());
            if (alipayResponse.isSuccess()) {
                res = alipayResponse.getBillDownloadUrl();  //账单地址
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("获取账单地址接口报错：" + e);
        }
        return res;
    }

    /**
     * 下载账单
     * @param bill_url  账单地址
     * @param file_path 账单文件输出目录
     * @return 账单文件
     */
    public static void downloadBill(String bill_url, String file_path) {
        //将接口返回的对账单下载地址传入urlStr
        String urlStr = bill_url;
        //指定希望保存的文件路径
        String filePath = file_path;
        URL url = null;
        HttpURLConnection httpUrlConnection = null;
        InputStream fis = null;
        FileOutputStream fos = null;
        try {
            url = new URL(urlStr);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setConnectTimeout(5 * 1000);
            httpUrlConnection.setDoInput(true);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.setRequestProperty("Charset", "UTF-8");
            httpUrlConnection.connect();
            fis = httpUrlConnection.getInputStream();
            byte[] temp = new byte[1024];
            int b;
            fos = new FileOutputStream(new File(filePath));
            while ((b = fis.read(temp)) != -1) {
                fos.write(temp, 0, b);
                fos.flush();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            logger.error(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.toString());
        } finally {
            try {
                if(fis!=null) fis.close();
                if(fos!=null) fos.close();
                if(httpUrlConnection!=null) httpUrlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.toString());
            }
        }
    }

    /**
     * 返回公共出参
     * @param alipayResponse
     * @return
     */
    public static JSONObject returnDefaultParam(AlipayResponse alipayResponse) {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("code", alipayResponse.getCode());
        jsonObj.put("msg", alipayResponse.getMsg());
        jsonObj.put("sub_code", alipayResponse.getSubCode());
        jsonObj.put("sub_msg", alipayResponse.getSubMsg());
        return jsonObj;
    }

    /**
     * 异步通知验签
     * @param params    异步通知传来的参数
     * @return
     */
    public static boolean checkSign(String params) {
        String sign = RegexUtil.getUrlParam(params,"sign", 2);
        String[] rName = new String[] {"sign", "sign_type"};
        params = RegexUtil.removeUrlParams(params, rName);
        boolean signVerified = false;
        try {
            //调用SDK验证签名
            signVerified = AlipaySignature.rsaCheck(params, sign, alipayConf.ALIPAY_PUBLIC_KEY, alipayConf.CHARSET, alipayConf.SIGNTYPE);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("验签接口报错：" + e);
        }
        return signVerified;
    }

    /**
     * 异步通知验签
     * @param paramMap    异步通知传来的参数
     * @return
     */
    public static boolean checkSignOther(Map<String, String> paramMap) {
        boolean signVerified = false;
        try {
            //调用SDK验证签名
            signVerified = AlipaySignature.rsaCheckV1(paramMap, alipayConf.ALIPAY_PUBLIC_KEY, alipayConf.CHARSET, alipayConf.SIGNTYPE);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error(e.toString());
        }
        return signVerified;
    }

}
