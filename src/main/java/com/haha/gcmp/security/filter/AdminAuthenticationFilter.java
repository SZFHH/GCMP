package com.haha.gcmp.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haha.gcmp.cache.AbstractStringCacheStore;
import com.haha.gcmp.config.propertites.GcmpProperties;
import com.haha.gcmp.exception.AuthenticationException;
import com.haha.gcmp.exception.NotInitializationException;
import com.haha.gcmp.model.entity.User;
import com.haha.gcmp.security.authentication.AuthenticationImpl;
import com.haha.gcmp.security.context.SecurityContextHolder;
import com.haha.gcmp.security.context.SecurityContextImpl;
import com.haha.gcmp.security.handler.DefaultAuthenticationFailureHandler;
import com.haha.gcmp.security.support.AuthenticationToken;
import com.haha.gcmp.security.support.UserDetail;
import com.haha.gcmp.security.util.SecurityUtils;
import com.haha.gcmp.service.AdminService;
import com.haha.gcmp.service.AuthService;
import com.haha.gcmp.service.PropertyService;
import com.haha.gcmp.service.UserService;
import com.haha.gcmp.utils.CookieUtil;
import com.haha.gcmp.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static com.haha.gcmp.config.GcmpConst.*;

/**
 * @author SZFHH
 * @date 2020/10/24
 */
@Component
@Order(0)
public class AdminAuthenticationFilter extends AbstractAuthenticationFilter {
    private final GcmpProperties gcmpProperties;

    private final UserService userService;

    private final AuthService authService;

    private final AdminService adminService;

    public AdminAuthenticationFilter(AbstractStringCacheStore cacheStore,
                                     UserService userService,
                                     GcmpProperties gcmpProperties,
                                     PropertyService propertyService,
                                     ObjectMapper objectMapper, AuthService authService, AdminService adminService) {
        super(gcmpProperties, propertyService, cacheStore);
        this.userService = userService;
        this.gcmpProperties = gcmpProperties;
        this.authService = authService;
        this.adminService = adminService;

        addUrlPatterns(
            "/api/admin/**",
            "/api/docker/list/all",
            "/api/docker/remove/common",
            "/api/docker/add/common"
        );

        addExcludeUrlPatterns(
            "/api/admin/login",
            "/api/admin/is_initialized",
            "/api/admin/init"
        );

        // set failure handler
        DefaultAuthenticationFailureHandler failureHandler = new DefaultAuthenticationFailureHandler();
        failureHandler.setProductionEnv(gcmpProperties.isProductionEnv());
        failureHandler.setObjectMapper(objectMapper);

        setFailureHandler(failureHandler);

    }

    @Override
    protected void doAuthenticate(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (!adminService.isInitialized()) {
            throw new NotInitializationException("管理员还没初始化。");
        }

        User user = null;
        // Get token from request
        String token = getTokenFromRequest(request);

        if (!StringUtils.isBlank(token)) {
            Optional<Integer> optionalUserId = cacheStore.get(SecurityUtils.buildTokenAccessKey(token), Integer.class);
            if (optionalUserId.isPresent()) {
                user = userService.getByIdOfNonNull(optionalUserId.get());
                if (!user.getUserName().equals(gcmpProperties.getAdminName())) {
                    user = null;
                }
            }
        }
        if (user == null) {
            Cookie[] cookies = request.getCookies();
            Cookie cookie = CookieUtil.findCookie(cookies, REMEMBER_ME_COOKIE_NAME);
            if (cookie != null) {
                String value = cookie.getValue();
                AuthenticationToken authenticationToken = JsonUtils.jsonToObject(value, AuthenticationToken.class);
                if (authenticationToken.getUserName().equals(gcmpProperties.getAdminName())) {
                    user = authService.authCheck(authenticationToken);
                }

            }
        }
        if (user == null) {
            throw new AuthenticationException("未登录，请登录后访问");
        }

        // Build user detail
        UserDetail userDetail = new UserDetail(user);

        // Set security
        SecurityContextHolder.setContext(new SecurityContextImpl(new AuthenticationImpl(userDetail)));

        // Do filter
        filterChain.doFilter(request, response);
    }

    @Override
    protected String getTokenFromRequest(@NonNull HttpServletRequest request) {
        return getTokenFromRequest(request, TOKEN_QUERY_NAME, TOKEN_HEADER_NAME);
    }

}
