package com.emailtohl.hjk.crm.organization;

import org.springframework.stereotype.Repository;

import com.emailtohl.hjk.crm.entities.Organization;
import com.github.emailtohl.lib.jpa.AuditedRepository;

@Repository
class OrganizationRepoImpl extends AuditedRepository<Organization, Long> implements OrganizationRepoCust {

}
