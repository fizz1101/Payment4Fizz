package com.fizz.core.controller;

import com.alipay.api.internal.util.AlipayUtils;
import com.fizz.common.controller.BasicController;
import com.fizz.common.entity.ErrorsMsg;
import com.fizz.common.entity.ResponseEntity;
import com.fizz.common.utils.ParamUtils;
import com.fizz.core.zhifubao.AlipayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/pay")
public class PaymentController extends BasicController {

    private static Logger logger = LoggerFactory.getLogger(PaymentController.class);

    /**
     * 订单
     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping("/order")
    public void order(HttpServletRequest request, HttpServletResponse response) {
        JsonObject data= ParamUtils.getRequireJsonData(request);
        String orderNo = ParamUtils.getRequiredString(data, "orderNo");
        String subject = ParamUtils.getRequiredString(data, "orderNo");
        Double amount = ParamUtils.getRequiredDouble(data, "amount");
        String form = AlipayUtil.createorder(orderNo, subject, amount);
        renderString(response, form, "text/html;charset=utf-8");
    }

}
