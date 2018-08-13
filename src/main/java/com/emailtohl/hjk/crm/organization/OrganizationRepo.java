package com.emailtohl.hjk.crm.organization;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emailtohl.hjk.crm.entities.Organization;

public interface OrganizationRepo extends JpaRepository<Organization, Long>, OrganizationRepoCust {
	
	Organization findByFlow_ProcessInstanceId(String processInstanceId);
	
	List<Organization> findByFlow_ApplyUserId(String applyUserId);
	
}
