package com.haha.gcmp.security.handler;

import cn.hutool.extra.servlet.ServletUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haha.gcmp.exception.AbstractGcmpException;
import com.haha.gcmp.model.support.BaseResponse;
import com.haha.gcmp.utils.ExceptionUtils;
import com.haha.gcmp.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author SZFHH
 * @date 2020/10/18
 */

public class DefaultAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private static final Logger log = LoggerFactory.getLogger(DefaultAuthenticationFailureHandler.class);
    private boolean productionEnv = true;

    private ObjectMapper objectMapper = JsonUtils.DEFAULT_JSON_MAPPER;

    public DefaultAuthenticationFailureHandler() {
    }

    @Override
    public void onFailure(HttpServletRequest request, HttpServletResponse response, AbstractGcmpException exception) throws IOException, ServletException {
        log.warn("Handle unsuccessful authentication, ip: [{}]", ServletUtil.getClientIP(request));
        log.error("Authentication failure", exception);

        BaseResponse<Object> errorDetail = new BaseResponse<>();

        errorDetail.setStatus(exception.getStatus().value());
        errorDetail.setMessage(exception.getMessage());
        errorDetail.setData(exception.getErrorData());

        if (!productionEnv) {
            errorDetail.setDevMessage(ExceptionUtils.getStackTrace(exception));
        }

        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setStatus(exception.getStatus().value());
        response.getWriter().write(objectMapper.writeValueAsString(errorDetail));
    }

    /**
     * Sets custom object mapper.
     *
     * @param objectMapper object mapper
     */
    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "Object mapper must not be null");

        this.objectMapper = objectMapper;
    }

    /**
     * Sets production environment.
     *
     * @param productionEnv production environment
     */
    public void setProductionEnv(boolean productionEnv) {
        this.productionEnv = productionEnv;
    }
}
