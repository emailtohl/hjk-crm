package com.emailtohl.hjk.crm.config;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.Picture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StreamUtils;

import com.emailtohl.hjk.crm.entities.User;
import com.emailtohl.hjk.crm.entities.UserType;

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
		String name = "admin";
		String password = passwordEncoder.encode("admin");
		String groupId = "ADMIN";
		String actuator = "ACTUATOR";
		User user = null;
		
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		
		CriteriaBuilder b = em.getCriteriaBuilder();
		CriteriaQuery<User> q = b.createQuery(User.class);
		Root<User> r = q.from(User.class);
		q = q.select(r).where(b.equal(r.get("name"), name));
		try {
			user = em.createQuery(q).getSingleResult();
		} catch (NoResultException e) {}
		if (user == null) {
			user = new User();
			user.setUserType(UserType.EMPLOYEE);
			user.setName(name);
			user.setNickname(name);
			user.setPassword(password);
			em.persist(user);
			
			org.activiti.engine.identity.User u = identityService.createUserQuery().userId(name).singleResult();
			u = identityService.newUser(name);
			u.setPassword(password);
			u.setEmail(name + "@localhost");
			u.setFirstName(name);
			identityService.saveUser(u);
			identityService.setUserInfo(name, "userType", user.getUserType().toString());
			
			ClassPathResource resource = new ClassPathResource("image/icon-head-admin.png");
			try (InputStream in = resource.getInputStream()) {
				byte[] bytes = StreamUtils.copyToByteArray(in);
				Picture p = new Picture(bytes, "application/x-png");
				identityService.setUserPicture(name, p);
			}

			Group g = identityService.createGroupQuery().groupId(groupId).singleResult();
			if (g == null) {
				g = identityService.newGroup(groupId);
				g.setName("administrator");
				identityService.saveGroup(g);
			}
			identityService.createMembership(name, groupId);

			g = identityService.createGroupQuery().groupId(actuator).singleResult();
			if (g == null) {
				g = identityService.newGroup(actuator);
				g.setName("ACTUATOR");
				identityService.saveGroup(g);
			}
			identityService.createMembership(name, actuator);

			Date.from(LocalDate.now().minusYears(16).atStartOfDay(ZoneId.systemDefault()).toInstant());
		}
		em.getTransaction().commit();
		em.close();
		
		return user;
	}

}
