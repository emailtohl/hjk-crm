package com.emailtohl.hjk.crm.user;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.Picture;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.emailtohl.hjk.crm.config.SecurityConfig;
import com.emailtohl.hjk.crm.entities.GroupEnum;
import com.emailtohl.hjk.crm.entities.User;
import com.github.emailtohl.lib.StandardService;
import com.github.emailtohl.lib.exception.ForbiddenException;
import com.github.emailtohl.lib.jpa.Paging;

/**
 * 用户信息管理接口的实现
 * 
 * @author HeLei
 */
@Service
@Transactional
public class UserServiceImpl extends StandardService<User, Long> implements UserService {
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private IdentityService identityService;
	@Autowired
	@Qualifier("userAdmin")
	private User admin;
	
	private ExampleMatcher emailMatcher = ExampleMatcher.matching().withMatcher("email", GenericPropertyMatchers.exact());
	private ExampleMatcher cellPhoneMatcher = ExampleMatcher.matching().withMatcher("cellPhone", GenericPropertyMatchers.exact());
	
	@Override
	public boolean emailOrCellPhoneExist(String emailOrCellPhone) {
		return userRepo.emailOrCellPhoneExist(emailOrCellPhone);
	}

	@Override
	public User byEmailOrCellPhone(String emailOrCellPhone) {
		return userRepo.byEmailOrCellPhone(emailOrCellPhone);
	}
	
	@Override
	public boolean emailIsExist(String email) {
		User u = new User();
		u.setEmail(email);
		Example<User> example = Example.<User>of(u, emailMatcher);
		return userRepo.exists(example);
	}

	@Override
	public boolean cellPhoneIsExist(String cellPhone) {
		User u = new User();
		u.setCellPhone(cellPhone);
		Example<User> example = Example.<User>of(u, cellPhoneMatcher);
		return userRepo.exists(example);
	}

	@Override
	public User create(@Valid User user) {
		validate(user);
		if (!hasText(user.getName())) {// 如果用户名是空的，那么将邮箱前缀作为用户名
			user.setName(user.getEmail().split("@")[0].trim());
		}
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		user.setCredentialsNonExpired(true);
		
		Set<GroupEnum> groups = user.getGroups();
		// 若该用户不仅是属于客户组的话，那么size就会大于1
		// 如果size小于等于1的话，那么就判断该用户是否只含有客户组
		// 以上两种情况，该用户属于内部账号，不能立即启用
		if (groups.size() > 1 || (groups.size() == 1 && !groups.contains(GroupEnum.CUSTOMER))) {
			user.setEnabled(false);
		} else {
			user.setEnabled(true);
		}
		
		String hashedPw = passwordEncoder.encode(user.getPassword());
		user.setPassword(hashedPw);
		userRepo.persist(user);
		String userId = user.getId().toString();
		org.activiti.engine.identity.User u = identityService.newUser(userId);
		u.setEmail(user.getEmail());
		u.setFirstName(user.getName());
		u.setLastName(user.getNickname());
		u.setPassword(hashedPw);
		identityService.saveUser(u);
		identityService.setUserInfo(userId, "cellPhone", user.getCellPhone());
		identityService.setUserInfo(userId, "idNumber", user.getIdNumber());
		user.getGroups().forEach(g -> identityService.createMembership(userId, g.id));
		return transientDetail(user);
	}

