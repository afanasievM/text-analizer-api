package com.ligazakon.test.ligazakon.filters;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class ControllersFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ContentCachingRequestWrapper wreq = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper wres = new ContentCachingResponseWrapper((HttpServletResponse) response);
        log.info("{}: {}?{}", wreq.getMethod(), wreq.getRequestURI(), wreq.getQueryString() != null ? wreq.getQueryString() : "");
        chain.doFilter(wreq, wres);
        while (wreq.getInputStream().read() >= 0) {
        }
        log.info("Response: {}", new String(wres.getContentAsByteArray()));
        wres.copyBodyToResponse();
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
