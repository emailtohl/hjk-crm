package com.emailtohl.hjk.crm.controller;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.emailtohl.hjk.crm.entities.BinFile;
import com.emailtohl.hjk.crm.entities.Flow;
import com.emailtohl.hjk.crm.entities.Invoice;
import com.emailtohl.hjk.crm.invoice.InvoiceService;
import com.github.emailtohl.lib.jpa.AuditedRepository.Tuple;
import com.github.emailtohl.lib.jpa.BaseEntity;
import com.github.emailtohl.lib.jpa.Paging;

/**
 * 发票信息控制接口
 * 
 * @author HeLei
 */
@RestController
@RequestMapping(value = "invoices", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class InvoiceCtl {
	private static final Logger LOG = LogManager.getLogger();
	private InvoiceService invoiceService;

	/**
	 * 创建发票资料
	 * 
	 * @param invoice
	 * @return
	 */
	@PostMapping
	public Invoice create(@RequestBody Invoice invoice) {
		return invoiceService.create(invoice);
	}

	/**
	 * 读取发票资料
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("{id}")
	public Invoice read(@PathVariable("id") Long id) {
		return invoiceService.read(id);
	}

	/**
	 * 根据发票资料的id查询其凭证
	 * 
	 * @param invoiceId
	 * @return
	 */
	@GetMapping("{invoiceId}/credentials")
	public Set<BinFile> getCredentials(@PathVariable("invoiceId") Long invoiceId) {
		return invoiceService.getCredentials(invoiceId);
	}

	/**
	 * 查询发票资料
	 * 
	 * @param query
	 * @param pageable
	 * @return
	 */
	@GetMapping("query")
	public Paging<Invoice> query(@RequestParam(required = false, defaultValue = "") String query,
			@PageableDefault(page = 0, size = 20, sort = { BaseEntity.ID_PROPERTY_NAME,
					BaseEntity.MODIFY_DATE_PROPERTY_NAME }, direction = Direction.DESC) Pageable pageable) {
		return invoiceService.query(query, pageable);
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
		LOG.debug("claim: {}" + f);
		return invoiceService.claim(f.taskId);
	}

	/**
	 * 审核任务
	 * 
	 * @param taskId
	 * @param checkApproved
	 * @param checkComment
	 */
	@PostMapping("check")
	public void check(@RequestBody Form f) {
		invoiceService.check(f.taskId, f.checkApproved, f.checkComment);
	}

	/**
	 * 修改开票资料
	 * @param id
	 * @param invoice
	 * @return
	 */
	@PutMapping("{id}")
	public Invoice update(@PathVariable("id") Long id, @RequestBody Invoice invoice) {
		return invoiceService.update(id, invoice);
	}
	
	/**
	 * 获取历史版本列表
	 * @param id
	 * @return
	 */
	@GetMapping("history/{id}")
	public List<Tuple<Invoice>> getRevisions(@PathVariable("id") Long id) {
		return invoiceService.getRevisions(id);
	}
	
	/**
	 * 获取某修订版详情
	 * @param id
	 * @param revision
	 * @return
	 */
	@GetMapping("history/{id}/revision/{revision}")
	public Invoice getEntityAtRevision(@PathVariable("id") Long id, @PathVariable("revision") Number revision) {
		return invoiceService.getEntityAtRevision(id, revision);
	}

	public static class Form {
		public Long id;
		public String taskId;
		public Boolean reApply;
		public Invoice invoice;
		public Boolean checkApproved;
		public String checkComment;
	}
}
