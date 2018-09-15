package com.emailtohl.hjk.crm.config;

import static com.emailtohl.hjk.crm.entities.GroupEnum.ADMIN;
import static com.emailtohl.hjk.crm.entities.GroupEnum.ADMINISTRATION;
import static com.emailtohl.hjk.crm.entities.GroupEnum.FINANCE;
import static com.emailtohl.hjk.crm.entities.GroupEnum.FOREIGN;
import static com.emailtohl.hjk.crm.entities.GroupEnum.MARKET;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.POST;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
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

import com.emailtohl.hjk.crm.entities.User;
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
	
	private final String[] EMPLOYEE_GROUPS = { ADMIN.name(), FINANCE.name(), ADMINISTRATION.name(), MARKET.name(),
			FOREIGN.name() };

	@Override
	public void configure(WebSecurity security) {
		security.ignoring()
				.antMatchers("/*.html", "/*.css", "/*.js", "/lib/*.js", "/*.png", "/*.gif", "/*.jpg", "/favicon.ico",
						"/resources/**", "/swagger-resources/**", "/api-docs/**", "/v2/api-docs/**", "/swagger-ui.html",
						"/webjars/**")
				.requestMatchers(CorsUtils::isPreFlightRequest);
	}

	@Override
	protected void configure(HttpSecurity security) throws Exception {
		String[] permitAll = { "/csrf", "/token", "/groups", "/users/isEmailExist", "/users/isCellPhoneExist",
				"/users/emailOrCellPhoneExist", "/users/login" };
		security
		.authorizeRequests()
		.antMatchers(permitAll).permitAll()
		.antMatchers(POST, "/users").permitAll()
		.antMatchers("/organization/history/**").hasAnyAuthority(EMPLOYEE_GROUPS)
		.antMatchers("/organization/export").hasAnyAuthority(EMPLOYEE_GROUPS)
		.antMatchers("/invoice/export").hasAnyAuthority(EMPLOYEE_GROUPS)
		.antMatchers(DELETE, "/users/").hasAnyAuthority(ADMIN.name())
		.antMatchers(POST, "/users/enable").hasAnyAuthority(ADMIN.name())
		.antMatchers(POST, "/users/*/groups").hasAnyAuthority(ADMIN.name())
		.antMatchers(POST, "/users/resetPassword").hasAnyAuthority(ADMIN.name())
		.anyRequest().authenticated()
		.and().formLogin().usernameParameter("emailOrCellPhone").permitAll()
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
		auth.userDetailsService(emailOrCellPhone -> {// email是必填项，用作登录，但若是手机号也可以查询
			User user = userService.byEmailOrCellPhone(emailOrCellPhone);
			if (user == null) {
				throw new UsernameNotFoundException(emailOrCellPhone);
			}
			List<String> groupIds = identityService.createGroupQuery().groupMember(user.getId().toString()).list()
					.stream().map(Group::getId).collect(Collectors.toList());
			return new org.springframework.security.core.userdetails.User(
					user.getId() + SEPARATOR + user.getName() + SEPARATOR + String.join(AUTHORITY_SEPARATOR, groupIds),
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
