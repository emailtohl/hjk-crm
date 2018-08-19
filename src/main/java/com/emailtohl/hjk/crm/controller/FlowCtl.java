package com.emailtohl.hjk.crm.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emailtohl.hjk.crm.entities.Flow;
import com.emailtohl.hjk.crm.invoice.InvoiceService;
import com.emailtohl.hjk.crm.organization.OrganizationService;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class FlowCtl {
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private InvoiceService invoiceService;
	
	/**
	 * 查询当前用户的任务
	 * 
	 * @return
	 */
	@GetMapping("todoTasks")
	public List<Flow> findTodoTasks() {
		List<Flow> all = new ArrayList<>();
		all.addAll(organizationService.findTodoTasks());
		all.addAll(invoiceService.findTodoTasks());
		return all;
	}
	
}
