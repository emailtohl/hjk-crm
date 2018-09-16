package com.emailtohl.hjk.crm.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emailtohl.hjk.crm.entities.GroupEnum;

import io.swagger.annotations.ApiOperation;

/**
 * 一些基础数据的接口
 * 
 * @author HeLei
 */
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class InfoCtl {
	private static final Logger LOG = LogManager.getLogger();
	@Autowired
	private MessageSource messageSource;

	/**
	 * 获取csrf令牌
	 * 
	 * @param token
	 * @return
	 */
	@ApiOperation(value = "获取csrf token", notes = "POST，PUT，DELETE等操作需要先在此接口处获取csrf token，不需要参数")
	@GetMapping(value = "csrf", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public CsrfToken csrf(CsrfToken token) {
		Locale locale = LocaleContextHolder.getLocale();
		LOG.debug(messageSource.getMessage("welcome", new Object[] {}, locale));
		return token;
	}

	/**
	 * 获取用户身份
	 * 
	 * @param principal
	 * @return
	 */
	@ApiOperation(value = "获取用户身份信息", notes = "登录后获取用户身份信息，不需要参数")
	@GetMapping(value = "principal", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Principal principal(Principal principal) {
		return principal;
	}

	private String groupsJson = Arrays.stream(GroupEnum.values()).filter(g -> {
		return GroupEnum.ADMIN != g;
	}).map(GroupEnum::toString).collect(Collectors.toList()).toString();

	/**
	 * 获取所有组
	 * 
	 * @return
	 */
	@GetMapping("groups")
	public String getGroups() {
		return groupsJson;
	}

	/**
	 * session ID包含在名为“X-Auth-Token”的header中
	 * @param session
	 * @return
	 */
	@ApiOperation(value = "获取session id", notes = "获取当前session的id，不需要参数")
	@GetMapping("token")
	public Map<String, String> token(HttpSession session) {
		return Collections.singletonMap("token", session.getId());
	}
}
