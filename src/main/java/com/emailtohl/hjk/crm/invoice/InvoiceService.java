package com.emailtohl.hjk.crm.invoice;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.emailtohl.hjk.crm.entities.Flow;
import com.emailtohl.hjk.crm.entities.Invoice;
import com.github.emailtohl.lib.jpa.Paging;

public interface InvoiceService {

	Invoice create(Invoice invoice);

	/**
	 * 审核，并完善信息
	 * 
	 * @param taskId
	 * @param checkApproved
	 * @param supplement
	 */
	void check(String taskId, boolean checkApproved, Invoice supplement);

	Invoice read(Long id);

	Invoice findByFlowProcessInstanceId(String processInstanceId);

	Paging<Invoice> query(Invoice example, Pageable pageable);

	Paging<Invoice> search(String query, Pageable pageable);
	
	List<Invoice> query(Invoice example);
	/**
	 * 客户端查询自己申请的发票信息
	 * @return
	 */
	List<Invoice> myApply();

	Invoice update(Long id, Invoice newInvoice);

	void delete(Long id);

	List<Flow> findTodoTasks();

	Invoice claim(String taskId);
	
}
