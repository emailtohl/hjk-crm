package com.emailtohl.hjk.crm.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import com.github.emailtohl.lib.StandardService;

import org.activiti.engine.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 设置用户名
 * 由Spring security管理
 * @author HeLei
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class UsernameFilter implements Filter {
	@Autowired
	private IdentityService identityService;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = "anonymous";
		if (auth != null && StringUtils.hasText(auth.getName())) {
			username = auth.getName();
		}
		StandardService.USERNAME.set(username);
		identityService.setAuthenticatedUserId(username);
		try {
			chain.doFilter(request, response);
		} finally {
			StandardService.USERNAME.remove();
			identityService.setAuthenticatedUserId(null);
		}
	}

	@Override
	public void destroy() {
	}

}
