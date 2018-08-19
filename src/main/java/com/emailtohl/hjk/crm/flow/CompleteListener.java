package com.emailtohl.hjk.crm.flow;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.emailtohl.hjk.crm.entities.Flow;
import com.emailtohl.hjk.crm.entities.FlowType;
import com.emailtohl.hjk.crm.entities.Invoice;
import com.emailtohl.hjk.crm.entities.Organization;
import com.emailtohl.hjk.crm.invoice.InvoiceRepo;
import com.emailtohl.hjk.crm.organization.OrganizationRepo;

@Component
public class CompleteListener implements ExecutionListener {
	private static final long serialVersionUID = 977519826235583920L;
	private FlowRepo flowRepo;
	private OrganizationRepo organizationRepo;
	private InvoiceRepo invoiceRepo;

	@Autowired
	public CompleteListener(FlowRepo flowRepo, OrganizationRepo organizationRepo, InvoiceRepo invoiceRepo) {
		this.flowRepo = flowRepo;
		this.organizationRepo = organizationRepo;
		this.invoiceRepo = invoiceRepo;
	}

	@Transactional
	@Override
	public void notify(DelegateExecution execution) {
		FlowType flowType = (FlowType) execution.getVariable("flowType");
		boolean result;
		switch (flowType) {
		case ORGANIZATION:
			Organization organization = organizationRepo.getByProcessInstanceId(execution.getProcessInstanceId());
			result = (boolean) execution.getVariable("result");
			organization.setPass(result);
			Flow flow = flowRepo.findByProcessInstanceId(execution.getProcessInstanceId());
			if (flow != null) {
				flow.setPass(result);
			}
			break;
		case INVOICE:
			Invoice invoice = invoiceRepo.findByFlowProcessInstanceId(execution.getProcessInstanceId());
			result = (boolean) execution.getVariable("result");
			invoice.getFlow().setPass(result);
			break;
		default:
			break;
		}
	}

}
