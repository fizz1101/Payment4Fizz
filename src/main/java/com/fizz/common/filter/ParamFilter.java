package com.fizz.common.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.*;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * 过滤参数
 *
 * Created by fuzhongyu on 2017/7/21.
 */
@WebFilter(filterName = "paramFilter", urlPatterns = "/payment/*", initParams = {
                @WebInitParam(name = "exclude_file", value = ".js,.gif,.jpg,.bmp,.png,.css,.ico"),  //忽略获取静态资源
                @WebInitParam(name = "exclude_url", value = "/payment/pay/notify_async")  //忽略特殊url
        }
)
public class ParamFilter implements Filter {

    private Logger logger= LoggerFactory.getLogger(getClass());
    
    private String exclude_file;
    private String exclude_url;
    private List<String> excludeUrlList;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        exclude_file = filterConfig.getInitParameter("exclude_file");
        exclude_url = filterConfig.getInitParameter("exclude_url");
        excludeUrlList = Arrays.asList(exclude_url.split(","));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        StringBuffer url = httpServletRequest.getRequestURL();
        boolean isExcludedUrl = false;
        String uri = httpServletRequest.getRequestURI();
        if (excludeUrlList.contains(uri)) {
            isExcludedUrl = true;
        }
        if (!isExcludedUrl) {
            String requestType = request.getContentType();
            if (requestType != null) {
                if (requestType.toLowerCase().startsWith("application/json")) {
                    ServletInputStream inputStream = httpServletRequest.getInputStream();
                    int contentLength = request.getContentLength();
                    if (contentLength != 0) {
                        JsonReader reader = Json.createReader(inputStream);
                        JsonStructure read = reader.read();
                        if (read.getValueType().equals(JsonValue.ValueType.ARRAY)) {
                            httpServletRequest.setAttribute("dataJson", (JsonArray) read);
                        } else if (read.getValueType().equals(JsonValue.ValueType.OBJECT)) {
                            httpServletRequest.setAttribute("dataJson", (JsonObject) read);
                        }
                    } else {
                        chain.doFilter(request, response);
                        return;
                    }
                } else if (requestType.toLowerCase().startsWith("application/x-www-form-urlencoded")) {

                }
                if (logger.isDebugEnabled()) {
                    logger.debug("解析requestType成功！URL={}", url);
                }
            } else {
                if (logger.isWarnEnabled()) {
                    if (!httpServletRequest.getMethod().equals("HEAD")) {
                        int index_ext = url.lastIndexOf(".");
                        if (index_ext >= 0) {
                            String ext = url.substring(index_ext);
                            if (!exclude_file.contains(ext)) {
                                logger.warn("请求没有带requestType!,URL={}", url);
                            }
                        } else {
                            logger.warn("请求没有带requestType!,URL={}", url);
                        }
                    }
                }
            }
        }
        chain.doFilter(request,response);
    }

    @Override
    public void destroy() {
        this.exclude_file = null;
        this.exclude_url = null;
        this.excludeUrlList = null;
    }
}
