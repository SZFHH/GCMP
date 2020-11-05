package com.haha.gcmp.security.filter;

import com.haha.gcmp.cache.AbstractStringCacheStore;
import com.haha.gcmp.config.propertites.GcmpProperties;
import com.haha.gcmp.exception.AbstractGcmpException;
import com.haha.gcmp.security.context.SecurityContextHolder;
import com.haha.gcmp.security.handler.AuthenticationFailureHandler;
import com.haha.gcmp.security.handler.DefaultAuthenticationFailureHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author SZFHH
 * @date 2020/10/18
 */
public abstract class AbstractAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(AbstractAuthenticationFilter.class);
    protected final AntPathMatcher antPathMatcher;
    protected final GcmpProperties gcmpProperties;
    protected final AbstractStringCacheStore cacheStore;
    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    private volatile AuthenticationFailureHandler failureHandler;
    /**
     * Exclude url patterns.
     */
    private Set<String> excludeUrlPatterns = new HashSet<>(16);

    private Set<String> urlPatterns = new LinkedHashSet<>();


    AbstractAuthenticationFilter(GcmpProperties gcmpProperties,
                                 AbstractStringCacheStore cacheStore) {
        this.gcmpProperties = gcmpProperties;
        this.cacheStore = cacheStore;
        antPathMatcher = new AntPathMatcher();
    }

    /**
     * Gets token from request.
     *
     * @param request http servlet request must not be null
     * @return token or null
     */
    @Nullable
    protected abstract String getTokenFromRequest(@NonNull HttpServletRequest request);

    protected abstract void doAuthenticate(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        Assert.notNull(request, "Http servlet request must not be null");

        // check white list
        boolean result = excludeUrlPatterns.stream().anyMatch(p -> antPathMatcher.match(p, urlPathHelper.getRequestUri(request)));

        return result || urlPatterns.stream().noneMatch(p -> antPathMatcher.match(p, urlPathHelper.getRequestUri(request)));

    }

    /**
     * Adds exclude url patterns.
     *
     * @param excludeUrlPatterns exclude urls
     */
    public void addExcludeUrlPatterns(@NonNull String... excludeUrlPatterns) {
        Assert.notNull(excludeUrlPatterns, "Exclude url patterns must not be null");

        Collections.addAll(this.excludeUrlPatterns, excludeUrlPatterns);
    }

    /**
     * Gets exclude url patterns.
     *
     * @return exclude url patterns.
     */
    @NonNull
    public Set<String> getExcludeUrlPatterns() {
        return excludeUrlPatterns;
    }

    /**
     * Sets exclude url patterns.
     *
     * @param excludeUrlPatterns exclude urls
     */
    public void setExcludeUrlPatterns(@NonNull Collection<String> excludeUrlPatterns) {
        Assert.notNull(excludeUrlPatterns, "Exclude url patterns must not be null");

        this.excludeUrlPatterns = new HashSet<>(excludeUrlPatterns);
    }

    public Collection<String> getUrlPatterns() {
        return this.urlPatterns;
    }

    public void setUrlPatterns(Collection<String> urlPatterns) {
        Assert.notNull(urlPatterns, "UrlPatterns must not be null");
        this.urlPatterns = new LinkedHashSet<>(urlPatterns);
    }

    public void addUrlPatterns(String... urlPatterns) {
        Assert.notNull(urlPatterns, "UrlPatterns must not be null");
        Collections.addAll(this.urlPatterns, urlPatterns);
    }

    /**
     * Gets authentication failure handler. (Default: @DefaultAuthenticationFailureHandler)
     *
     * @return authentication failure handler
     */
    @NonNull
    private AuthenticationFailureHandler getFailureHandler() {
        if (failureHandler == null) {
            synchronized (this) {
                if (failureHandler == null) {
                    // Create default authentication failure handler
                    DefaultAuthenticationFailureHandler failureHandler = new DefaultAuthenticationFailureHandler();
                    failureHandler.setProductionEnv(gcmpProperties.isProductionEnv());

                    this.failureHandler = failureHandler;
                }
            }
        }
        return failureHandler;
    }

    /**
     * Sets authentication failure handler.
     *
     * @param failureHandler authentication failure handler
     */
    public synchronized void setFailureHandler(@NonNull AuthenticationFailureHandler failureHandler) {
        Assert.notNull(failureHandler, "Authentication failure handler must not be null");

        this.failureHandler = failureHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            doAuthenticate(request, response, filterChain);
        } catch (AbstractGcmpException e) {
            getFailureHandler().onFailure(request, response, e);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * Check if the sufficient one-time token is set.
     *
     * @param request http servlet request
     * @return true if sufficient; false otherwise
     */

    /**
     * Get token from http servlet request.
     *
     * @param request         http servlet request must not be null
     * @param tokenQueryName  token query name must not be blank
     * @param tokenHeaderName token header name must not be blank
     * @return corresponding token
     */
    protected String getTokenFromRequest(@NonNull HttpServletRequest request, @NonNull String tokenQueryName, @NonNull String tokenHeaderName) {
        Assert.notNull(request, "Http servlet request must not be null");
        Assert.hasText(tokenQueryName, "Token query name must not be blank");
        Assert.hasText(tokenHeaderName, "Token header name must not be blank");

        // Get from header
        String accessKey = request.getHeader(tokenHeaderName);

        // Get from param
        if (StringUtils.isBlank(accessKey)) {
            accessKey = request.getParameter(tokenQueryName);
            log.debug("Got access key from parameter: [{}: {}]", tokenQueryName, accessKey);
        } else {
            log.debug("Got access key from header: [{}: {}]", tokenHeaderName, accessKey);
        }

        return accessKey;
    }

}
