package com.emailtohl.hjk.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.emailtohl.hjk.crm.entities.User;
import com.emailtohl.hjk.crm.user.UserService;
import com.github.emailtohl.lib.jpa.BaseEntity;
import com.github.emailtohl.lib.jpa.Paging;

/**
 * 用户信息控制接口
 * 
 * @author HeLei
 */
@RestController
@RequestMapping(value = "users", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UserCtl {
	@Autowired
	private UserService userService;

	/**
	 * 创建用户信息
	 * 
	 * @param user
	 * @return
	 */
	@PostMapping
	public User create(@RequestBody User user) {
		return userService.create(user);

	}

	/**
	 * 读取用户信息
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("{id}")
	public User read(@PathVariable("id") Long id) {
		return userService.read(id);
	}

	/**
	 * 查询用户信息
	 * 
	 * @param query
	 * @param pageable
	 * @return
	 */
	@GetMapping("query")
	public Paging<User> query(@RequestParam(required = false, defaultValue = "") String query,
			@PageableDefault(page = 0, size = 20, sort = { BaseEntity.ID_PROPERTY_NAME,
					BaseEntity.MODIFY_DATE_PROPERTY_NAME }, direction = Direction.DESC) Pageable pageable) {
		return userService.query(query, pageable);
	}

	/**
	 * 修改用户信息
	 * 
	 * @param id
	 * @param User
	 * @return
	 */
	@PutMapping("{id}")
	public User update(@PathVariable("id") Long id, @RequestBody User User) {
		return userService.update(id, User);
	}

	/**
	 * 删除用户信息
	 * 
	 * @param id
	 */
	@DeleteMapping("{id}")
	public void delete(@PathVariable("id") Long id) {
		userService.delete(id);
	}

	/**
	 * 是否启用
	 * 
	 * @param id
	 * @param enabled
	 */
	@PostMapping("approve")
	public void enable(@RequestBody Form form) {
		userService.enable(form.id, form.enabled);
	}

	public static class Form {
		public Long id;
		public Boolean enabled;
	}
}
