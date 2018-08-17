package com.emailtohl.hjk.crm.flow;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.emailtohl.hjk.crm.entities.FlowType;
import com.emailtohl.hjk.crm.entities.Organization;
import com.emailtohl.hjk.crm.organization.OrganizationRepo;

public class CompleteListener implements ExecutionListener {
	private static final long serialVersionUID = 977519826235583920L;
	private OrganizationRepo organizationRepo;

	public CompleteListener(OrganizationRepo invoiceRepo) {
		this.organizationRepo = invoiceRepo;
	}

	@Transactional
	@Override
	public void notify(DelegateExecution execution) {
		FlowType flowType = (FlowType) execution.getVariable("flowType");
		switch (flowType) {
		case ORGANIZATION:
			Organization organization = organizationRepo.getByProcessInstanceId(execution.getProcessInstanceId());
			boolean result = (boolean) execution.getVariable("result");
			organization.setPass(result);
			break;
		default:
			break;
		}
	}

}
