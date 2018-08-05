package com.emailtohl.hjk.crm.user;

import org.springframework.data.domain.Pageable;

import com.emailtohl.hjk.crm.entities.User;
import com.github.emailtohl.lib.jpa.Paging;

/**
 * 用户信息管理接口
 * @author HeLei
 */
public interface UserService {
	/**
	 * 创建用户信息
	 * @param user
	 * @return
	 */
	User create(User user);
	
	/**
	 * 读取用户信息
	 * @param id
	 * @return
	 */
	User read(Long id);
	
	/**
	 * 查询用户信息
	 * @param query
	 * @param pageable
	 * @return
	 */
	Paging<User> query(String query, Pageable pageable);
	
	/**
	 * 修改用户信息
	 * @param id
	 * @param User
	 * @return
	 */
	User update(Long id, User User);
	
	/**
	 * 删除用户信息
	 * @param id
	 */
	void delete(Long id);
	
	/**
	 * 是否启用
	 * @param id
	 * @param enabled
	 */
	void enable(Long id, boolean enabled);
	
}
