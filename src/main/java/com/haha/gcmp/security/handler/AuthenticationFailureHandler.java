package com.haha.gcmp.security.handler;

import com.haha.gcmp.exception.AbstractGcmpException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author SZFHH
 * @date 2020/10/18
 */
public interface AuthenticationFailureHandler {

    /**
     * Calls when a user has been unsuccessfully authenticated.
     *
     * @param request   http servlet request
     * @param response  http servlet response
     * @param exception api exception
     * @throws IOException      io exception
     * @throws ServletException service exception
     */
    void onFailure(HttpServletRequest request, HttpServletResponse response, AbstractGcmpException exception) throws IOException, ServletException;
}
