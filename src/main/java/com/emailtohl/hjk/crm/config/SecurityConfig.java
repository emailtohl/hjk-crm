package com.emailtohl.hjk.crm.config;

import java.util.List;
import java.util.stream.Collectors;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
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
/**
 * 安全层配置
 * @author HeLei
 */
@Configuration
@Import({ BeansConfig.class })
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private IdentityService identityService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void configure(WebSecurity security) {
		String[] ignoring = {"/favicon.ico", "/resources/**"};
		// 告诉Spring Security需要忽略的路径
		security
		.ignoring().antMatchers(ignoring)
		// CorsUtils::isCorsRequest开启后，将不对访问做身份校验
		.requestMatchers(CorsUtils::isPreFlightRequest/* , CorsUtils::isCorsRequest */);
	}

	@Override
	protected void configure(HttpSecurity security) throws Exception {
		security
		.authorizeRequests()
		.antMatchers("/csrf").permitAll()
		.antMatchers("/afterLogin").authenticated()
		.antMatchers(HttpMethod.GET).permitAll()
		.anyRequest().authenticated()
		.and().formLogin()
		.and().logout().logoutSuccessUrl("/login")
		.and().csrf().ignoringAntMatchers("/topic", "/queue", "/socket")
        // allow same origin to frame our site to support iframe SockJS
        .and().headers().frameOptions().sameOrigin()
		;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(username -> {
			User user = identityService.createUserQuery().userId(username).singleResult();
			if (user == null) {
				throw new UsernameNotFoundException(username);
			}
			List<String> roles = identityService.createGroupQuery().groupMember(username).list().stream()
					.map(group -> group.getId()).collect(Collectors.toList());
			return new org.springframework.security.core.userdetails.User(user.getId(), user.getPassword(),
					AuthorityUtils.createAuthorityList(roles.toArray(new String[roles.size()])));
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
