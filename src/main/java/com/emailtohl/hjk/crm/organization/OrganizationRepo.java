package com.emailtohl.hjk.crm.organization;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emailtohl.hjk.crm.entities.Organization;

public interface OrganizationRepo extends JpaRepository<Organization, Long>, OrganizationRepoCust {
	
}
