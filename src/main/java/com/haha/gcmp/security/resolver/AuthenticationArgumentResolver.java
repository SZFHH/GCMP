package com.haha.gcmp.security.resolver;


import com.haha.gcmp.exception.AuthenticationException;
import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.security.authentication.Authentication;
import com.haha.gcmp.security.context.SecurityContextHolder;
import com.haha.gcmp.security.support.UserDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

/**
 * Authentication argument resolver.
 *
 * @author johnniang
 * @date 12/11/18
 */

public class AuthenticationArgumentResolver implements HandlerMethodArgumentResolver {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationArgumentResolver.class);

    public AuthenticationArgumentResolver() {
        log.debug("Initializing AuthenticationArgumentResolver");
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        return Authentication.class.isAssignableFrom(parameterType)
            || UserDetail.class.isAssignableFrom(parameterType)
            || User.class.isAssignableFrom(parameterType);
    }

    @Override
    @Nullable
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {
        log.debug("Handle AuthenticationArgument");

        Class<?> parameterType = parameter.getParameterType();

        Authentication authentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .orElseThrow(() -> new AuthenticationException("You haven't signed in yet"));

        if (Authentication.class.isAssignableFrom(parameterType)) {
            return authentication;
        } else if (UserDetail.class.isAssignableFrom(parameterType)) {
            return authentication.getDetail();
        } else if (User.class.isAssignableFrom(parameterType)) {
            return authentication.getDetail().getUser();
        }

        // Should never happen...
        throw new UnsupportedOperationException("Unknown parameter type: " + parameterType);
    }

}
