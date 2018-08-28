package com.emailtohl.hjk.crm.flow;

import java.util.Date;
import java.util.Locale;

import org.activiti.engine.IdentityService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class NotifyListener implements TaskListener {
	private static final long serialVersionUID = 212783939230473817L;
	@Autowired
	private SimpMessagingTemplate template;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private ApplicationContext ctx;
	
	@Override
	public void notify(DelegateTask delegateTask) {
		IdentityService identityService = ctx.getBean(IdentityService.class);
		delegateTask.getCandidates().forEach(id -> {
			if (StringUtils.hasText(id.getGroupId())) {
				identityService.createUserQuery().memberOfGroup(id.getGroupId()).list().forEach(u -> {
					Locale locale = LocaleContextHolder.getLocale();
					template.convertAndSend("/topic/task/" + u.getId(), messageSource.getMessage("you_have_a_new_task",
							new Object[] { new Date(), delegateTask.getName() }, locale));
				});
			}
		});
	}
}
