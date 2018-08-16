package com.emailtohl.hjk.crm.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emailtohl.hjk.crm.entities.Flow;
import com.emailtohl.hjk.crm.flow.FlowRepo;
import com.emailtohl.hjk.crm.organization.OrganizationService;

@RestController
@RequestMapping("flow")
public class FlowCtl {
	@Autowired
	private FlowRepo flowRepo;
	@Autowired
	private OrganizationService invoiceService;
	
	/**
	 * 查询当前用户的任务
	 * 
	 * @return
	 */
	@GetMapping("todoTasks")
	public List<Flow> findTodoTasks() {
		List<Flow> all = new ArrayList<>();
		all.addAll(invoiceService.findTodoTasks());
		return all;
	}
	
	@GetMapping("flow/{id}")
	public Flow getFlow(@PathVariable("id") Long id) {
		Flow flow = flowRepo.findById(id).get();
		return flow.transientDetail();
	}
}
