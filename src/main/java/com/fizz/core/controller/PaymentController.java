package com.fizz.core.controller;

import com.fizz.common.controller.BasicController;
import com.fizz.common.utils.IdUtil;
import com.fizz.common.utils.IpUtil;
import com.fizz.core.weixin.WxpayCollection;
import com.fizz.core.weixin.WxpayOrder;
import com.fizz.core.weixin.WxpayUtil;
import com.fizz.core.zhifubao.AlipayConf;
import com.fizz.core.zhifubao.AlipayOrder;
import com.fizz.core.zhifubao.AlipayTradeCollection;
import com.fizz.core.zhifubao.AlipayUtil;
import com.github.wxpay.sdk.WXPayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PaymentController extends BasicController {

    private static Logger logger = LoggerFactory.getLogger(PaymentController.class);
    @Autowired
    private static AlipayConf alipayConf;

    //支付宝
    /**
     * 创建订单Form(支付宝)
     * @param request
     * @param response
     * @param subject   订单主题
     * @param amount    订单金额
     */
    @ResponseBody
    @RequestMapping("/create_order_form")
    public void orderForm(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam(required = false) String subject,
                          @RequestParam(required = false, defaultValue = "0.01") Double amount) {
        /*JsonObject data= ParamUtils.getRequireJsonData(request);
        String orderNo = ParamUtils.getRequiredString(data, "orderNo");
        String subject = ParamUtils.getRequiredString(data, "subject");
        Double amount = ParamUtils.getRequiredDouble(data, "amount");*/
        String orderNo = "alipay" + IdUtil.getId();
        String form = AlipayUtil.createOrderFormPC(orderNo, subject, amount);
        renderString(response, form, "text/html;charset=utf-8");
    }

    /**
     * 异步通知处理(支付宝)
     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping("/notify_async_ali")
    public void asyncNotifyAli(HttpServletRequest request, HttpServletResponse response) {
        String res = "failure";
        Map<String, String> paramMap = new HashMap<>();
        Map<String, String[]> params = request.getParameterMap();
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            paramMap.put(key, values[0]);
        }
        boolean flag_sign = AlipayUtil.checkSignOther(paramMap);
        if (flag_sign) {
            res = "success";
            // TODO 处理接收到的数据
            String out_trade_no = paramMap.get("out_trade_no");
            AlipayOrder alipayOrder = AlipayTradeCollection.getAlipayMap(out_trade_no);
            if (alipayOrder != null) {
                String app_id = paramMap.get("app_id");
                Double total_amount = Double.valueOf(paramMap.get("total_amount"));
                if (app_id.equals(alipayConf.APPID) && total_amount==alipayOrder.getTotal_amount()) {
                    String trade_status = paramMap.get("trade_status");
                    if ("TRADE_SUCCESS".equals(trade_status)) {
                        logger.info("该订单交易支付已完成，订单号：" + out_trade_no);
                        AlipayTradeCollection.removeQueue(alipayOrder);
                        AlipayTradeCollection.removeAlipayMap(out_trade_no);
                    } else if ("TRADE_CLOSED".equals(trade_status)) {
                        logger.info("该订单交易已关闭，订单号：" + out_trade_no);
                    }
                }
            }
        }
        renderString(response, res, "text/html;charset=utf-8");
    }


    //微信

    /**
     * 创建订单(微信)
     * @param request
     * @param response
     * @param body  订单内容
     * @param amount    订单金额
     */
    @ResponseBody
    @RequestMapping("/create_order_wx")
    public void createOrder(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(required = false) String body,
                            @RequestParam(required = false, defaultValue = "1") Integer amount) {
        String user_ip = IpUtil.getIpAddress(request);
        String orderNo = "wxpay" + IdUtil.getId();
        Map<String, String> resMap = WxpayUtil.createOrder(orderNo, body, amount, user_ip);
        renderString(response, resMap);
    }

    /**
     * 异步支付结果通知(微信)
     * @param request
     * @param response
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/notify_pay_wx")
    public void asyncNotifyPay(HttpServletRequest request, HttpServletResponse response) throws Exception {
        InputStream inputStream = request.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        outputStream.close();
        inputStream.close();
        String xmlData = new String(outputStream.toByteArray(), "utf-8");
        Map<String, String> resMap = WxpayUtil.notifyPay(xmlData);
        if ("SUCCESS".equals(resMap.get("return_code"))) {
            // TODO 更新订单状态、时间
        }
        String returnXml = WXPayUtil.mapToXml(resMap);
        renderString(response, returnXml, "text/xml;charset=utf-8");
    }

    /**
     * 异步退款结果通知(微信)
     * @param request
     * @param response
     * @throws Exception
     */
    public void asyncNotifyRefund(HttpServletRequest request, HttpServletResponse response) throws Exception {
        InputStream inputStream = request.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        outputStream.close();
        inputStream.close();
        String notifyXml = new String(outputStream.toByteArray(), "utf-8");

    }

}
