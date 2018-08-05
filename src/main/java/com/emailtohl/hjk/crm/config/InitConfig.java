package com.emailtohl.hjk.crm.config;

import java.io.IOException;
import java.io.InputStream;

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

import com.emailtohl.hjk.crm.entities.GroupId;
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
	public Group admin() {
		Group g = identityService.createGroupQuery().groupId(GroupId.ADMIN.name()).singleResult();
		if (g == null) {
			g = identityService.newGroup(GroupId.ADMIN.name());
			g.setName("系统管理员");
			g.setType(UserType.EMPLOYEE.name());
			identityService.saveGroup(g);
		}
		return g;
	}
	
	@Bean
	public Group finance() {
		Group g = identityService.createGroupQuery().groupId(GroupId.FINANCE.name()).singleResult();
		if (g == null) {
			g = identityService.newGroup(GroupId.FINANCE.name());
			g.setName("财务");
			g.setType(UserType.EMPLOYEE.name());
			identityService.saveGroup(g);
		}
		return g;
	}
	
	@Bean
	public Group administration() {
		Group g = identityService.createGroupQuery().groupId(GroupId.ADMINISTRATION.name()).singleResult();
		if (g == null) {
			g = identityService.newGroup(GroupId.ADMINISTRATION.name());
			g.setName("行政");
			g.setType(UserType.EMPLOYEE.name());
			identityService.saveGroup(g);
		}
		return g;
	}
	
	@Bean
	public Group market() {
		Group g = identityService.createGroupQuery().groupId(GroupId.MARKET.name()).singleResult();
		if (g == null) {
			g = identityService.newGroup(GroupId.MARKET.name());
			g.setName("市场");
			g.setType(UserType.EMPLOYEE.name());
			identityService.saveGroup(g);
		}
		return g;
	}
	
	@Bean
	public Group customer() {
		Group g = identityService.createGroupQuery().groupId(GroupId.CUSTOMER.name()).singleResult();
		if (g == null) {
			g = identityService.newGroup(GroupId.CUSTOMER.name());
			g.setName("客户");
			g.setType(UserType.CUSTOMER.name());
			identityService.saveGroup(g);
		}
		return g;
	}

	@Bean
	public User userAdmin() throws IOException {
		String name = "admin";
		String password = passwordEncoder.encode("admin");
		String groupId = GroupId.ADMIN.name();
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

			identityService.createMembership(name, groupId);

		}
		em.getTransaction().commit();
		em.close();
		
		return user;
	}

}
