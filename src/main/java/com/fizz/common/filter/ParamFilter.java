package com.fizz.common.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.*;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;


/**
 * 过滤参数
 *
 * Created by fuzhongyu on 2017/7/21.
 */
@WebFilter(filterName = "paramFilter", urlPatterns = "/payment/*", initParams = {
                @WebInitParam(name = "exclusions", value = "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico")//忽略获取静态资源
        }
)
public class ParamFilter implements Filter {

    private Logger logger= LoggerFactory.getLogger(getClass());
    
    private String exclusions = ".html,.js,.gif,.jpg,.bmp,.png,.css,.ico";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        StringBuffer url = ((HttpServletRequest) request).getRequestURL();
        String requestType=request.getContentType();
        if (requestType != null){
            if(requestType.toLowerCase().startsWith("application/json")){
                ServletInputStream inputStream = httpServletRequest.getInputStream();
                int contentLength = request.getContentLength();
                if (contentLength != 0){
                    JsonReader reader = Json.createReader(inputStream);
                    JsonStructure read = reader.read();
                    if (read.getValueType().equals(JsonValue.ValueType.ARRAY)){
                        httpServletRequest.setAttribute("dataJson", (JsonArray)read);
                    }else if(read.getValueType().equals(JsonValue.ValueType.OBJECT)){
                        httpServletRequest.setAttribute("dataJson", (JsonObject)read);
                    }
                }else {
                    chain.doFilter(request,response);
                    return ;
                }
            }else if(requestType.toLowerCase().startsWith("application/x-www-form-urlencoded")){

            }
            if(logger.isDebugEnabled()){
                logger.debug("解析requestType成功！URL={}", url);
            }
        }else {
            if(logger.isWarnEnabled()){
                if(!((HttpServletRequest) request).getMethod().equals("HEAD")) {
                	int index_ext = url.lastIndexOf(".");
                	if (index_ext >= 0) {
                		String ext = url.substring(index_ext);
                		if (!exclusions.contains(ext)) {
                			logger.warn("请求没有带requestType!,URL={}", url);
                		}
                	} else {
                		logger.warn("请求没有带requestType!,URL={}", url);
                	}
                }
            }
        }
        chain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
