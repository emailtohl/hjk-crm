package com.emailtohl.hjk.crm.user;

import com.emailtohl.hjk.crm.entities.User;
import com.github.emailtohl.lib.jpa.SearchInterface;

/**
 * 实现自定义接口
 * 
 * @author HeLei
 */
interface UserRepoCust extends SearchInterface<User, Long> {

	/**
	 * 判断邮箱或手机号是否已存在
	 * 
	 * @param emailOrCellPhone
	 * @return
	 */
	boolean emailOrCellPhoneExist(String emailOrCellPhone);

	/**
	 * 通过用户名或手机号查询用户
	 * 
	 * @param emailOrCellPhone
	 * @return 若未查询到，则返回null
	 */
	User byEmailOrCellPhone(String emailOrCellPhone);
}
