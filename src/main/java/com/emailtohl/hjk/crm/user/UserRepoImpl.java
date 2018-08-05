package com.emailtohl.hjk.crm.user;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.emailtohl.hjk.crm.entities.User;
import com.github.emailtohl.lib.jpa.SearchRepository;

@Repository
class UserRepoImpl extends SearchRepository<User, Long> implements UserRepoCust {

	@Override
	public boolean exist(String name) {
		CriteriaBuilder b = entityManager.getCriteriaBuilder();
		CriteriaQuery<Boolean> q = b.createQuery(boolean.class);
		Root<User> r = q.from(User.class);
		q = q.select(b.greaterThan(b.count(r.<String>get("name")), 0L)).where(b.equal(r.<String>get("name"), name));
		return entityManager.createQuery(q).getSingleResult();
	}

}
