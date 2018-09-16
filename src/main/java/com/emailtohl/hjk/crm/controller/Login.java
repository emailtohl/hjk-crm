package com.emailtohl.hjk.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 为开发测试提供的登录接口
 * @author HeLei
 */
@Profile("dev")
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class Login {
	@Autowired
	private AuthenticationManager authenticationManager;

	@ApiModel(value = "登录对象", description = "包含用户名和密码")
	private static class Body {
		@ApiModelProperty(value = "邮箱或手机号", name = "emailOrCellPhone", example = "emailtohl@163.com", required = true)
		public String emailOrCellPhone;
		@ApiModelProperty(value = "密码", name = "password", example = "\"123456\"", required = true)
		public String password;
	}
	
	/**
	 * 登录，主要用于Swagger等接口需要使用
	 * @param emailOrCellPhone
	 * @param password
	 * @return
	 */
	@ApiOperation(value = "登录", notes = "邮箱或手机号进行登录")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "登录成功"),
		@ApiResponse(code = 401, message = "登录失败"),
		@ApiResponse(code = 404, message = "未找到用户名"),
	})
	@PostMapping("_login")
	public Authentication login(@RequestBody Body body) throws UsernameNotFoundException, DisabledException, LockedException, BadCredentialsException, AuthenticationException {
		SecurityContextHolder.clearContext();
		Authentication token = new UsernamePasswordAuthenticationToken(body.emailOrCellPhone, body.password);
		Authentication authentication = authenticationManager.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return authentication;
	}
}
