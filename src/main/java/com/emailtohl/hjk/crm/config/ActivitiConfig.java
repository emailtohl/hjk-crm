package com.emailtohl.hjk.crm.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.AbstractFormType;
import org.activiti.engine.impl.form.StringFormType;
import org.activiti.rest.common.application.DefaultContentTypeResolver;
import org.activiti.rest.service.api.RestResponseFactory;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import com.emailtohl.hjk.crm.flow.CompleteListener;
import com.emailtohl.hjk.crm.flow.NotifyListener;

/**
 * 流程配置
 * 
 * @author HeLei
 */
@Configuration
@ComponentScan(basePackages = { "org.activiti.rest", "org.activiti.conf" })
public class ActivitiConfig {
	
	@Bean
	public SpringProcessEngineConfiguration processEngineConfiguration(DataSource dataSource,
			PlatformTransactionManager platformTransactionManager, EntityManagerFactory jpaEntityManagerFactory,
			Environment env, CompleteListener completeListener, NotifyListener notifyListener) {
		SpringProcessEngineConfiguration cfg = new SpringProcessEngineConfiguration();
		cfg.setDataSource(dataSource);
		cfg.setTransactionManager(platformTransactionManager);
		cfg.setJpaEntityManagerFactory(jpaEntityManagerFactory);
		cfg.setJpaHandleTransaction(false);
		cfg.setJpaCloseEntityManager(false);
		cfg.setCustomFormTypes(Arrays.asList(new BigtextFormType(), new DoubleFormType(), new JavascriptFormType()));
		cfg.setDeploymentResources(new Resource[] {
			new ClassPathResource("processes/invoice.bpmn"),
			new ClassPathResource("processes/organization.bpmn"),
			new ClassPathResource("processes/leave.bpmn"),
		});
		cfg.setActivityFontName("宋体");
		cfg.setLabelFontName("宋体");
		
		Map<Object, Object> beans = new HashMap<>();
		beans.put("completeListener", completeListener);
		beans.put("notifyListener", notifyListener);
		cfg.setBeans(beans);
		
		cfg.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		String hbm2ddl_auto = env.getProperty("spring.jpa.properties.hibernate.hbm2ddl.auto", "update");
		if (ProcessEngineConfiguration.DB_SCHEMA_UPDATE_CREATE_DROP.equalsIgnoreCase(hbm2ddl_auto)) {
			cfg.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_CREATE_DROP);
		}
		return cfg;
	}

	@Bean
	public ProcessEngine processEngine(SpringProcessEngineConfiguration config) throws Exception {
		ProcessEngineFactoryBean factory = new ProcessEngineFactoryBean();
		factory.setProcessEngineConfiguration(config);
		return factory.getObject();
	}

	/**
	 * 提供了管理和控制发布包和流程定义的操作
	 * 
	 * @param engine
	 * @return
	 */
	@Bean
	public RepositoryService repositoryService(ProcessEngine engine) {
		return engine.getRepositoryService();
	}

	/**
	 * 负责启动一个流程定义的新实例
	 * 
	 * @param engine
	 * @return
	 */
	@Bean
	public RuntimeService runtimeService(ProcessEngine engine) {
		return engine.getRuntimeService();
	}

	/**
	 * 任务是由系统中真实人员执行的，它是Activiti这类BPMN引擎的核心功能之一。 所有与任务有关的功能都包含在TaskService中
	 * 
	 * @param engine
	 * @return
	 */
	@Bean
	public TaskService taskService(ProcessEngine engine) {
		return engine.getTaskService();
	}

	/**
	 * 提供了Activiti引擎手机的所有历史数据
	 * 
	 * @param engine
	 * @return
	 */
	@Bean
	public HistoryService historyService(ProcessEngine engine) {
		return engine.getHistoryService();
	}

	/**
	 * 管理（创建，更新，删除，查询...）群组和用户 Activiti执行时并没有对用户进行检查，引擎不会校验系统中是否存在这个用户
	 * 
	 * @param engine
	 * @return
	 */
	@Bean
	public IdentityService identityService(ProcessEngine engine) {
		return engine.getIdentityService();
	}

	/**
	 * 可选服务 提供了启动表单和任务表单两个概念。 启动表单会在流程实例启动之前展示给用户， 任务表单会在用户完成任务时展示。
	 * 
	 * @param engine
	 * @return
	 */
	@Bean
	public FormService formService(ProcessEngine engine) {
		return engine.getFormService();
	}

	/**
	 * 可以查询数据库的表和表的元数据
	 * 
	 * @param engine
	 * @return
	 */
	@Bean
	public ManagementService managementService(ProcessEngine engine) {
		return engine.getManagementService();
	}
	
	/**
	 * 集成REST服务需要的bean
	 * 
	 * @return
	 */
	@Bean
	public RestResponseFactory restResponseFactory() {
		return new RestResponseFactory();
	}

	@Bean
	public DefaultContentTypeResolver contentTypeResolver() {
		return new DefaultContentTypeResolver();
	}
}

class BigtextFormType extends StringFormType {
	private static final long serialVersionUID = -7591690640370103699L;

	@Override
	public String getName() {
		return "bigtext";
	}
}

class DoubleFormType extends AbstractFormType {
	private static final long serialVersionUID = 3233712710206227594L;

	@Override
	public String getName() {
		return "double";
	}

	@Override
	public Object convertFormValueToModelValue(String propertyValue) {
		return new Double(propertyValue);
	}

	@Override
	public String convertModelValueToFormValue(Object modelValue) {
		return String.valueOf(modelValue);
	}

}

class JavascriptFormType extends AbstractFormType {
	private static final long serialVersionUID = 7576462007106547698L;

	@Override
	public String getName() {
		return "javascript";
	}

	@Override
	public Object convertFormValueToModelValue(String propertyValue) {
		return propertyValue;
	}

	@Override
	public String convertModelValueToFormValue(Object modelValue) {
		return (String) modelValue;
	}
}
