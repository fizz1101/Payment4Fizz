package com.fizz.common.utils;


import com.fizz.common.entity.ErrorsMsg;
import com.fizz.common.exception.ServiceException;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 参数处理类
 *
 * Created by fuzhongyu on 2017/7/21.
 */
public class ParamUtils {

	
	public static Integer IntegerParam(Object object){
		Integer param = null;
		if(object instanceof String)
			param = Integer.valueOf((String)object);
		if(object instanceof Integer)
			param = (Integer)object;
		return param;
	}


	public static String StringParam(Object object){
		String param = null;
		if(object instanceof String)
			param = (String)object;
		if(object instanceof Integer)
			param = object.toString();
		if (object instanceof Long)
			param = object.toString();
		return param;
	}

	public static Long LongParam(Object object){
		Long param = null;
		if(object instanceof String)
			param = Long.valueOf((String)object);
		if(object instanceof Integer )
			param = Long.valueOf((Integer)object);
		if(object instanceof Long)
			param = (Long)object;
		return param;
	}
	
	public static List<?> ReturnArrayList(List<?> list){
		if(list == null)
			return new ArrayList<>();
		return list;
	}

	public static JsonObject getJsonData(HttpServletRequest request){
		JsonObject data= (JsonObject) request.getAttribute("dataJson");
		if (data == null)
			return null;
		return data;
	}
	public static JsonObject getRequireJsonData(HttpServletRequest request){
		JsonObject data= (JsonObject) request.getAttribute("dataJson");
		if (data == null ){
			throw new ServiceException(ErrorsMsg.ERR_1002.getCode(),getEmptyMessage("JSON参数不正确!!"));
		}
		return data;
	}

	private static String getEmptyMessage(String key){
		return "参数 "+key+" 不存在或为空！";
	}
	
	private static String getWrongTypeMessage(String key){
		return "参数 "+key+" 类型错误！";
	}
	
	private static void checkContainsKey(JsonObject data,String key){
		if(!data.containsKey(key)){
			throw new ServiceException(ErrorsMsg.ERR_1002.getCode(),getEmptyMessage(key));
		}
	}
	
	/**
	 * 获取可为空的String类型参数
	 * @param data
	 * @param key
	 * @return
	 */
	public static String getString(JsonObject data,String key){
		if(!data.containsKey(key)){
			return null;
		}
		checkStringType(data.get(key).getValueType().name(),key);
		return data.getString(key);
	}
	
	/**
	 * 获取非空的String类型参数
	 * @param data
	 * @param key
	 * @return
	 */
	public static String getRequiredString(JsonObject data,String key){
		checkContainsKey(data,key);
		checkStringType(data.get(key).getValueType().name(),key);
		String value = data.getString(key);
		if(value == null || "".equals(value)){
			throw new ServiceException(ErrorsMsg.ERR_1002.getCode(),getEmptyMessage(key));
		}
		return value;
	}
	
	/**
	 * 获取可为空的int类型参数
	 * @param data
	 * @param key
	 * @return
	 */
	public static Integer getInteger(JsonObject data,String key){
		if(!data.containsKey(key)){
			return null;
		}
		checkNumberType(data.get(key).getValueType().name(),key);
		return data.getInt(key);
	}
	
	/**
	 * 获取非空的int类型参数
	 * @param data
	 * @param key
	 * @return
	 */
	public static Integer getRequiredInteger(JsonObject data,String key){
		checkContainsKey(data,key);
		checkNumberType(data.get(key).getValueType().name(),key);
		Integer value = data.getInt(key);
		if(value == null){
			throw new ServiceException(ErrorsMsg.ERR_1002.getCode(),getEmptyMessage(key));
		}
		return value;
	}
	
	/**
	 * 获取可为空的long类型参数
	 * @param data
	 * @param key
	 * @return
	 */
	public static Long getLong(JsonObject data,String key){
		if(!data.containsKey(key)){
			return null;
		}
		checkNumberType(data.get(key).getValueType().name(),key);
		return data.getJsonNumber(key).longValue();
	}
	
