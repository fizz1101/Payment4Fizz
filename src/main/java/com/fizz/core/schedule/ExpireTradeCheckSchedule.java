package com.fizz.core.schedule;

import com.alibaba.fastjson.JSONObject;
import com.fizz.common.utils.SpringUtils;
import com.fizz.core.zhifubao.AlipayConf;
import com.fizz.core.zhifubao.AlipayOrder;
import com.fizz.core.zhifubao.AlipayTradeCollection;
import com.fizz.core.zhifubao.AlipayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 订单过期校验，并处理
 */
public class ExpireTradeCheckSchedule {

    private static Logger logger = LoggerFactory.getLogger(ExpireTradeCheckSchedule.class);

    private static AlipayConf alipayConf;
    static {
        alipayConf = (AlipayConf) SpringUtils.getBean("alipayConf");
    }

    public void start() {
        logger.info(new Date() + "启动订单超时校验");
        try {
            while (true) {
                AlipayOrder alipayOrder = AlipayTradeCollection.peekQueue();
                if (alipayOrder != null) {
                    long order_time = alipayOrder.getCreate_time();
                    long interval = (System.currentTimeMillis() - order_time) / 1000;
                    long time_sleep = alipayConf.PAY_TIMEOUT - interval;
                    if (time_sleep <= 0) {
                        //TODO 订单超时，手动调用交易关闭接口
                        String out_trade_no = alipayOrder.getOut_order_no();
                        JSONObject res = AlipayUtil.closeTrade(out_trade_no, null, "1");
                        if (!res.isEmpty() && out_trade_no.equals(res.get("out_trade_no"))) {
                            AlipayTradeCollection.pollQueue();
                            AlipayTradeCollection.removeAlipayMap(out_trade_no);
                        } else {
                            int count_close = AlipayTradeCollection.getErrorOperCount(AlipayTradeCollection.oper_close);
                            if (count_close < 3) {
                                AlipayTradeCollection.putErrorOperMap(AlipayTradeCollection.oper_close, ++count_close);
                            } else {
                                AlipayTradeCollection.pollQueue();
                                AlipayTradeCollection.removeAlipayMap(out_trade_no);
                                // TODO 记录失败操作
                            }
                        }
                    } else {
                        try {
                            logger.info("订单超时校验任务进入睡眠，时间：" + time_sleep + "ms");
                            Thread.sleep(time_sleep);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            logger.error("睡眠被打断：" + e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("订单超时校验报错：" + e);
        }
    }

}
