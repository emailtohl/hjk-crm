package com.emailtohl.hjk.crm.organization;

import java.util.List;

import com.emailtohl.hjk.crm.entities.Organization;
import com.github.emailtohl.lib.jpa.AuditedInterface;

interface OrganizationRepoCust extends AuditedInterface<Organization, Long> {
	/**
	 * 从流程实例中查组织信息
	 * @param processInstanceId
	 * @return
	 */
	Organization getByProcessInstanceId(String processInstanceId);
	
	/**
	 * 根据申请人id查询组织信息列表
	 * @param applyUserId
	 * @return
	 */
	List<Organization> getByApplyUserId(String applyUserId);
	
	/**
	 * 查询出所有与此人有关的组织信息
	 * @param stakeholderId
	 * @return
	 */
	List<Organization> getBystakeholderId(Long stakeholderId);
	
	/**
	 * 找出所有关联文件的id，以便于清空失去关联的文件
	 * @return
	 */
	List<Long> allAssociatedIds();
}
