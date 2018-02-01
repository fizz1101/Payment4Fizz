package com.fizz.core.weixin;

/**
 * 微信订单实体
 */
public class WxpayOrder {

    private String out_trade_no;
    private Integer total_fee;
    private String body;
    private String trade_status;
    private long create_time;

    public WxpayOrder() {
    }

    public WxpayOrder(String out_trade_no, Integer total_fee, String body) {
        this.out_trade_no = out_trade_no;
        this.total_fee = total_fee;
        this.body = body;
        this.create_time = System.currentTimeMillis();
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public Integer getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(Integer total_fee) {
        this.total_fee = total_fee;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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