	@Override
	public User read(Long id) {
		User source = userRepo.findById(id).get();
		return transientDetail(source);
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
	public List<User> allUsers() {
		return userRepo.findAll().stream().map(this::toTransient).collect(Collectors.toList());
	}

	@Override
	public User update(Long id, User user) {
		User source = userRepo.findById(id).get();
		org.activiti.engine.identity.User u = identityService.createUserQuery().userId(id.toString()).singleResult();
		if (user.getIdentityType() != null) {
			source.setIdentityType(user.getIdentityType());
		}
		if (hasText(user.getIdNumber())) {
			source.setIdNumber(user.getIdNumber());
			identityService.setUserInfo(id.toString(), "idNumber", user.getIdNumber());
		}
		if (hasText(user.getName())) {
			source.setName(user.getName());
			u.setFirstName(user.getName());
		}
		if (hasText(user.getNickname())) {
			source.setNickname(user.getNickname());
			u.setLastName(user.getNickname());
		}
		if (hasText(user.getEmail())) {
			source.setEmail(user.getEmail());
			u.setEmail(user.getEmail());
		}
		if (hasText(user.getCellPhone())) {
			source.setCellPhone(user.getCellPhone());
			identityService.setUserInfo(id.toString(), "cellPhone", user.getCellPhone());
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
		if (admin.getId().equals(id)) {
			throw new ForbiddenException("Can not delete administrator account");
		}
		String _id = id.toString();
		identityService.createGroupQuery().groupMember(_id).list().stream().map(Group::getId)
				.forEach(groupId -> identityService.deleteMembership(_id, groupId));
		userRepo.deleteById(id);
	}

	@Override
	public Paging<User> search(String query, Pageable pageable) {
		Page<User> page = userRepo.search(query, pageable);
		List<User> ls = page.getContent().stream().map(this::toTransient).collect(Collectors.toList());
		return new Paging<>(ls, pageable, page.getTotalElements());
	}

	@Override
	public void enable(Long id, boolean enabled) {
		if (!enabled && admin.getId().equals(id)) {
			throw new ForbiddenException("Can not disable administrator account");
		}
		User user = userRepo.findById(id).get();
		user.setEnabled(enabled);
	}

	@Override
	public void setGroups(Long id, GroupEnum... groups) {
		String _id = id.toString();
		identityService.createGroupQuery().groupMember(_id).list().stream().map(Group::getId)
				.forEach(groupId -> identityService.deleteMembership(_id, groupId));
		Arrays.stream(groups).filter(groupId -> GroupEnum.ADMIN != groupId)
				.forEach(groupId -> identityService.createMembership(_id, groupId.name()));
		// 如果是管理员则将原ADMIN组设置进去
		if (admin.getId().equals(id)) {
			identityService.createMembership(_id, GroupEnum.ADMIN.name());
		}
	}

	@Override
	public Set<GroupEnum> getGroups(Long id) {
		String _id = id.toString();
		return identityService.createGroupQuery().groupMember(_id).list().stream().map(Group::getId)
				.map(GroupEnum::valueOf).collect(Collectors.toSet());
	}

	@Override
	public Picture getUserPicture(Long id) {
		return identityService.getUserPicture(id.toString());
	}

	@Override
	public void resetPassword(Long id) {
		User u = userRepo.findById(id).get();
		String hashed = passwordEncoder.encode("123456");
		u.setPassword(hashed);
		org.activiti.engine.identity.User _u = identityService.createUserQuery().userId(id.toString()).singleResult();
		_u.setPassword(hashed);
		identityService.saveUser(_u);
		u.setLastChangeCredentials(new Date());
	}

	@Override
	public void updateMyPassword(Long id, String oldPassword, String newPassword) {
		String[] username = CURRENT_USER_INFO.get().split(SecurityConfig.SEPARATOR);
		if (!id.toString().equals(username[0])) {
			throw new ForbiddenException("{\"cause\":\"Only the account itself can change the password\"}");
		}
		User u = userRepo.findById(id).get();
		org.activiti.engine.identity.User _u = identityService.createUserQuery().userId(id.toString()).singleResult();
		String encodedPassword = u.getPassword();
		if (!passwordEncoder.matches(oldPassword, encodedPassword)) {
			throw new ForbiddenException("{\"cause\":\"The original password is incorrect\"}");
		}
		String hashedNewPassword = passwordEncoder.encode(newPassword);
		u.setPassword(hashedNewPassword);
		_u.setPassword(hashedNewPassword);
		identityService.saveUser(_u);
		u.setLastChangeCredentials(new Date());
	}
	
	@Override
	public void setUserPicture(@NotNull String userId, Picture picture) {
		identityService.setUserPicture(userId.toString(), picture);
	}
	
	@Override
	public void refreshLastLoginTime(Long id) {
		User u = userRepo.findById(id).get();
		u.setLastLogin(new Date());
	}

	@Override
	protected User toTransient(User source) {
		if (source == null) {
			return source;
		}
		User target = new User();
		BeanUtils.copyProperties(source, target, "password", "groups");
		return target;
	}

	@Override
	protected User transientDetail(User source) {
		if (source == null) {
			return null;
		}
		User target = toTransient(source);
		Set<GroupEnum> groups = identityService.createGroupQuery().groupMember(source.getId().toString()).list()
				.stream().map(Group::getId).map(GroupEnum::valueOf).collect(Collectors.toSet());
		target.getGroups().addAll(groups);
		return target;
	}

}
