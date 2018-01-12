package com.fizz.common.controller;

import com.fizz.common.entity.ErrorsMsg;
import com.fizz.common.entity.ResponseEntity;
import com.fizz.common.exception.ServiceException;
import com.fizz.common.utils.JacksonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 控制器基类
 *
 * Created by fuzhongyu on 2017/7/21.
 */
public abstract class BasicController {

	/**
	 * 日志对象
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());



	/**
	 * 客户端返回JSON字符串
	 * @param response
	 * @param object
	 * @return
	 */
	protected String renderString(HttpServletResponse response, Object object) {
		return renderString(response, JacksonUtil.toJsonString(object), "application/json");
	}



	/**
	 * 客户端返回字符串
	 * @param response
	 * @param string
	 * @return
	 */
	protected String renderString(HttpServletResponse response, String string) {
		try {
			response.reset();
			response.setContentType("application/json");
			response.setCharacterEncoding("utf-8");
			response.getWriter().print(string);
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 客户端返回字符串
	 * @param response
	 * @param string
	 * @return
	 */
	protected String renderString(HttpServletResponse response, String string, String type) {
		try {
			if(logger.isDebugEnabled()){
				logger.debug(string);
			}
			response.reset();
			response.setContentType(type);
			response.setHeader("Access-Control-Allow-Origin","*");
			response.setCharacterEncoding("utf-8");
			response.getWriter().print(string);

			return null;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 参数绑定异常
	 * 注：当这个Controller中任何一个方法发生异常，会被这个方法拦截到
	 */
	@ResponseBody
	@ExceptionHandler()
    public ResponseEntity bindException(Exception ex) {
		ResponseEntity responseEntity = null;
		if(ex instanceof ServiceException){
			ServiceException serviceException = (ServiceException) ex;
			if (StringUtils.isNotBlank(serviceException.getErrCode())){
				responseEntity = new ResponseEntity(serviceException.getErrCode(),serviceException.getErrMsg(),null);
			}else{
				logger.error(ex.getMessage() ,ex);
				responseEntity = new ResponseEntity(ErrorsMsg.ERR_9999);
			}
		}else{
			logger.error(ex.getMessage() ,ex);
			responseEntity = new ResponseEntity(ErrorsMsg.ERR_9999);
		}
		return responseEntity;
    }


	
}
