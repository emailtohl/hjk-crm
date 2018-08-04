package com.emailtohl.hjk.crm.config;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.persistence.EntityManagerFactory;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.Picture;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StreamUtils;

/**
 * 初始化内置数据
 * 
 * @author HeLei
 */
@Configuration
@Import({ BeansConfig.class })
public class InitConfig {
	@Autowired
	private IdentityService identityService;
	@Autowired
	private EntityManagerFactory factory;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Bean
	public User userAdmin() throws IOException {
		String userId = "admin";
		String password = "admin";
		String groupId = "ADMIN";
		String actuator = "ACTUATOR";
		User u = null;
		u = identityService.createUserQuery().userId(userId).singleResult();
		if (u != null) {
			return u;
		}
		u = identityService.newUser(userId);
		u.setPassword(hashpw(password));
		u.setEmail(userId + "@localhost");
		u.setFirstName(userId);
		u.setLastName(userId);
		identityService.saveUser(u);

//		identityService.setUserInfo(userId, UserInfo.key_name, userId);
//		identityService.setUserInfo(userId, UserInfo.key_cellPhone, "18712345678");

		ClassPathResource r = new ClassPathResource("image/icon-head-admin.png");
		try (InputStream in = r.getInputStream()) {
			byte[] bytes = StreamUtils.copyToByteArray(in);
			Picture p = new Picture(bytes, "application/x-png");
			identityService.setUserPicture(userId, p);
		}

		Group g = identityService.createGroupQuery().groupId(groupId).singleResult();
		if (g == null) {
			g = identityService.newGroup(groupId);
			g.setName("administrator");
			identityService.saveGroup(g);
		}
		identityService.createMembership(userId, groupId);

		g = identityService.createGroupQuery().groupId(actuator).singleResult();
		if (g == null) {
			g = identityService.newGroup(actuator);
			g.setName("ACTUATOR");
			identityService.saveGroup(g);
		}
		identityService.createMembership(userId, actuator);

		Date.from(LocalDate.now().minusYears(16).atStartOfDay(ZoneId.systemDefault()).toInstant());

		return u;
	}

	private String hashpw(String password) {
		// String salt = BCrypt.gensalt(10, new SecureRandom());
		// return BCrypt.hashpw(password, salt);
		return passwordEncoder.encode(password);
	}
}
