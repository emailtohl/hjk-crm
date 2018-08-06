package com.emailtohl.hjk.crm.config;

import static com.emailtohl.hjk.crm.entities.GroupEnum.ADMIN;
import static com.emailtohl.hjk.crm.entities.GroupEnum.ADMINISTRATION;
import static com.emailtohl.hjk.crm.entities.GroupEnum.CUSTOMER;
import static com.emailtohl.hjk.crm.entities.GroupEnum.FINANCE;
import static com.emailtohl.hjk.crm.entities.GroupEnum.MARKET;

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

import com.emailtohl.hjk.crm.entities.User;

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
		Group g = identityService.createGroupQuery().groupId(ADMIN.id).singleResult();
		if (g == null) {
			g = identityService.newGroup(ADMIN.id);
			g.setName(ADMIN.name);
			g.setType(ADMIN.type);
			identityService.saveGroup(g);
		}
		return g;
	}

	@Bean
	public Group finance() {
		Group g = identityService.createGroupQuery().groupId(FINANCE.name()).singleResult();
		if (g == null) {
			g = identityService.newGroup(FINANCE.name());
			g.setName(FINANCE.name);
			g.setType(FINANCE.type);
			identityService.saveGroup(g);
		}
		return g;
	}

	@Bean
	public Group administration() {
		Group g = identityService.createGroupQuery().groupId(ADMINISTRATION.name()).singleResult();
		if (g == null) {
			g = identityService.newGroup(ADMINISTRATION.name());
			g.setName(ADMINISTRATION.name);
			g.setType(ADMINISTRATION.type);
			identityService.saveGroup(g);
		}
		return g;
	}

	@Bean
	public Group market() {
		Group g = identityService.createGroupQuery().groupId(MARKET.name()).singleResult();
		if (g == null) {
			g = identityService.newGroup(MARKET.name());
			g.setName(MARKET.name);
			g.setType(MARKET.type);
			identityService.saveGroup(g);
		}
		return g;
	}

	@Bean
	public Group customer() {
		Group g = identityService.createGroupQuery().groupId(CUSTOMER.name()).singleResult();
		if (g == null) {
			g = identityService.newGroup(CUSTOMER.name());
			g.setName(CUSTOMER.name);
			g.setType(CUSTOMER.type);
			identityService.saveGroup(g);
		}
		return g;
	}

	@Bean
	public User userAdmin() throws IOException {
		String name = "admin";
		String password = passwordEncoder.encode("admin");
		User auser = null;

		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();

		CriteriaBuilder b = em.getCriteriaBuilder();
		CriteriaQuery<User> q = b.createQuery(User.class);
		Root<User> r = q.from(User.class);
		q = q.select(r).where(b.equal(r.get("name"), name));
		try {
			auser = em.createQuery(q).getSingleResult();
		} catch (NoResultException e) {
		}
		if (auser == null) {
			auser = new User();
			auser.setName(name);
			auser.setNickname(name);
			auser.setPassword(password);
			em.persist(auser);

			org.activiti.engine.identity.User u = identityService.createUserQuery().userId(name).singleResult();
			u = identityService.newUser(name);
			u.setPassword(password);
			u.setEmail(name + "@localhost");
			u.setFirstName(name);
			identityService.saveUser(u);

			ClassPathResource resource = new ClassPathResource("image/icon-head-admin.png");
			try (InputStream in = resource.getInputStream()) {
				byte[] bytes = StreamUtils.copyToByteArray(in);
				Picture p = new Picture(bytes, "application/x-png");
				identityService.setUserPicture(name, p);
			}

			identityService.createMembership(name, ADMIN.id);
		}
		em.getTransaction().commit();
		em.close();

		return auser;
	}

}
