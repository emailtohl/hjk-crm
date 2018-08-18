package com.emailtohl.hjk.crm.controller;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.emailtohl.hjk.crm.entities.Organization;
import com.emailtohl.hjk.crm.organization.OrganizationService;
import com.github.emailtohl.lib.jpa.AuditedRepository.Tuple;
import com.github.emailtohl.lib.jpa.BaseEntity;
import com.github.emailtohl.lib.jpa.Paging;

/**
 * 发票信息控制接口
 * 
 * @author HeLei
 */
@RestController
@RequestMapping(value = "organization", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class OrganizationCtl {
	private static final Logger LOG = LogManager.getLogger();
	@Autowired
	private OrganizationService organizationService;

	/**
	 * 检查该纳税人识别号是否存在
	 * @param taxNumber
	 * @return
	 */
	@GetMapping("isTaxNumberExist")
	public boolean isTaxNumberExist(@RequestParam(required = false, defaultValue = "") String taxNumber) {
		boolean reslut = false;
		if (StringUtils.hasText(taxNumber)) {
			reslut = organizationService.isTaxNumberExist(taxNumber);
		}
		LOG.debug(" taxNumber exist {} ", reslut);
		return reslut;
	}
	
	/**
	 * 检查该账户是否存在
	 * @param account
	 * @return
	 */
	@GetMapping("isAccountExist")
	public boolean isAccountExist(@RequestParam(required = false, defaultValue = "") String account) {
		boolean reslut = false;
		if (StringUtils.hasText(account)) {
			reslut = organizationService.isAccountExist(account);
		}
		LOG.debug(" account exist {} ", reslut);
		return reslut;
	}
	
	/**
	 * 创建公司信息
	 * 
	 * @param organization
	 * @return
	 */
	@PostMapping
	public Organization create(@RequestBody Organization organization) {
		return organizationService.create(organization);
	}

	/**
	 * 读取公司信息
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("{id}")
	public Organization read(@PathVariable("id") Long id) {
		return organizationService.read(id);
	}
	
	/**
	 * 读取公司信息
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("processInstanceId/{processInstanceId}")
	public Organization readByProcessInstanceId(@PathVariable("processInstanceId") String processInstanceId) {
		return organizationService.findByFlowProcessInstanceId(processInstanceId);
	}

	/**
	 * 根据公司信息的id查询其凭证
	 * 
	 * @param organizationId
	 * @return
	 */
	@GetMapping("{organizationId}/credentials")
	public Set<BinFile> getCredentials(@PathVariable("organizationId") Long organizationId) {
		return organizationService.getCredentials(organizationId);
	}

	/**
	 * 查询公司信息
	 * 
	 * @param query
	 * @param pageable
	 * @return
	 */
	@GetMapping("search")
	public Paging<Organization> search(@RequestParam(required = false, defaultValue = "") String query,
			@PageableDefault(page = 0, size = 20, sort = { BaseEntity.ID_PROPERTY_NAME,
					BaseEntity.MODIFY_DATE_PROPERTY_NAME }, direction = Direction.DESC) Pageable pageable) {
		return organizationService.query(query, pageable);
	}

	/**
	 * 查询当前用户的任务
	 * 
	 * @return
	 */
	@GetMapping("todoTasks")
	public List<Flow> findTodoTasks() {
		return organizationService.findTodoTasks();
	}

	/**
	 * 签收任务
	 * 
	 * @param taskId
	 * @return
	 */
	@PostMapping("claim")
	public Organization claim(@RequestBody Form f) {
		LOG.debug("claim: {}" + f);
		return organizationService.claim(f.taskId);
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
		organizationService.check(f.taskId, f.checkApproved, f.checkComment);
	}

	/**
	 * 修改开票资料
	 * @param id
	 * @param organization
	 * @return
	 */
	@PutMapping("{id}")
	public Organization update(@PathVariable("id") Long id, @RequestBody Organization organization) {
		return organizationService.update(id, organization);
	}
	
	/**
	 * 删除公司信息
	 * @param id
	 */
	@DeleteMapping("{id}")
	public void delete(@PathVariable("id") Long id) {
		organizationService.delete(id);
	}
	
	/**
	 * 获取历史版本列表
	 * @param id
	 * @return
	 */
	@GetMapping("history/{id}")
	public List<Tuple<Organization>> getRevisions(@PathVariable("id") Long id) {
		return organizationService.getRevisions(id);
	}
	
	/**
	 * 获取某修订版详情
	 * @param id
	 * @param revision
	 * @return
	 */
	@GetMapping("history/{id}/revision/{revision}")
	public Organization getEntityAtRevision(@PathVariable("id") Long id, @PathVariable("revision") Number revision) {
		return organizationService.getEntityAtRevision(id, revision);
	}
	
	/**
	 * 客户查询自己申请的组织信息
	 * @return
	 */
	@GetMapping("myRegisterOrganization")
	public List<Organization> myRegisterOrganization() {
		return organizationService.myRegisterOrganization();
	}

	public static class Form {
		public Long id;
		public String taskId;
		public Boolean reApply;
		public Organization organization;
		public Boolean checkApproved;
		public String checkComment;
	}
}
