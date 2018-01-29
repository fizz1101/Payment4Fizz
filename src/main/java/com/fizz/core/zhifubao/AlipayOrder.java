package com.fizz.core.zhifubao;

/**
 * 支付宝订单实体
 */
public class AlipayOrder {

    private String out_order_no;
    private Double total_amount;
    private String subject;
    private String trade_status;
    private long create_time;

    public AlipayOrder() {
    }

    public AlipayOrder(String out_order_no, Double total_amount, String subject) {
        this.out_order_no = out_order_no;
        this.total_amount = total_amount;
        this.subject = subject;
        this.create_time = System.currentTimeMillis();
    }

    public String getOut_order_no() {
        return out_order_no;
    }

    public void setOut_order_no(String out_order_no) {
        this.out_order_no = out_order_no;
    }

    public Double getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(Double total_amount) {
        this.total_amount = total_amount;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTrade_status() {
        return trade_status;
    }

    public void setTrade_status(String trade_status) {
        this.trade_status = trade_status;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

}
