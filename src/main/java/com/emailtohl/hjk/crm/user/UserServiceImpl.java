package com.emailtohl.hjk.crm.user;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.activiti.engine.IdentityService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.emailtohl.hjk.crm.entities.User;
import com.github.emailtohl.lib.StandardService;
import com.github.emailtohl.lib.jpa.Paging;
/**
 * 用户信息管理接口的实现
 * @author HeLei
 */
@Service
@Transactional
public class UserServiceImpl extends StandardService<User, Long, Long> implements UserService {
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private IdentityService identityService;
	
	@Override
	public User create(@Valid User user) {
		validate(user);
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		user.setCredentialsNonExpired(true);
		user.setEnabled(true);
		String hashedPw = passwordEncoder.encode(user.getPassword());
		user.setPassword(hashedPw);
		userRepo.persist(user);
		org.activiti.engine.identity.User u = identityService.newUser(user.getName());
		u.setEmail(user.getEmail());
		u.setFirstName(user.getNickname());
		u.setPassword(hashedPw);
		identityService.setUserInfo(user.getName(), "userType", user.getUserType().toString());
		identityService.saveUser(u);
		return user;
	}

	@Override
	public User read(Long id) {
		User source = userRepo.findById(id).get();
		return toTransient(source);
	}

	@Override
	public Paging<User> query(User example, Pageable pageable) {
		Page<User> page = userRepo.queryForPage(example, pageable);
		List<User> ls = page.getContent().stream().map(this::toTransient).collect(Collectors.toList());
		return new Paging<>(ls, pageable, page.getTotalElements());
	}

	@Override
	public List<User> query(User example) {
		return userRepo.queryForList(example).stream().map(this::toTransient).collect(Collectors.toList());
	}

	@Override
	public User update(Long id, User user) {
		User source = userRepo.findById(id).get();
		org.activiti.engine.identity.User u = identityService.createUserQuery().userId(id.toString()).singleResult();
		if (user.getUserType() != null) {
			source.setUserType(user.getUserType());
			identityService.setUserInfo(id.toString(), "userType", user.getUserType().toString());
		}
		if (user.getIdentityType() != null) {
			source.setIdentityType(user.getIdentityType());
		}
		if (hasText(user.getIdNumber())) {
			source.setIdNumber(user.getIdNumber());
		}
		if (hasText(user.getNickname())) {
			source.setNickname(user.getNickname());
			u.setFirstName(user.getNickname());
		}
		if (hasText(user.getEmail())) {
			source.setEmail(user.getEmail());
			u.setEmail(user.getEmail());
		}
		if (hasText(user.getCellPhone())) {
			source.setCellPhone(user.getCellPhone());
		}
		if (hasText(user.getAddress())) {
			source.setAddress(user.getAddress());
		}
		if (user.getBirthday() != null) {
			source.setBirthday(user.getBirthday());
		}
		if (user.getGender() != null) {
			source.setGender(user.getGender());
		}
		if (user.getImage() != null) {
			source.setImage(user.getImage());
		}
		if (hasText(user.getDescription())) {
			source.setDescription(user.getDescription());
		}
		identityService.saveUser(u);
		return toTransient(source);
	}

	@Override
	public void delete(Long id) {
		userRepo.deleteById(id);
	}

	@Override
	public Paging<User> query(String query, Pageable pageable) {
		Page<User> page = userRepo.search(query, pageable);
		List<User> ls = page.getContent().stream().map(this::toTransient).collect(Collectors.toList());
		return new Paging<>(ls, pageable, page.getTotalElements());
	}

	@Override
	public void enable(Long id, boolean enabled) {
		User user = userRepo.findById(id).get();
		user.setEnabled(enabled);
	}
	
	@Override
	protected User toTransient(User source) {
		if (source == null) {
			return source;
		}
		User target = new User();
		BeanUtils.copyProperties(source, target,
				User.getIgnoreProperties("password", "lastLogin", "lastChangeCredentials", "age"));
		return target;
	}

	@Override
	protected User transientDetail(@Valid User source) {
		return toTransient(source);
	}
	
}
