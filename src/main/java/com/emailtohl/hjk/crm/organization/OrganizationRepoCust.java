package com.emailtohl.hjk.crm.organization;

import java.util.List;

import com.emailtohl.hjk.crm.entities.Organization;
import com.github.emailtohl.lib.jpa.AuditedInterface;

interface OrganizationRepoCust extends AuditedInterface<Organization, Long> {
	/**
	 * 从流程实例中查公司信息
	 * @param processInstanceId
	 * @return
	 */
	Organization getByProcessInstanceId(String processInstanceId);
	
	/**
	 * 根据申请人id查询公司信息列表
	 * @param applyUserId
	 * @return
	 */
	List<Organization> getByApplyUserId(String applyUserId);
	
	/**
	 * 找出所有关联文件的id，以便于清空失去关联的文件
	 * @return
	 */
	List<Long> allAssociatedIds();
}
