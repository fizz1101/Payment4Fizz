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
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝接口工具类
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
     * 创建订单form
     * @param out_trade_no  商户订单号
     * @param subject   订单标题
     * @param total_amount  订单金额
     * @return 下单请求所需的完整的form表单html
     */
    public static String createorder(String out_trade_no, String subject, Double total_amount) {
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
            logger.error(e.toString());
        }
        return form;
    }

    /**
     * 接口：alipay.trade.query
     * 查询订单
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
            AlipayTradeQueryResponse alipayResponse = alipayClient.execute(alipayRequest); //通过alipayClient调用API，获得对应的response类
            logger.info("查询订单接口返回Body：" + alipayResponse.getBody());
            res = returnDefaultParam(alipayResponse);
            if (alipayResponse.isSuccess()) {
                res.put("trade_no", alipayResponse.getTradeNo());
                res.put("out_trade_no", alipayResponse.getOutTradeNo());
                res.put("trade_status", alipayResponse.getTradeStatus());   //交易状态
                res.put("total_amount", alipayResponse.getTotalAmount());   //交易金额
                res.put("buyer_user_id", alipayResponse.getBuyerUserId());  //买家支付宝用户id
                res.put("buyer_logon_id", alipayResponse.getBuyerLogonId());    //买家支付宝账号
                res.put("fund_bill_list", alipayResponse.getFundBillList());    //交易支付使用的资金渠道
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("查询订单接口报错：" + e.toString());
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
            AlipayTradeRefundResponse alipayResponse = alipayClient.execute(alipayRequest); //通过alipayClient调用API，获得对应的response类
            logger.info("退款接口返回Body：" + alipayResponse.getBody());
            res = returnDefaultParam(alipayResponse);
            if (alipayResponse.isSuccess()) {
                res.put("trade_no", alipayResponse.getTradeNo());
                res.put("out_trade_no", alipayResponse.getOutTradeNo());
                res.put("fund_change", alipayResponse.getFundChange()); //本次退款是否发生资金变化
                res.put("refund_fee", alipayResponse.getRefundFee());   //退款总金额
                res.put("gmt_refund_pay", alipayResponse.getGmtRefundPay());    //退款支付时间
                res.put("buyer_user_id", alipayResponse.getBuyerUserId());  //买家支付宝用户id
                res.put("buyer_logon_id", alipayResponse.getBuyerLogonId());    //买家支付宝账号
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("退款接口报错：" + e.toString());
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
            AlipayTradeFastpayRefundQueryResponse alipayResponse = alipayClient.execute(alipayRequest); //通过alipayClient调用API，获得对应的response类
            logger.info("退款查询接口返回Body：" + alipayResponse.getBody());
            res = returnDefaultParam(alipayResponse);
            if (alipayResponse.isSuccess()) {
                res.put("trade_no", alipayResponse.getTradeNo());
                res.put("out_trade_no", alipayResponse.getOutTradeNo());
                res.put("out_request_no", alipayResponse.getOutRequestNo());
                res.put("total_amount", alipayResponse.getTotalAmount());   //该笔退款所属订单总金额
                res.put("refund_amount", alipayResponse.getRefundAmount()); //退款金额
                res.put("refund_reason", alipayResponse.getRefundReason()); //退款原因
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("退款查询接口报错：" + e.toString());
        }
        return res;
    }

    /**
     * 接口：alipay.trade.close
     * 关闭交易
     * @param out_trade_no  商户订单很号
     * @param trade_no  支付宝28位交易号(与trade_no二者至少传一个,优先级高)
     * @param operator_id   卖家端自定义的的操作员 ID
     * @return
     */
    public static void closeTrade(String out_trade_no, String trade_no, String operator_id) {
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
                res.put("trade_no", alipayResponse.getTradeNo());
                res.put("out_trade_no", alipayResponse.getOutTradeNo());
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("关闭交易接口报错：" + e.toString());
        }
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
            AlipayDataDataserviceBillDownloadurlQueryResponse alipayResponse = alipayClient.execute(alipayRequest); //通过alipayClient调用API，获得对应的response类
            logger.info("获取账单地址接口返回Body：" + alipayResponse.getBody());
            if (alipayResponse.isSuccess()) {
                res = alipayResponse.getBillDownloadUrl();  //账单地址
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error("获取账单地址接口报错：" + e.toString());
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
     * 创建通用请求入参
     * @param method    请求接口名
     * @return
     */
    public static JSONObject createDefaultParam(String method) {
        JSONObject paramJson = new JSONObject();
        paramJson.put("method", method);
        paramJson.put("timestamp", DateUtils.getFormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        paramJson.put("version", VERSION);
        return paramJson;
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
        boolean flag = false;
        Map<String, String> paramsMap = new HashMap<>(); //将异步通知中收到的所有参数都存放到map中(trade_no, trade_status, total_amount)
        paramsMap.put("trade_no", "");
        paramsMap.put("trade_status", "");
        paramsMap.put("total_amount", "");
        paramsMap.put("sign", "");
        boolean signVerified = false;
        try {
            //调用SDK验证签名
            signVerified = AlipaySignature.rsaCheckV2(paramsMap, alipayConf.ALIPAY_PUBLIC_KEY, alipayConf.CHARSET, alipayConf.SIGNTYPE);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            logger.error(e.toString());
        }
        if(signVerified){
            // TODO 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
        }else{
            // TODO 验签失败则记录异常日志，并在response中返回failure.
        }
        return flag;
    }

}
