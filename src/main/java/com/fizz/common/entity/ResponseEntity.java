package com.fizz.common.entity;

import java.util.HashMap;

/**
 *  响应实体
 * Created by fuzhongyu on 2017/7/21.
 */
public class ResponseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 无返回值
     * @param errorsMsg
     */
    public ResponseEntity(ErrorsMsg errorsMsg){
        this.code = errorsMsg.getCode();
        this.msg = errorsMsg.getMsg();
        this.result = new HashMap<String,String>();
    }

    /**
     * 有返回值
     * @param errorsMsg
     * @param result
     */
    public ResponseEntity(ErrorsMsg errorsMsg, Object result){
        this.code = errorsMsg.getCode();
        this.msg = errorsMsg.getMsg();
        if(result == null)
            this.result = new HashMap<String,String>();
        else
            this.result = result;
    }

    /**
     * 自定义 返回码 和返回信息
     * @param errCode
     * @param errMsg
     * @param result
     */
    public ResponseEntity(String errCode, String errMsg, Object result){
        this.code = errCode;
        this.msg = errMsg;
        if(result == null)
            this.result = new HashMap<String,String>();
        else
            this.result = result;
    }

    /**
     * 响应码
     */
    private String code;

    /**
     * 响应码说明
     */
    private String msg;

    /**
     * 返回结果, 需能正确转化成json串
     */
    private Object result;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }


}
