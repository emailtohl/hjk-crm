package com.emailtohl.hjk.crm.config;

import static com.emailtohl.hjk.crm.entities.GroupEnum.ADMIN;
import static com.emailtohl.hjk.crm.entities.GroupEnum.ADMINISTRATION;
import static com.emailtohl.hjk.crm.entities.GroupEnum.FINANCE;
import static com.emailtohl.hjk.crm.entities.GroupEnum.FOREIGN;
import static com.emailtohl.hjk.crm.entities.GroupEnum.MARKET;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsUtils;

import com.emailtohl.hjk.crm.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * 安全层配置
 * @author HeLei
 */
@Configuration
@Import({ BeansConfig.class })
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	public static final String SEPARATOR = ":";
	public static final String AUTHORITY_SEPARATOR = ",";
	@Autowired
	private IdentityService identityService;
	@Autowired
	private UserService userService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private ObjectMapper om;

	@Override
	public void configure(WebSecurity security) {
		String[] ignoring = {"/favicon.ico", "/resources/**"};
		security
		.ignoring().antMatchers(ignoring)
		.requestMatchers(CorsUtils::isPreFlightRequest);
	}

	@Override
	protected void configure(HttpSecurity security) throws Exception {
		String[] permitAll = { "/csrf", "/token", "/groups", "/users/isEmailExist", "/users/isCellPhoneExist",
				"/swagger-resources/**", "/api-docs/**" };
		security
		.authorizeRequests()
		.antMatchers(permitAll).permitAll()
		.antMatchers("/back/**").hasAnyAuthority(ADMIN.name(), FINANCE.name(), ADMINISTRATION.name(), MARKET.name(), FOREIGN.name())
		.antMatchers("/back/users").hasAnyAuthority(ADMIN.name())
		.anyRequest().authenticated()
		.and().formLogin().usernameParameter("email").permitAll()
			.successHandler((req, resp, auth) -> {
				String username = auth.getName();
				Long id = Long.valueOf(username.split(SEPARATOR)[0]);
				userService.refreshLastLoginTime(id);
				resp.getWriter().write(om.writeValueAsString(auth));
			})
			.failureHandler((req, resp, auth) -> resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, auth.getMessage()))
		.and().rememberMe()
		.and().logout().permitAll()
			.logoutSuccessHandler((req, resp, auth) -> resp.getWriter().write("{\"success\":true}"))
//			.logoutSuccessUrl("/login")
		.and().exceptionHandling().authenticationEntryPoint((req, resp, auth) -> resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, auth.getMessage()))
		.and().csrf().ignoringAntMatchers("/topic", "/queue", "/socket")
        // allow same origin to frame our site to support iframe SockJS
        .and().headers().frameOptions().sameOrigin()
		;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(email -> {// email是必填项，用作登录
			User user = identityService.createUserQuery().userEmail(email).singleResult();
			if (user == null) {
				throw new UsernameNotFoundException(email);
			}
			List<String> groupIds = identityService.createGroupQuery().groupMember(user.getId()).list().stream()
					.map(Group::getId).collect(Collectors.toList());
			return new org.springframework.security.core.userdetails.User(
					user.getId() + SEPARATOR + user.getFirstName() + SEPARATOR
							+ String.join(AUTHORITY_SEPARATOR, groupIds),
					user.getPassword(),
					AuthorityUtils.createAuthorityList(groupIds.toArray(new String[groupIds.size()])));
		}).passwordEncoder(passwordEncoder);
	}
	
	/**
	 * 暴露认证管理器供Oauth2使用
	 */
	@Bean
	@Override
	public AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}
}
