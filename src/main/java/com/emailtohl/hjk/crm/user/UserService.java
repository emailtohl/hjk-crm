package com.emailtohl.hjk.crm.user;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.activiti.engine.identity.Picture;
import org.springframework.data.domain.Pageable;

import com.emailtohl.hjk.crm.entities.GroupEnum;
import com.emailtohl.hjk.crm.entities.User;
import com.github.emailtohl.lib.jpa.Paging;

/**
 * 用户信息管理接口
 * @author HeLei
 */
public interface UserService {
	
	/**
	 * 邮箱是否存在
	 * @param email
	 * @return
	 */
	boolean emailIsExist(String email);
	
	/**
	 * 手机号是否存在
	 * @param cellPhone
	 * @return
	 */
	boolean cellPhoneIsExist(String cellPhone);
	
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
	Paging<User> search(String query, Pageable pageable);
	
	/**
	 * 获取所有用户
	 * @return
	 */
	List<User> allUsers();
	
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
	 * @param groups 组id
	 */
	void setGroups(Long id, GroupEnum... groups);
	
	/**
	 * 获取用的相关组名
	 * @param id 用户id
	 * @return
	 */
	Set<GroupEnum> getGroups(Long id);
	
	/**
	 * 获取用户头像
	 * @param id
	 * @return
	 */
	Picture getUserPicture(Long id);
	
	/**
	 * 重置密码为123456
	 * @param id
	 */
	void resetPassword(Long id);
	
	/**
	 * 只能由自己修改自己的密码
	 * @param id
	 * @param oldPassword
	 * @param newPassword
	 */
	void updateMyPassword(@NotNull Long id, @NotNull String oldPassword, @NotNull String newPassword);
	
	/**
	 * 刷新最后登录时间
	 */
	void refreshLastLoginTime(Long id);
}
