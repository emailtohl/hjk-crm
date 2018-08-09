package com.emailtohl.hjk.crm.user;

import com.emailtohl.hjk.crm.entities.User;
import com.github.emailtohl.lib.jpa.SearchInterface;
/**
 * 实现自定义接口
 * @author HeLei
 */
interface UserRepoCust extends SearchInterface<User, Long> {
}
