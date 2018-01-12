package com.fizz.common.exception;


import com.fizz.common.entity.ErrorsMsg;

/**
 * Service层公用的Exception, 抛出时会触发事务回滚.
 *
 * Created by fuzhongyu on 2017/7/21.
 *
 */
public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String errCode;
	private String errMsg;


	public ServiceException(ErrorsMsg errorsMsg) {
		super(errorsMsg.getMsg());
		this.errCode = errorsMsg.getCode();
		this.errMsg = errorsMsg.getMsg();
	}
	
	public ServiceException(Throwable cause) {
		super(cause);
	}

	public ServiceException(String errCode, String message) {
		super(message);
		this.errCode = errCode;
		this.errMsg = message;
	}

	public ServiceException(String errCode, String message, Throwable cause) {
		super(message, cause);
		this.errCode = errCode;
		this.errMsg = message;
	}
	public ServiceException(String errCode, Throwable cause) {
		super("", cause);
		this.errCode = errCode;
	}
	
	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

}
