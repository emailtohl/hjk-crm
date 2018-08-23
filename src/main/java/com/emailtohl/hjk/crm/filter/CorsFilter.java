package com.emailtohl.hjk.crm.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
/**
 * 前后端分离，取消Cors后端的限制
 * @author HeLei
 */
@ConfigurationProperties("cros")
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {
	private Set<String> accessControlAllowOrigins = new HashSet<>();

	public void setAccessControlAllowOrigin(String accessControlAllowOrigin) {
		if (StringUtils.hasText(accessControlAllowOrigin)) {
			accessControlAllowOrigins.addAll(
					Arrays.stream(accessControlAllowOrigin.split(",")).map(s -> s.trim()).collect(Collectors.toSet()));
		}
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		final HttpServletResponse response = (HttpServletResponse) res;
		final HttpServletRequest request = (HttpServletRequest) req;
		String originHeader = request.getHeader("Origin");
		if (accessControlAllowOrigins.contains(originHeader) || accessControlAllowOrigins.contains("*")) {
			response.setHeader("Access-Control-Allow-Origin", originHeader);
			response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
			response.setHeader("Access-Control-Allow-Headers",
					"Content-Type, Access-Control-Allow-Headers, Authorization, X-CSRF-TOKEN, X-Requested-With, X-Auth-Token, X-Xsrf-Token");
			response.setHeader("Access-Control-Max-Age", "3600");
			response.setHeader("Access-Control-Allow-Credentials", "true");
		}
		if ("OPTIONS".equalsIgnoreCase(((HttpServletRequest) req).getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			chain.doFilter(req, res);
		}
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}
}