	/**
	 * 获取非空的long类型参数
	 * @param data
	 * @param key
	 * @return
	 */
	public static Long getRequiredLong(JsonObject data,String key){
		checkContainsKey(data,key);
		checkNumberType(data.get(key).getValueType().name(),key);
		Long value = data.getJsonNumber(key).longValue();
		if(value == null){
			throw new ServiceException(ErrorsMsg.ERR_1002.getCode(),getEmptyMessage(key));
		}
		return value;
	}
	
	/**
	 * 获取可为空的Double类型参数
	 * @param data
	 * @param key
	 * @return
	 */
	public static Double getDouble(JsonObject data,String key){
		if(!data.containsKey(key)){
			return null;
		}
		checkNumberType(data.get(key).getValueType().name(),key);
		return data.getJsonNumber(key).doubleValue();
	}
	
	/**
	 * 获取非空的Double类型参数
	 * @param data
	 * @param key
	 * @return
	 */
	public static Double getRequiredDouble(JsonObject data,String key){
		checkContainsKey(data,key);
		checkNumberType(data.get(key).getValueType().name(),key);
		Double value = data.getJsonNumber(key).doubleValue();
		if(value == null){
			throw new ServiceException(ErrorsMsg.ERR_1002.getCode(),getEmptyMessage(key));
		}
		return value;
	}
	
	/**
	 * 获取可为空的Boolean类型参数
	 * @param data
	 * @param key
	 * @return
	 */
	public static Boolean getBoolean(JsonObject data,String key){
		if(!data.containsKey(key)){
			return null;
		}
		checkBooleanType(data.get(key).getValueType().name(),key);
		return data.getBoolean(key);
	}
	
	/**
	 * 获取非空的Boolean类型参数
	 * @param data
	 * @param key
	 * @return
	 */
	public static Boolean getRequiredBoolean(JsonObject data,String key){
		checkContainsKey(data,key);
		checkBooleanType(data.get(key).getValueType().name(),key);
		Boolean value = data.getBoolean(key);
		if(value == null){
			throw new ServiceException(ErrorsMsg.ERR_1002.getCode(),getEmptyMessage(key));
		}
		return value;
	}
	
	/**
	 * 获取可为空的Array类型参数
	 * @param data
	 * @param key
	 * @return
	 */
	public static JsonArray getArray(JsonObject data,String key){
		if(!data.containsKey(key)){
			return null;
		}
		checkArrayType(data.get(key).getValueType().name(),key);
		return data.getJsonArray(key);
	}
	
	/**
	 * 获取非空的Array类型参数
	 * @param data
	 * @param key
	 * @return
	 */
	public static JsonArray getRequiredArray(JsonObject data,String key){
		checkContainsKey(data,key);
		checkArrayType(data.get(key).getValueType().name(),key);
		JsonArray value = data.getJsonArray(key);
		if(value == null || value.size() == 0){
			throw new ServiceException(ErrorsMsg.ERR_1002.getCode(),getEmptyMessage(key));
		}
		return value;
	}

	public static JsonObject getJsonRequiredObject(JsonObject data,String key){
		checkContainsKey(data,key);
		JsonObject value = data.getJsonObject(key);
		if(value == null ){
			throw new ServiceException(ErrorsMsg.ERR_1002.getCode(),getEmptyMessage(key));
		}
		return value;
	}

	public static JsonObject getJsonObject(JsonObject data,String key){
		if(!data.containsKey(key)){
			return null;
		}
		return data.getJsonObject(key);
	}


	
	private static void checkStringType(String type,String key){
		if(!"STRING".equals(type)){
			throw new ServiceException(ErrorsMsg.ERR_1002.getCode(),getWrongTypeMessage(key));
		}
	}
	
	private static void checkNumberType(String type,String key){
		if(!"NUMBER".equals(type)){
			throw new ServiceException(ErrorsMsg.ERR_1002.getCode(),getWrongTypeMessage(key));
		}
	}
	
	private static void checkBooleanType(String type,String key){
		if(!"FALSE".equals(type) && !"TRUE".equals(type)){
			throw new ServiceException(ErrorsMsg.ERR_1002.getCode(),getWrongTypeMessage(key));
		}
	}
	
	private static void checkArrayType(String type,String key){
		if(!"ARRAY".equals(type)){
			throw new ServiceException(ErrorsMsg.ERR_1002.getCode(),getWrongTypeMessage(key));
		}
	}


	
}
