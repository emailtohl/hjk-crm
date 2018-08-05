package com.emailtohl.hjk.crm.config;

import static com.google.common.collect.Lists.newArrayList;

import java.time.LocalDate;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {
	@Bean
	public Docket petApi() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.emailtohl.hjk.crm.controller"))
				.paths(PathSelectors.any()).build().apiInfo(apiInfo()).pathMapping("/")
				.directModelSubstitute(LocalDate.class, String.class).genericModelSubstitutes(ResponseEntity.class)
				.securitySchemes(newArrayList(apiKey())).securityContexts(newArrayList(securityContext()))
				.enableUrlTemplating(false);
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Api Documentation").description("Api Documentation")
				.contact(new Contact("hl", "https://github.com/emailtohl", "emailtohl@163.com")).version("1.0").build();
	}

	private ApiKey apiKey() {
		// 用于Swagger UI测试时添加Bear Token
		return new ApiKey("Bear", "Authorization", "header");
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.regex("/api/.*")) // 注意要与Restful
				.build();
	}

	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return newArrayList(new SecurityReference("BearerToken", authorizationScopes));
	}
}
