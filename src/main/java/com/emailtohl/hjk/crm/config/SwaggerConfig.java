package com.emailtohl.hjk.crm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 在http://localhost:8080/swagger-ui.html中查看API文档
 * @author HeLei
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig {
	/**
	 * UI页面显示信息
	 */
	private final String SWAGGER2_API_BASEPACKAGE = "com.emailtohl.hjk.crm.controller";
	private final String SWAGGER2_API_TITLE = "HJK-CRM-API";
	private final String SWAGGER2_API_DESCRIPTION = "com.emailtohl.hjk.crm.controller";
	private final String SWAGGER2_API_VERSION = "1.0";

	/**
	 * createRestApi
	 *
	 * @return
	 */
	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
				.apis(RequestHandlerSelectors.basePackage(SWAGGER2_API_BASEPACKAGE)).paths(PathSelectors.any()).build();
	}

	/**
	 * apiInfo
	 * 
	 * @return
	 */
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title(SWAGGER2_API_TITLE).description(SWAGGER2_API_DESCRIPTION)
				.version(SWAGGER2_API_VERSION).build();
	}
}
