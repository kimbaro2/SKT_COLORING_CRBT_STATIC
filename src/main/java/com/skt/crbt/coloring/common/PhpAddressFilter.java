package com.skt.crbt.coloring.common;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Old .php addresses redirect to the same path without the extension.
 * Controllers are mapped without .php.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class PhpAddressFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        String context = request.getContextPath() == null ? "" : request.getContextPath();
        String path = uri.startsWith(context) ? uri.substring(context.length()) : uri;
        if (path.endsWith(".php")) {
            String target = context + path.substring(0, path.length() - 4);
            String query = request.getQueryString();
            if (query != null && query.length() > 0) {
                target = target + "?" + query;
            }
            response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
            response.setHeader("Location", target);
            return;
        }
        chain.doFilter(request, response);
    }
}
