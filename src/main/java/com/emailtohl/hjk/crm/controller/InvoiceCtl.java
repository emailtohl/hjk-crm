package com.emailtohl.hjk.crm.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.emailtohl.hjk.crm.entities.Flow;
import com.emailtohl.hjk.crm.entities.Invoice;
import com.emailtohl.hjk.crm.invoice.InvoiceService;
import com.github.emailtohl.lib.jpa.BaseEntity;
import com.github.emailtohl.lib.jpa.Paging;

/**
 * 开票流程管理
 * 
 * @author HeLei
 */
@RestController
@RequestMapping(value = "invoice", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class InvoiceCtl {
	private static final Logger LOG = LogManager.getLogger();
	@Autowired
	private InvoiceService invoiceService;
	
	/**
	 * 发起开票流程
	 * @param invoice
	 * @return
	 */
	@PostMapping("start")
	public Invoice start(@RequestBody Invoice invoice) {
		return invoiceService.create(invoice);
	}
	
	@GetMapping("search")
	public Paging<Invoice> search(@RequestParam(required = false, defaultValue = "") String query,
			@PageableDefault(page = 0, size = 20, sort = { BaseEntity.ID_PROPERTY_NAME,
					BaseEntity.MODIFY_DATE_PROPERTY_NAME }, direction = Direction.DESC) Pageable pageable) {
		return invoiceService.search(query, pageable);
	}
	
	@GetMapping("myApply")
	public List<Invoice> myApply() {
		return invoiceService.myApply();
	}
	
	@GetMapping("get/{id}")
	public Invoice get(@PathVariable("id") Long id) {
		return invoiceService.read(id);
	}
	
	/**
	 * 根据流程实例id读取发票信息
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("processInstanceId/{processInstanceId}")
	public Invoice readByProcessInstanceId(@PathVariable("processInstanceId") String processInstanceId) {
		return invoiceService.findByFlowProcessInstanceId(processInstanceId);
	}
	

	/**
	 * 查询当前用户的任务
	 * 
	 * @return
	 */
	@GetMapping("todoTasks")
	public List<Flow> findTodoTasks() {
		return invoiceService.findTodoTasks();
	}
	

	/**
	 * 签收任务
	 * 
	 * @param taskId
	 * @return
	 */
	@PostMapping("claim")
	public Invoice claim(@RequestBody Form f) {
		LOG.debug("claim: {}" + f.taskId);
		return invoiceService.claim(f.taskId);
	}

	/**
	 * 审核任务
	 * 
	 * @param taskId
	 * @param checkApproved
	 * @param invoice
	 */
	@PostMapping("check")
	public void check(@RequestBody Form f) {
		invoiceService.check(f.taskId, f.checkApproved, f);
	}
	
	public class Form extends Invoice {
		private static final long serialVersionUID = -2069783159467498322L;
		public String taskId;
		public Boolean checkApproved;
	}
}
