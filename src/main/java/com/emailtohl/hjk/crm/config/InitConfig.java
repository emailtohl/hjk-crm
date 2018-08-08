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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.emailtohl.hjk.crm.entities.GroupEnum;
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

	private Group getGroup(GroupEnum groupEnum) {
		Group g = identityService.createGroupQuery().groupId(groupEnum.id).singleResult();
		if (g == null) {
			g = identityService.newGroup(groupEnum.id);
			g.setName(groupEnum.name);
			g.setType(groupEnum.type);
			identityService.saveGroup(g);
		}
		return g;
	}

	@Bean
	public Group admin() {
		return getGroup(ADMIN);
	}

	@Bean
	public Group finance() {
		return getGroup(FINANCE);
	}

	@Bean
	public Group administration() {
		return getGroup(ADMINISTRATION);
	}

	@Bean
	public Group market() {
		return getGroup(MARKET);
	}

	@Bean
	public Group customer() {
		return getGroup(CUSTOMER);
	}

	private User getUser(String email, String password, GroupEnum groupEnum, String name, String nickname,
			String cellPhone) {
		String pw = passwordEncoder.encode(password);
		User auser = null;

		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();

		CriteriaBuilder b = em.getCriteriaBuilder();
		CriteriaQuery<User> q = b.createQuery(User.class);
		Root<User> r = q.from(User.class);
		q = q.select(r).where(b.equal(r.get("email"), email));
		try {
			auser = em.createQuery(q).getSingleResult();
		} catch (NoResultException e) {
		}
		if (auser == null) {
			auser = new User();
			auser.setEmail(email);
			auser.setPassword(pw);
			auser.setName(name);
			auser.setNickname(nickname);
			auser.setCellPhone(cellPhone);
			em.persist(auser);

			org.activiti.engine.identity.User u = identityService.createUserQuery().userId(auser.getId().toString())
					.singleResult();
			if (u == null) {
				String userId = auser.getId().toString();
				u = identityService.newUser(userId);
				u.setEmail(email);
				u.setPassword(pw);
				u.setFirstName(name);
				u.setLastName(nickname);
				identityService.saveUser(u);
				if (StringUtils.hasText(cellPhone)) {
					identityService.setUserInfo(userId, "cellPhone", cellPhone);
				}
				identityService.createMembership(userId, groupEnum.id);
			}
		}
		em.getTransaction().commit();
		em.close();
		return auser;
	}

	@Bean
	public User userAdmin(@Qualifier("admin") Group admin, @Qualifier("finance") Group finance,
			@Qualifier("administration") Group administration, @Qualifier("market") Group market,
			@Qualifier("customer") Group customer) throws IOException {
		// email, password, groupEnum, name, nickname, cellPhone
		User u = getUser("admin@localhost", "admin", ADMIN, "admin", "admin", "19012345678");
		ClassPathResource resource = new ClassPathResource("image/icon-head-admin.png");
		try (InputStream in = resource.getInputStream()) {
			byte[] bytes = StreamUtils.copyToByteArray(in);
			Picture p = new Picture(bytes, "application/x-png");
			identityService.setUserPicture(u.getId().toString(), p);
		}
		return u;
	}

	@Bean
	public User lisa(@Qualifier("admin") Group admin, @Qualifier("finance") Group finance,
			@Qualifier("administration") Group administration, @Qualifier("market") Group market,
			@Qualifier("customer") Group customer) throws IOException {
		// email, password, groupEnum, name, nickname, cellPhone
		User u = getUser("398776453@qq.com", "lisa", ADMINISTRATION, "Ms Huang", "lisa", "13996248085");
		ClassPathResource resource = new ClassPathResource("image/icon-head-lisa.jpg");
		try (InputStream in = resource.getInputStream()) {
			byte[] bytes = StreamUtils.copyToByteArray(in);
			Picture p = new Picture(bytes, "application/x-png");
			identityService.setUserPicture(u.getId().toString(), p);
		}
		return u;
	}

	@Bean
	public User mark(@Qualifier("admin") Group admin, @Qualifier("finance") Group finance,
			@Qualifier("administration") Group administration, @Qualifier("market") Group market,
			@Qualifier("customer") Group customer) throws IOException {
		// email, password, groupEnum, name, nickname, cellPhone
		return getUser("mark@localhost", "mark", MARKET, null, "mark", null);
	}

	@Bean
	public User adminw(@Qualifier("admin") Group admin, @Qualifier("finance") Group finance,
			@Qualifier("administration") Group administration, @Qualifier("market") Group market,
			@Qualifier("customer") Group customer) throws IOException {
		// email, password, groupEnum, name, nickname, cellPhone
		return getUser("adminw@localhost", "adminw", ADMINISTRATION, "adminw", "adminw", null);
	}

	@Bean
	public User troungSon(@Qualifier("admin") Group admin, @Qualifier("finance") Group finance,
			@Qualifier("administration") Group administration, @Qualifier("market") Group market,
			@Qualifier("customer") Group customer) throws IOException {
		// email, password, groupEnum, name, nickname, cellPhone
		return getUser("troung@localhost", "troung", CUSTOMER, "troung", "son", null);
	}

	@Bean
	public User amy(@Qualifier("admin") Group admin, @Qualifier("finance") Group finance,
			@Qualifier("administration") Group administration, @Qualifier("market") Group market,
			@Qualifier("customer") Group customer) throws IOException {
		// email, password, groupEnum, name, nickname, cellPhone
		return getUser("amy@localhost", "amy", FINANCE, "amy", "amy", null);
	}

}
