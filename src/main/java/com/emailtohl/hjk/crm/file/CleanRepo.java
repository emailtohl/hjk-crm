package com.emailtohl.hjk.crm.file;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.emailtohl.hjk.crm.entities.BinFile;

/**
 * 清理失去连接的图片
 * @author HeLei
 */
@Repository
public class CleanRepo {
	@PersistenceContext
	protected EntityManager em;
	
	/**
	 * 清理无效的图片
	 * @param validIds 仍然有效的id
	 */
	@Transactional
	public int removeOrphan(List<Long> validIds) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<BinFile> q = cb.createCriteriaDelete(BinFile.class);
		Root<BinFile> r = q.from(BinFile.class);
		q = q.where(cb.not(r.get("id").in(validIds)));
		return em.createQuery(q).executeUpdate();
	}
}
