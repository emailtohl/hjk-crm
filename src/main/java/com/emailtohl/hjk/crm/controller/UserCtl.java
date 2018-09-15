package com.emailtohl.hjk.crm.controller;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.identity.Picture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.emailtohl.hjk.crm.entities.GroupEnum;
import com.emailtohl.hjk.crm.entities.User;
import com.emailtohl.hjk.crm.user.UserService;
import com.github.emailtohl.lib.exception.InnerDataStateException;
import com.github.emailtohl.lib.exception.NotFoundException;
import com.github.emailtohl.lib.jpa.BaseEntity;
import com.github.emailtohl.lib.jpa.Paging;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

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
	@Autowired
	private AuthenticationManager authenticationManager;
	

	@ApiModel(value = "登录对象", description = "包含用户名和密码")
	private static class Login {
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
	@PostMapping("login")
	public Authentication login(@RequestBody Login login) throws UsernameNotFoundException, DisabledException, LockedException, BadCredentialsException, AuthenticationException {
		SecurityContextHolder.clearContext();
		Authentication token = new UsernamePasswordAuthenticationToken(login.emailOrCellPhone, login.password);
		Authentication authentication = authenticationManager.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return authentication;
	}
	
	/**
	 * 判断邮箱或手机号是否已存在
	 * 
	 * @param emailOrCellPhone
	 * @return
	 */
	@GetMapping("emailOrCellPhoneExist")
	public boolean emailOrCellPhoneExist(@RequestParam(required = false, defaultValue = "") String emailOrCellPhone) {
		if (StringUtils.hasText(emailOrCellPhone)) {
			return userService.emailIsExist(emailOrCellPhone);
		} else {
			return false;
		}
	}

	/**
	 * 用户名是否存在
	 * @param email
	 * @return
	 */
	@GetMapping("isEmailExist")
	public boolean isEmailExist(@RequestParam(required = false, defaultValue = "") String email) {
		if (StringUtils.hasText(email)) {
			return userService.emailIsExist(email);
		} else {
			return false;
		}
	}
	
	@GetMapping("isCellPhoneExist")
	public boolean isCellPhoneExist(@RequestParam(required = false, defaultValue = "") String cellPhone) {
		if (StringUtils.hasText(cellPhone)) {
			return userService.cellPhoneIsExist(cellPhone);
		} else {
			return false;
		}
	}

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
	@GetMapping("search")
	public Paging<User> search(@RequestParam(required = false, defaultValue = "") String query,
			@PageableDefault(page = 0, size = 10, sort = { BaseEntity.ID_PROPERTY_NAME,
					BaseEntity.MODIFY_DATE_PROPERTY_NAME }, direction = Direction.DESC) Pageable pageable) {
		query = query.replaceAll("是", "true");
		query = query.replaceAll("否", "false");
		return userService.search(query, pageable);
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
	@PostMapping("enable")
	public void enable(@RequestBody Form form) {
		userService.enable(form.id, form.enabled);
	}

	/**
	 * 设置用户的组id
	 * @param id
	 * @param groups
	 */
	@PostMapping("{id}/groups")
	public void setGroupIds(@PathVariable("id") Long id, @RequestBody GroupEnum[] groups) {
		userService.setGroups(id, groups);
	}

	/**
	 * 获取用户的组id
	 * @param id
	 * @return
	 */
	@GetMapping("{id}/groups")
	public String getGroupIds(@PathVariable("id") Long id) {
		return userService.getGroups(id).toString();
	}
	
	/**
	 * 获取用户头像
	 * @param id
	 * @param response
	 */
	@GetMapping("userPicture/{id}")
	public void getUserPicture(@PathVariable("id") Long id, HttpServletResponse response) {
		Picture pic = userService.getUserPicture(id);
		if (pic == null) {
			throw new NotFoundException("not exist " + id);
		}
		response.setHeader("content-disposition", "attachment;fileName=" + id.toString());
		response.setContentType(pic.getMimeType());
		try (ServletOutputStream out = response.getOutputStream()) {
			StreamUtils.copy(pic.getBytes(), out);
		} catch (IOException e) {
			throw new InnerDataStateException("read file failed", e);
		}
	}
	
	/**
	 * 重置用户密码
	 * @param form
	 */
	@PostMapping("resetPassword")
	public void resetPassword(@RequestBody Form form) {
		userService.resetPassword(form.id);
	}
	
	/**
	 * 更新个人的密码
	 * @param form
	 */
	@PostMapping("updateMyPassword")
	public void updateMyPassword(@RequestBody Form form) {
		userService.updateMyPassword(form.id, form.oldPassword, form.newPassword);
	}
	
	/**
	 * 上传头像，并返回id
	 * @param file
	 * @return
	 * @throws IOException 
	 */
    @PostMapping("uploadPicture/{userId}")
	public void fileUpload(@PathVariable("userId") String userId, @RequestParam("file") MultipartFile file) throws IOException {
		if (file.isEmpty()) {
			return;
		}
		Picture p = new Picture(file.getBytes(), file.getContentType());
		userService.setUserPicture(userId, p);
	}

	public static class Form {
		public Long id;
		public Boolean enabled;
		public String oldPassword;
		public String newPassword;
	}
}
