package com.emailtohl.hjk.crm.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.emailtohl.hjk.crm.config.SecurityConfig;
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
		// 存储用户id和用户姓名，以“:”分开
		// 配置在com.emailtohl.hjk.crm.config.SecurityConfig中，当用户登录时，将id和姓名存储在
		// org.springframework.security.core.userdetails.User的username中
		// 匿名账号的id为0，代表在系统中不存在的id
		String username = "0" + SecurityConfig.SEPARATOR + "anonymous";
		if (auth != null && StringUtils.hasText(auth.getName())) {
			username = auth.getName();
		}
		StandardService.USER_ID.set(username);
		identityService.setAuthenticatedUserId(username.split(SecurityConfig.SEPARATOR)[0]);
		try {
			chain.doFilter(request, response);
		} finally {
			StandardService.USER_ID.remove();
			identityService.setAuthenticatedUserId(null);
		}
	}

	@Override
	public void destroy() {
	}

}
