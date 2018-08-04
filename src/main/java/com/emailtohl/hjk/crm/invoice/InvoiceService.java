package com.emailtohl.hjk.crm.invoice;

import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.emailtohl.hjk.crm.entities.Image;
import com.emailtohl.hjk.crm.entities.Invoice;
import com.github.emailtohl.lib.jpa.Paging;

/**
 * 发票资料管理接口
 * @author HeLei
 */
public interface InvoiceService {
	/**
	 * 创建发票资料
	 * @param invoice
	 * @param userId
	 * @return
	 */
	Invoice create(Invoice invoice, Long userId);
	
	/**
	 * 是否审核通过
	 * @param id
	 * @param approve
	 * @param userId
	 */
	void approve(long id, boolean approve, Long userId);

	/**
	 * 读取发票资料
	 * @param id
	 * @return
	 */
	Invoice read(Long id);
	
	/**
	 * 根据发票资料的id查询其凭证
	 * @param invoiceId
	 * @return
	 */
	Set<Image> getCredentials(Long invoiceId);
	
	/**
	 * 查询发票资料
	 * @param query
	 * @param pageable
	 * @return
	 */
	Paging<Invoice> query(String query, Pageable pageable);
	
	/**
	 * 修改发票资料
	 * @param id
	 * @param invoice
	 * @param userId
	 * @return
	 */
	Invoice update(Long id, Invoice invoice, Long userId);
	
	/**
	 * 删除发票资料
	 * @param id
	 * @param userId
	 */
	void delete(Long id, Long userId);
}
