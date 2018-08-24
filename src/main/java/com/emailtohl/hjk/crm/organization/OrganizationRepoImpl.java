package com.emailtohl.hjk.crm.organization;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.emailtohl.hjk.crm.entities.BinFile;
import com.emailtohl.hjk.crm.entities.Flow;
import com.emailtohl.hjk.crm.entities.Organization;
import com.emailtohl.hjk.crm.entities.User;
import com.github.emailtohl.lib.jpa.AuditedRepository;

@Repository
class OrganizationRepoImpl extends AuditedRepository<Organization, Long> implements OrganizationRepoCust {

	@Override
	public Organization getByProcessInstanceId(String processInstanceId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Organization> q = cb.createQuery(entityClass);
		Root<Organization> r = q.from(entityClass);
		Join<Organization, Flow> join = r.join("flows");
		q = q.select(r).where(cb.equal(join.get("processInstanceId"), processInstanceId));
		return entityManager.createQuery(q).getSingleResult();
	}

	@Override
	public List<Organization> getByApplyUserId(String applyUserId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Organization> q = cb.createQuery(entityClass);
		Root<Organization> r = q.from(entityClass);
		Join<Organization, Flow> join = r.join("flows");
		q = q.select(r).where(cb.equal(join.get("applyUserId"), applyUserId));
		Order o = cb.desc(r.get(Organization.MODIFY_DATE_PROPERTY_NAME));
		q = q.orderBy(o);
		return entityManager.createQuery(q).getResultList();
	}

	@Override
	public List<Organization> getBystakeholderId(Long stakeholderId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Organization> q = cb.createQuery(entityClass);
		Root<Organization> r = q.from(entityClass);
		Join<Organization, User> join = r.join("stakeholders", JoinType.LEFT);
		q = q.distinct(true).select(r).where(cb.or(
			cb.equal(r.<String>get("creatorId"), stakeholderId.toString()),
			cb.equal(join.<Long>get("id"), stakeholderId)
		));
		Order o = cb.desc(r.get(Organization.MODIFY_DATE_PROPERTY_NAME));
		q = q.orderBy(o);
		return entityManager.createQuery(q).getResultList();
	}

	@Override
	public List<Long> allAssociatedIds() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(idClass);
		Root<Organization> r = q.from(entityClass);
		Join<Organization, BinFile> join = r.join("credentials");
		q = q.select(join.get("id"));
		return entityManager.createQuery(q).getResultList();
	}

}
