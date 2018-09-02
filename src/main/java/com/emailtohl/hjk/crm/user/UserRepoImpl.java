package com.emailtohl.hjk.crm.user;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.emailtohl.hjk.crm.entities.User;
import com.github.emailtohl.lib.jpa.SearchRepository;

@Repository
class UserRepoImpl extends SearchRepository<User, Long> implements UserRepoCust {

	@Override
	public boolean emailOrCellPhoneExist(String emailOrCellPhone) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Boolean> q = cb.createQuery(Boolean.class);
		Root<User> r = q.from(entityClass);
		q = q.select(cb.greaterThan(cb.count(r), 0L)).where(
				cb.or(cb.equal(r.get("email"), emailOrCellPhone), cb.equal(r.get("cellPhone"), emailOrCellPhone)));
		return entityManager.createQuery(q).getSingleResult();
	}

	@Override
	public User byEmailOrCellPhone(String emailOrCellPhone) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<User> q = cb.createQuery(entityClass);
		Root<User> r = q.from(entityClass);
		q = q.select(r).where(
				cb.or(cb.equal(r.get("email"), emailOrCellPhone), cb.equal(r.get("cellPhone"), emailOrCellPhone)));
		User u = null;
		try {
			u = entityManager.createQuery(q).getSingleResult();
		} catch (NoResultException e) {}
		return u;
	}

}
