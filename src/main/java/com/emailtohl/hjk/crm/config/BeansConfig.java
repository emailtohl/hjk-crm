package com.emailtohl.hjk.crm.config;

import java.security.SecureRandom;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class BeansConfig {
	@Bean
	public ObjectMapper ObjectMapper() {
		return new ObjectMapper();
	}

	@Bean
	public CacheManager concurrentMapCacheManager() {
		return new ConcurrentMapCacheManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10, new SecureRandom());
	}

}
