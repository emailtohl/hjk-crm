package com.emailtohl.hjk.crm.invoice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emailtohl.hjk.crm.entities.Invoice;

public interface InvoiceRepo extends JpaRepository<Invoice, Long>, InvoiceRepoCust {
	
	Invoice findByFlowProcessInstanceId(String processInstanceId);
	
	List<Invoice> findByFlowApplyUserId(String applyUserId);
}
