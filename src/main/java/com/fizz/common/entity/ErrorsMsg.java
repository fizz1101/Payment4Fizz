package com.fizz.common.entity;

/**
 * 异常实体类
 *
 * Created by fuzhongyu on 2017/7/21.
 */
public enum ErrorsMsg {

    /**
     * 系统错误码
     */
    SUCC_1("1","操作成功"),
    ERR_9999("9999","操作异常"),

    ERR_1001("1001","请求参数错误"),
    ERR_1002("1002","参数类型错误");

    private String code; //相应状态码
    private String msg;  //响应吗说明

    ErrorsMsg(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
