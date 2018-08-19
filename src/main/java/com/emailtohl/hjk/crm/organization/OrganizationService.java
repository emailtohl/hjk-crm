package com.emailtohl.hjk.crm.organization;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.emailtohl.hjk.crm.entities.Flow;
import com.emailtohl.hjk.crm.entities.BinFile;
import com.emailtohl.hjk.crm.entities.Organization;
import com.github.emailtohl.lib.jpa.Paging;
import com.github.emailtohl.lib.jpa.AuditedRepository.Tuple;

/**
 * 公司信息管理接口
 * @author HeLei
 */
public interface OrganizationService {
	
	/**
	 * 检查该税号是否存在
	 * @param taxNumber 税号
	 * @return
	 */
	boolean isTaxNumberExist(String taxNumber);
	
	/**
	 * 检查该账户是否存在
	 * @param account
	 * @return
	 */
	boolean isAccountExist(String account);
	
	/**
	 * 创建公司信息
	 * @param organization
	 * @return
	 */
	Organization create(Organization organization);
	
	/**
	 * 读取公司信息
	 * @param id
	 * @return
	 */
	Organization read(Long id);
	
	/**
	 * 通过流程实例id读取公司信息
	 * @param processInstanceId
	 * @return
	 */
	Organization findByFlowProcessInstanceId(String processInstanceId);
	
	/**
	 * 根据资料的id查询其凭证
	 * @param organizationId
	 * @return
	 */
	Set<BinFile> getCredentials(Long organizationId);
	
	/**
	 * 查询公司信息
	 * @param query
	 * @param pageable
	 * @return
	 */
	Paging<Organization> query(String query, Pageable pageable);
	
	/**
	 * 修改公司信息
	 * @param id
	 * @param organization
	 * @return
	 */
	Organization update(Long id, Organization organization);
	
	/**
	 * 删除公司信息
	 * @param id
	 */
	void delete(Long id);
	
	/**
	 * 查询当前用户的任务
	 * @return
	 */
	List<Flow> findTodoTasks();
	
	/**
	 * 签收任务
	 * @param taskId
	 * @return
	 */
	Organization claim(String taskId);
	
	/**
	 * 审核任务，包括申请人重提申请或放弃申请
	 * @param taskId
	 * @param checkApproved
	 * @param checkComment
	 */
	void check(String taskId, boolean checkApproved, String checkComment);

	/**
	 * 获取历史版本列表
	 * @param id
	 * @return
	 */
	List<Tuple<Organization>> getRevisions(Long id);
	
	/**
	 * 获取历史某版本的详情
	 * @param id
	 * @param revision
	 * @return
	 */
	Organization getEntityAtRevision(Long id, Number revision);
	
	/**
	 * 客户查询自己申请的组织信息
	 * @return
	 */
	List<Organization> myRegisterOrganization();

	/**
	 * 查找所有的公司信息
	 * @return
	 */
	List<Organization> findAll();
	
}
