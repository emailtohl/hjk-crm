package com.emailtohl.hjk.crm.invoice;

import java.util.List;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emailtohl.hjk.crm.config.SecurityConfig;
import com.emailtohl.hjk.crm.entities.Invoice;
import com.emailtohl.hjk.crm.flow.FlowRepo;
import com.github.emailtohl.lib.StandardService;
import com.github.emailtohl.lib.jpa.Paging;

@Service
@Transactional
public class InvoiceServiceImpl extends StandardService<Invoice, Long> {
	public final static String PROCESS_DEFINITION_KEY = "invoice";
	@Autowired
	private InvoiceRepo invoiceRepo;
	@Autowired
	private FlowRepo flowRepo;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private IdentityService identityService;

	@Override
	public Invoice create(Invoice invoice) {
		validate(invoice);
		invoiceRepo.persist(invoice);
		String[] username = USER_ID.get().split(SecurityConfig.SEPARATOR);
//		organization.setCreatorId(username[0]);
		return null;
	}

	@Override
	public Invoice read(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Paging<Invoice> query(Invoice example, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Invoice> query(Invoice example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Invoice update(Long id, Invoice newInvoice) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Invoice toTransient(Invoice invoice) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Invoice transientDetail(Invoice invoice) {
		// TODO Auto-generated method stub
		return null;
	}

}
