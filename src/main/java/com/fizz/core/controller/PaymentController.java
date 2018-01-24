package com.fizz.core.controller;

import com.fizz.common.controller.BasicController;
import com.fizz.common.utils.ParamUtils;
import com.fizz.core.zhifubao.AlipayConf;
import com.fizz.core.zhifubao.AlipayOrder;
import com.fizz.core.zhifubao.AlipayTradeCollection;
import com.fizz.core.zhifubao.AlipayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/pay")
public class PaymentController extends BasicController {

    private static Logger logger = LoggerFactory.getLogger(PaymentController.class);
    @Autowired
    private static AlipayConf alipayConf;

    /**
     * 创建订单Form
     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping("/create_order_form")
    public void orderForm(HttpServletRequest request, HttpServletResponse response) {
        JsonObject data= ParamUtils.getRequireJsonData(request);
        String orderNo = ParamUtils.getRequiredString(data, "orderNo");
        String subject = ParamUtils.getRequiredString(data, "orderNo");
        Double amount = ParamUtils.getRequiredDouble(data, "amount");
        String form = AlipayUtil.createOrderFormPC(orderNo, subject, amount);
        renderString(response, form, "text/html;charset=utf-8");
    }

    /**
     * 异步通知处理(支付宝)
     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping("/notify_async")
    public void asyncNotify(HttpServletRequest request, HttpServletResponse response) {
        String res = "failure";
        try {
            String url = request.getRequestURI();
            String params = url.substring(url.indexOf("?") + 1, url.length());
            boolean flag_sign = AlipayUtil.checkSign(params);
            if (flag_sign) {
                res = "success";
                // TODO 处理接收到的数据
                JsonObject data = ParamUtils.getRequireJsonData(request);
                String out_trade_no = ParamUtils.getRequiredString(data, "out_trade_no");
                AlipayOrder alipayOrder = AlipayTradeCollection.getAlipayMap(out_trade_no);
                if (alipayOrder != null) {
                    String app_id = ParamUtils.getRequiredString(data,"app_id");
                    Double total_amount = ParamUtils.getRequiredDouble(data, "total_amount");
                    if (app_id.equals(alipayConf.APPID) && total_amount==alipayOrder.getTotal_amount()) {
                        String trade_status = ParamUtils.getRequiredString(data, "trade_status");
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
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
        }
        renderString(response, res, "text/plain;charset=utf-8");
    }

}
