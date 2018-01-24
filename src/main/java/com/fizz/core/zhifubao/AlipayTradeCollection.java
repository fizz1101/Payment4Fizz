package com.fizz.core.zhifubao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AlipayTradeCollection {

    private static Logger logger = LoggerFactory.getLogger(AlipayTradeCollection.class);

    private static ConcurrentLinkedQueue<AlipayOrder> queue_alipay = new ConcurrentLinkedQueue();
    private static ConcurrentHashMap<String, AlipayOrder> map_alipay = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Integer> map_oper_fail = new ConcurrentHashMap<>();

    public static final String oper_close = "OPER_CLOSE";

    public static ConcurrentLinkedQueue<AlipayOrder> getQueue_alipay() {
        return queue_alipay;
    }

    public static ConcurrentHashMap<String, AlipayOrder> getMap_alipay() {
        return map_alipay;
    }

    /**
     * 加订单实体入队列
     * @param alipayOrder
     * @return
     */
    public static boolean offerQueue(AlipayOrder alipayOrder) {
        try {
            return queue_alipay.offer(alipayOrder);
        } catch (Exception e) {
            logger.error(e.toString());
            return false;
        }
    }

    /**
     * 移除队列第一个元素，并返回实体
     * @return
     */
    public static AlipayOrder pollQueue() {
        return queue_alipay.poll();
    }

    /**
     * 获取队列第一个元素
     * @return
     */
    public static AlipayOrder peekQueue() {
        return queue_alipay.peek();
    }

    /**
     * 移除订单实体出队列
     * @param alipayOrder
     */
    public static void removeQueue(AlipayOrder alipayOrder) {
        if (alipayOrder != null) {
            queue_alipay.remove(alipayOrder);
        } else {
            queue_alipay.remove();
        }
    }

    /**
     * 加订单实体入Map数组
     * @param alipayOrder
     */
    public static void putAlipayMap(AlipayOrder alipayOrder) {
        try {
            map_alipay.put(alipayOrder.getOut_order_no(), alipayOrder);
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    /**
     * 从Map数组中取订单实体
     * @param out_order_no
     * @return
     */
    public static AlipayOrder getAlipayMap(String out_order_no) {
        return map_alipay.get(out_order_no);
    }

    /**
     * 移除订单实体出Map数组
     * @param out_order_no
     */
    public static void removeAlipayMap(String out_order_no) {
        map_alipay.remove(out_order_no);
    }

    /**
     * 设置失败操作
     * @param operName
     * @param count
     */
    public static void putErrorOperMap(String operName, int count) {
        map_oper_fail.put(operName, count);
    }

    /**
     * 获取失败操作次数
     * @param operName
     * @return
     */
    public static int getErrorOperCount(String operName) {
        return map_oper_fail.get(operName)==null ? 0 : map_oper_fail.get(operName);
    }

}
