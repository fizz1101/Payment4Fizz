package com.fizz.core.zhifubao;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.fizz.common.utils.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

    /**
     * 创建订单form
     * @param out_trade_no
     * @param subject
     * @param total_amount
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
     * 查询订单
     * @param out_trade_no
     * @param trade_no
     */
    public static void queryOrder(String out_trade_no, String trade_no) {
        AlipayClient alipayClient = AlipayClientFactory.getInstance();
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", out_trade_no);
        bizContent.put("trade_no", trade_no);
        alipayRequest.setBizContent(bizContent.toString());
        try {
            AlipayTradeWapPayResponse alipayResponse = alipayClient.execute(alipayRequest); //通过alipayClient调用API，获得对应的response类
            System.out.println(alipayResponse.getBody());
            // TODO 根据response中的结果继续业务逻辑处理(trade_no, out_trade_no, trade_status)
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * 退款
     * @param out_trade_no
     * @param trade_no
     * @param out_request_no
     * @param refund_amount
     */
    public static void refund(String out_trade_no, String trade_no, String out_request_no, String refund_amount) {
        AlipayClient alipayClient = AlipayClientFactory.getInstance();
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", out_trade_no);
        bizContent.put("trade_no", trade_no);
        bizContent.put("out_request_no", out_request_no);
        bizContent.put("refund_amount", refund_amount);
        alipayRequest.setBizContent(bizContent.toString());
        try {
            AlipayTradeWapPayResponse alipayResponse = alipayClient.execute(alipayRequest); //通过alipayClient调用API，获得对应的response类
            System.out.println(alipayResponse.getBody());
            //TODO 根据response中的结果继续业务逻辑处理(refund_fee)
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取账单地址
     * @param bill_type
     * @param bill_data
     */
    public static void billAddress(String bill_type, String bill_data) {
        AlipayClient alipayClient = AlipayClientFactory.getInstance();
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("bill_typw", bill_type);
        bizContent.put("bill_data", bill_data);
        alipayRequest.setBizContent(bizContent.toString());
        try {
            AlipayTradeWapPayResponse alipayResponse = alipayClient.execute(alipayRequest);
            System.out.println(alipayResponse.getBody());
            //TODO 根据response中的结果继续业务逻辑处理(bill_download_url)
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载账单
     * @param bill_url
     * @param file_path
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
            httpUrlConnection.setRequestProperty("Charsert", "UTF-8");
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(fis!=null) fis.close();
                if(fos!=null) fos.close();
                if(httpUrlConnection!=null) httpUrlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 异步通知验签
     * @param params
     */
    public static void checkSign(String params) {
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
        }
        if(signVerified){
            // TODO 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
        }else{
            // TODO 验签失败则记录异常日志，并在response中返回failure.
        }
    }

}
