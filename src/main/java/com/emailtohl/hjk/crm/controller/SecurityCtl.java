package com.emailtohl.hjk.crm.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emailtohl.hjk.crm.entities.GroupId;

/**
 * 安全相关的接口
 * 
 * @author HeLei
 */
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class SecurityCtl {

	/**
	 * 获取csrf令牌
	 * 
	 * @param token
	 * @return
	 */
	@GetMapping(value = "csrf", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public CsrfToken csrf(CsrfToken token) {
		return token;
	}
	
	/**
	 * 获取用户身份
	 * @param principal
	 * @return
	 */
	@GetMapping(value = "principal", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Principal principal(Principal principal) {
		return principal;
	}
	
	private Set<GroupId> groupIds = Arrays.stream(GroupId.values()).filter(groupId -> {
		return GroupId.ADMIN != groupId;
	}).collect(Collectors.toSet());
	/**
	 * 获取所有组
	 * @return
	 */
	@GetMapping("groupIds")
	public Set<GroupId> getGroupIds() {
		return groupIds;
	}

}
