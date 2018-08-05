package com.emailtohl.hjk.crm.controller;

import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.emailtohl.hjk.crm.entities.Image;
import com.emailtohl.hjk.crm.entities.Invoice;
import com.emailtohl.hjk.crm.invoice.InvoiceService;
import com.github.emailtohl.lib.jpa.BaseEntity;
import com.github.emailtohl.lib.jpa.Paging;
/**
 * 发票信息控制接口
 * @author HeLei
 */
@RestController
@RequestMapping(value = "invoices", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class InvoiceCtl {
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
	 * 是否审核通过
	 * 
	 * @param id
	 * @param approve
	 */
	@PostMapping("approve")
	public void approve(@RequestBody Form form) {
		invoiceService.approve(form.id, form.approve);
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
	public Set<Image> getCredentials(@PathVariable("invoiceId") Long invoiceId) {
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
	 * 修改发票资料
	 * 
	 * @param id
	 * @param invoice
	 * @return
	 */
	@PutMapping("{id}")
	public Invoice update(@PathVariable("id") Long id, @RequestBody Invoice invoice) {
		return invoiceService.update(id, invoice);
	}

	/**
	 * 删除发票资料
	 * 
	 * @param id
	 */
	@DeleteMapping("{id}")
	public void delete(@PathVariable("id") Long id) {
		invoiceService.delete(id);
	}

	public static class Form {
		public Long id;
		public Boolean approve;
	}
}
