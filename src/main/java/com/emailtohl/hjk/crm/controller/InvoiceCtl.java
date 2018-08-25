package com.emailtohl.hjk.crm.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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

import com.emailtohl.hjk.crm.entities.Flow;
import com.emailtohl.hjk.crm.entities.Invoice;
import com.emailtohl.hjk.crm.entities.InvoiceType;
import com.emailtohl.hjk.crm.invoice.InvoiceService;
import com.github.emailtohl.lib.jpa.BaseEntity;
import com.github.emailtohl.lib.jpa.Paging;

/**
 * 开票流程管理
 * 
 * @author HeLei
 */
@RestController
@RequestMapping(value = "invoice", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class InvoiceCtl {
	private static final Logger LOG = LogManager.getLogger();
	@Autowired
	private InvoiceService invoiceService;
	
	/**
	 * 发起开票流程
	 * @param invoice
	 * @return
	 */
	@PostMapping("start")
	public Invoice start(@RequestBody Invoice invoice) {
		return invoiceService.create(invoice);
	}
	
	@GetMapping("search")
	public Paging<Invoice> search(@RequestParam(required = false, defaultValue = "") String query,
			@PageableDefault(page = 0, size = 10, sort = { BaseEntity.ID_PROPERTY_NAME,
					BaseEntity.MODIFY_DATE_PROPERTY_NAME }, direction = Direction.DESC) Pageable pageable) {
		return invoiceService.search(query, pageable);
	}
	
	@GetMapping("myApply")
	public List<Invoice> myApply() {
		return invoiceService.myApply();
	}
	
	@GetMapping("get/{id}")
	public Invoice get(@PathVariable("id") Long id) {
		return invoiceService.read(id);
	}
	
	/**
	 * 根据流程实例id读取发票信息
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("processInstanceId/{processInstanceId}")
	public Invoice readByProcessInstanceId(@PathVariable("processInstanceId") String processInstanceId) {
		return invoiceService.findByFlowProcessInstanceId(processInstanceId);
	}
	

	/**
	 * 查询当前用户的任务
	 * 
	 * @return
	 */
	@GetMapping("todoTasks")
	public List<Flow> findTodoTasks() {
		return invoiceService.findTodoTasks();
	}
	

	/**
	 * 签收任务
	 * 
	 * @param taskId
	 * @return
	 */
	@PostMapping("claim")
	public Invoice claim(@RequestParam(value = "taskId", required = true) String taskId) {
		LOG.debug("claim: {}" + taskId);
		return invoiceService.claim(taskId);
	}

	/**
	 * 审核任务
	 * 
	 * @param taskId
	 * @param checkApproved
	 * @param f
	 */
	@PostMapping("check")
	public void check(@RequestParam(value = "taskId", required = true) String taskId,
			@RequestParam(value = "checkApproved", required = true) Boolean checkApproved,
			@RequestParam(value = "checkComment", required = false, defaultValue = "") String checkComment,
			@RequestBody Invoice f) {
		invoiceService.check(taskId, checkApproved, checkComment, f);
	}
	
	/**
	 * 将所有开票信息导出成Excel文件
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@GetMapping("export")
	public void exportExcel(HttpServletResponse response) throws FileNotFoundException, IOException {
		List<Invoice> ls = invoiceService.query(null).stream().filter(invoice -> {
			Boolean pass = invoice.getFlow().getPass();
			if (pass != null && !pass)
				return false;
			else
				return true;
		}).collect(Collectors.toList());
		ClassPathResource r = new ClassPathResource("excel/invoice_template.xlsx");
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		try (InputStream in = r.getInputStream();
				XSSFWorkbook workbook = new XSSFWorkbook(in);
				ServletOutputStream out = response.getOutputStream()) {
			XSSFSheet sheet = workbook.getSheetAt(0);
			int i = 4;
			for (Invoice o : ls) {
				XSSFRow row = sheet.getRow(i);
				if (row == null) {
					row = sheet.createRow(i);
				}
				XSSFCell cell = row.createCell(0);
				cell.setCellValue(i - 3);
				cell = row.createCell(1);
				cell.setCellValue(o.getType() == InvoiceType.ORDINARY ? "普票" : o.getType() == InvoiceType.SPECIAL ? "专票" : o.getType().name());
				cell = row.createCell(2);
				cell.setCellValue(format(o.getOrganization().getName()));
				cell = row.createCell(3);
				cell.setCellValue(format(o.getReceiveTime()));
				cell = row.createCell(4);
				cell.setCellValue(format(o.getIncome()));
				cell = row.createCell(5);
				cell.setCellValue(format(o.getDeduct()));
				cell = row.createCell(6);
				cell.setCellValue(format(o.getTicketfee()));
				cell = row.createCell(7);
				cell.setCellValue(format(o.getDetail()));
				cell = row.createCell(8);
				cell.setCellValue(format(o.getTicketTime()));
				cell = row.createCell(9);
				cell.setCellValue(format(o.getContent()));
				cell = row.createCell(10);
				cell.setCellValue(format(o.getInvoiceNumber()));
				cell = row.createCell(12);
				cell.setCellValue(format(o.getPaymentOn()));
				cell = row.createCell(13);
				cell.setCellValue(format(o.getExpressTime()));
				cell = row.createCell(14);
				cell.setCellValue(format(o.getExpressNumber()));
				cell = row.createCell(15);
				cell.setCellValue(format(o.getExpressCompany()));
				cell = row.createCell(16);
				cell.setCellValue(format(o.getExpressFee()));
				cell = row.createCell(17);
				cell.setCellValue(format(o.getRemark()));
				i++;
			}
			workbook.write(out);
		}
	}
	
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	private String format(Object o) {
		if (o == null) {
			return "";
		}
		if (o instanceof String) {
			return (String) o;
		}
		if (o instanceof Date) {
			return sdf.format(o);
		}
		if (o instanceof Double) {
			String s = o.toString();
			if (s.contains(".")) {
				return s.split("\\.")[0];
			} else {
				return s;
			}
		}
		return o.toString();
	}
}
