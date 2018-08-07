package com.emailtohl.hjk.crm.invoice;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.emailtohl.hjk.crm.entities.Flow;
import com.emailtohl.hjk.crm.entities.BinFile;
import com.emailtohl.hjk.crm.entities.Invoice;
import com.github.emailtohl.lib.jpa.Paging;

/**
 * 发票资料管理接口
 * @author HeLei
 */
public interface InvoiceService {
	
	/**
	 * 保存凭证信息
	 * @param credentials
	 * @return 所保存的id
	 */
	List<Long> saveCredentials(BinFile... credentials);
	/**
	 * 创建发票资料
	 * @param invoice
	 * @return
	 */
	Invoice create(Invoice invoice);
	
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
	Set<BinFile> getCredentials(Long invoiceId);
	
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
	 * @return
	 */
	Invoice update(Long id, Invoice invoice);
	
	/**
	 * 删除发票资料
	 * @param id
	 */
	void delete(Long id);
	
	/**
	 * 查询当前用户的任务
	 * @return
	 */
	List<Flow> findTodoTasks();
	
	/**
	 * 签收任务
	 * @param taskId
	 * @return
	 */
	Invoice claim(String taskId);
	
	/**
	 * 审核任务，包括申请人重提申请或放弃申请
	 * @param taskId
	 * @param checkApproved
	 * @param checkComment
	 */
	void check(String taskId, boolean checkApproved, String checkComment);

}
