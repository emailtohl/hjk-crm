package com.emailtohl.hjk.crm.controller;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.emailtohl.hjk.crm.entities.BinFile;
import com.emailtohl.hjk.crm.entities.Flow;
import com.emailtohl.hjk.crm.entities.Invoice;
import com.emailtohl.hjk.crm.invoice.InvoiceService;
import com.github.emailtohl.lib.jpa.BaseEntity;
import com.github.emailtohl.lib.jpa.Paging;
/**
 * 发票信息控制接口
 * @author HeLei
 */
@RestController
@RequestMapping(value = "invoices", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class InvoiceCtl {
	private static final Logger LOG = LogManager.getLogger();
	private InvoiceService invoiceService;
/*
	@PostMapping(value = "binFile")
	public List<BinFile> addBinFile(HttpServletRequest request) {
		List<BinFile> res = new ArrayList<>();

		Collection<Part> fileParts = null;
		Map<String, String[]> map = request.getParameterMap();
		try {
			fileParts = request.getParts();
		} catch (IOException | ServletException e) {
			LOG.error("不能获取文件part", e);
			return res;
		}
		for (Iterator<Part> iterable = fileParts.iterator(); iterable.hasNext();) {
			Part filePart = iterable.next();
			String submittedFileName = filePart.getSubmittedFileName();
			if (submittedFileName != null && !map.containsKey(submittedFileName)) {
				try (InputStream in = filePart.getInputStream();
						ByteArrayOutputStream out = new ByteArrayOutputStream()) {
					StreamUtils.copy(in, out);
					BinFile BinFile = new BinFile(submittedFileName, FILE_NAME_MAP.getContentTypeFor(submittedFileName),
							out.toByteArray());
					BinFile = fileService.saveBinFile(BinFile);
					res.add(BinFile);
				} catch (IOException e) {
					LOG.error(submittedFileName + " 文件读取失败", e);
				}
			}
		}
		return res;
	}

	@GetMapping(value = "binFile/{id}")
	public void downloadBinFile(@PathVariable("id") Long id, HttpServletResponse response) {
		BinFile BinFile = fileService.findById(id);
		String filename;
		try {
			filename = URLEncoder.encode(BinFile.getFilename(), "UTF-8");
			response.setHeader("content-disposition", "attachment;fileName=" + filename);
		} catch (UnsupportedEncodingException e) {
			LOG.error(BinFile.getFilename() + "编码为UTF-8失败");
		}
		response.setContentType(BinFile.getMimeType());
		try (ServletOutputStream out = response.getOutputStream()) {
			out.write(BinFile.getBin());
		} catch (IOException e) {
			LOG.error(BinFile.getFilename() + " 文件读取失败", e);
		}
	}*/
	/**
	 * 创建发票资料
	 * 
	 * @param invoice
	 * @return
	 */
	@PostMapping
	public Invoice create(@RequestBody Invoice invoice) {
		return invoiceService.create(invoice);
	}

	/**
	 * 读取发票资料
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("{id}")
	public Invoice read(@PathVariable("id") Long id) {
		return invoiceService.read(id);
	}

	/**
	 * 根据发票资料的id查询其凭证
	 * 
	 * @param invoiceId
	 * @return
	 */
	@GetMapping("{invoiceId}/credentials")
	public Set<BinFile> getCredentials(@PathVariable("invoiceId") Long invoiceId) {
		return invoiceService.getCredentials(invoiceId);
	}

	/**
	 * 查询发票资料
	 * 
	 * @param query
	 * @param pageable
	 * @return
	 */
	@GetMapping("query")
	public Paging<Invoice> query(@RequestParam(required = false, defaultValue = "") String query,
			@PageableDefault(page = 0, size = 20, sort = { BaseEntity.ID_PROPERTY_NAME,
					BaseEntity.MODIFY_DATE_PROPERTY_NAME }, direction = Direction.DESC) Pageable pageable) {
		return invoiceService.query(query, pageable);
	}

	/**
	 * 查询当前用户的任务
	 * @return
	 */
	@GetMapping("todoTasks")
	public List<Flow> findTodoTasks() {
		return invoiceService.findTodoTasks();
	}
	
	/**
	 * 签收任务
	 * @param taskId
	 * @return
	 */
	@PostMapping("claim")
	public Invoice claim(@RequestBody Form f) {
		return invoiceService.claim(f.taskId);
	}
	
	/**
	 * 审核任务
	 * @param taskId
	 * @param checkApproved
	 * @param checkComment
	 */
	@PostMapping("check")
	public void check(@RequestBody Form f) {
		invoiceService.check(f.taskId, f.checkApproved, f.checkComment);
	}

	/**
	 * 重新申请
	 * @param taskId
	 * @param reApply
	 * @param invoice
	 * @return
	 */
	@PostMapping("reApply")
	public Invoice reApply(@RequestBody Form f) {
		return invoiceService.reApply(f.taskId, f.reApply, f.invoice);
	}
	
	public static class Form {
		public Long id;
		public String taskId;
		public Boolean reApply;
		public Invoice invoice;
		public Boolean checkApproved;
		public String checkComment;
	}
}
