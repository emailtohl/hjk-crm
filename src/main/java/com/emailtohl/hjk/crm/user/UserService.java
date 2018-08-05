package com.emailtohl.hjk.crm.user;

import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.emailtohl.hjk.crm.entities.GroupId;
import com.emailtohl.hjk.crm.entities.User;
import com.github.emailtohl.lib.jpa.Paging;

/**
 * 用户信息管理接口
 * @author HeLei
 */
public interface UserService {
	
	/**
	 * 用户名是否存在
	 * @param name
	 * @return
	 */
	boolean exist(String name);
	
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
	
	/**
	 * 设置用户的所属组，但不包括ADMIN
	 * @param id 用户id
	 * @param groupIds 组id
	 */
	void setGroupIds(Long id, GroupId... groupIds);
	
	/**
	 * 获取用的相关组名
	 * @param id 用户id
	 * @return
	 */
	Set<GroupId> getGroupIds(Long id);
	
}
