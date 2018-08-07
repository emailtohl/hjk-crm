package com.emailtohl.hjk.crm.invoice;

import java.io.Serializable;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.emailtohl.hjk.crm.entities.Invoice;

public class CompleteListener implements ExecutionListener, Serializable {
	private static final long serialVersionUID = 977519826235583920L;
	private InvoiceRepo invoiceRepo;

	public CompleteListener(InvoiceRepo invoiceRepo) {
		this.invoiceRepo = invoiceRepo;
	}

	@Transactional
	@Override
	public void notify(DelegateExecution execution) {
		Invoice invoice = invoiceRepo.findByFlowProcessInstanceId(execution.getProcessInstanceId());
		boolean result = (boolean) execution.getVariable("result");
		invoice.setPass(result);
	}

}
