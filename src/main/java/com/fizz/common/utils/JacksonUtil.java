package com.fizz.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Jackson的简单封装.
 *
 * Created by fuzhongyu on 2017/7/21.
 */
public class JacksonUtil {

	private static Logger logger= Logger.getLogger(JacksonUtil.class);

	/**
	 * 创建ObjectMapper,内部使用
	 */
	private static JacksonUtil getInstance() {
		return new JacksonUtil();
	}


	/**
	 * 对象转json
	 * @param javaObj
	 * @return
	 */
	public static String toJsonString(Object javaObj){
		return  toJson(javaObj,"yyyy-MM-dd HH:mm:ss");
	}


	/**
	 * json字符串转对象
	 * @param jsonString
	 * @param clazz
	 * @return
	 */
	public static Object fromJsonString(String jsonString,Class<?> clazz){
		return getInstance().toObject(jsonString,clazz);
	}



	private static String toJson(Object javaObj,String dataFormat){
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setDateFormat(new SimpleDateFormat(dataFormat));
			return mapper.writeValueAsString(javaObj);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Exception",e);
			return null;
		}
	}
	

	/**
	 * 如果JSON字符串为Null或"null"字符串,返回Null.
	 * 如果JSON字符串为"[]",返回空集合.
	 *
	 * 如需读取集合如List/Map,且不是List<String>这种简单类型时使用如下语句:
	 * List<MyBean> beanList = binder.getMapper().readValue(listString, new TypeReference<List<MyBean>>() {});
	 */
	private  <T> T toObject(String jsonString, Class<T> clazz) {
		ObjectMapper mapper = new ObjectMapper();
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}
		try {
			return mapper.readValue(jsonString, clazz);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	

}
