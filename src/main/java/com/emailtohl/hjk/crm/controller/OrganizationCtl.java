package com.emailtohl.hjk.crm.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.emailtohl.hjk.crm.entities.BinFile;
import com.emailtohl.hjk.crm.entities.Flow;
import com.emailtohl.hjk.crm.entities.Organization;
import com.emailtohl.hjk.crm.organization.OrganizationService;
import com.github.emailtohl.lib.jpa.AuditedRepository.RevTuple;
import com.github.emailtohl.lib.jpa.EntityBase;
import com.github.emailtohl.lib.jpa.Paging;

/**
 * 公司企业信息管理
 * 
 * @author HeLei
 */
@RestController
@RequestMapping(value = "organization", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class OrganizationCtl {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
	private final Logger LOG = LogManager.getLogger();
	@Autowired
	private OrganizationService organizationService;

	/**
	 * 检查该纳税人识别号是否存在
	 * 
	 * @param taxNumber
	 * @return
	 */
	@GetMapping("isTaxNumberExist")
	public boolean isTaxNumberExist(@RequestParam(required = false, defaultValue = "") String taxNumber) {
		boolean reslut = false;
		if (StringUtils.hasText(taxNumber)) {
			reslut = organizationService.isTaxNumberExist(taxNumber);
		}
		LOG.debug(" taxNumber exist {} ", reslut);
		return reslut;
	}

	/**
	 * 检查该账户是否存在
	 * 
	 * @param account
	 * @return
	 */
	@GetMapping("isAccountExist")
	public boolean isAccountExist(@RequestParam(required = false, defaultValue = "") String account) {
		boolean reslut = false;
		if (StringUtils.hasText(account)) {
			reslut = organizationService.isAccountExist(account);
		}
		LOG.debug(" account exist {} ", reslut);
		return reslut;
	}

	/**
	 * 创建公司信息
	 * 
	 * @param organization
	 * @return
	 */
	@PostMapping
	public Organization create(@RequestBody Organization organization) {
		return organizationService.create(organization);
	}

	/**
	 * 读取公司信息
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("{id}")
	public Organization read(@PathVariable("id") Long id) {
		return organizationService.read(id);
	}

	/**
	 * 读取公司信息
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("processInstanceId/{processInstanceId}")
	public Organization readByProcessInstanceId(@PathVariable("processInstanceId") String processInstanceId) {
		return organizationService.findByFlowProcessInstanceId(processInstanceId);
	}

	/**
	 * 根据公司信息的id查询其凭证
	 * 
	 * @param organizationId
	 * @return
	 */
	@GetMapping("{organizationId}/credentials")
	public Set<BinFile> getCredentials(@PathVariable("organizationId") Long organizationId) {
		return organizationService.getCredentials(organizationId);
	}

	/**
	 * 查询公司信息
	 * 
	 * @param query
	 * @param pageable
	 * @return
	 */
	@GetMapping("search")
	public Paging<Organization> search(@RequestParam(required = false, defaultValue = "") String query,
			@PageableDefault(page = 0, size = 10, sort = { EntityBase.ID_PROPERTY_NAME,
					EntityBase.MODIFY_TIME_PROPERTY_NAME }, direction = Direction.DESC) Pageable pageable) {
		query = query.replaceAll("是", "true");
		query = query.replaceAll("否", "false");
		return organizationService.search(query, pageable);
	}

	/**
	 * 查询当前用户的任务
	 * 
	 * @return
	 */
	@GetMapping("todoTasks")
	public List<Flow> findTodoTasks() {
		return organizationService.findTodoTasks();
	}

	/**
	 * 签收任务
	 * 
	 * @param taskId
	 * @return
	 */
	@PostMapping("claim")
	public Organization claim(@RequestBody Form f) {
		LOG.debug("claim: {}" + f);
		return organizationService.claim(f.taskId);
	}

	/**
	 * 审核任务
	 * 
	 * @param taskId
	 * @param checkApproved
	 * @param checkComment
	 */
	@PostMapping("check")
	public void check(@RequestBody Form f) {
		organizationService.check(f.taskId, f.checkApproved, f.checkComment);
	}

	/**
	 * 修改开票资料
	 * 
	 * @param id
	 * @param organization
	 * @return
	 */
	@PutMapping("{id}")
	public Organization update(@PathVariable("id") Long id, @RequestBody Organization organization) {
		return organizationService.update(id, organization);
	}

	/**
	 * 删除公司信息
	 * 
	 * @param id
	 */
	@DeleteMapping("{id}")
	public void delete(@PathVariable("id") Long id) {
		organizationService.delete(id);
	}

	/**
	 * 获取历史版本列表
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("history/{id}")
	public List<RevTuple<Organization>> getRevisions(@PathVariable("id") Long id) {
		return organizationService.getRevisions(id);
	}

	/**
	 * 获取某修订版详情
	 * 
	 * @param id
	 * @param revision
	 * @return
	 */
	@GetMapping("history/{id}/revision/{revision}")
	public Organization getEntityAtRevision(@PathVariable("id") Long id, @PathVariable("revision") Number revision) {
		return organizationService.getEntityAtRevision(id, revision);
	}

	/**
	 * 客户查询自己申请的组织信息
	 * 
	 * @return
	 */
	@GetMapping("myRegisterOrganizations")
	public List<Organization> myRegisterOrganizations() {
		return organizationService.myRegisterOrganizations();
	}

	/**
	 * 查询出所有与此人有关的组织信息
	 * 
	 * @param stakeholderId
	 * @return
	 */
	@GetMapping("myRelationshipOrganizations")
	public List<Organization> getMyRelationshipOrganizations() {
		return organizationService.getMyRelationshipOrganizations();
	}

	/**
	 * 创建组织信息与干系人的关系，以便于这些干系人都能查找到此组织信息
	 * 
	 * @param organizationId
	 * @param stakeholderIds
	 */
	@PostMapping("relationship")
	public void createRelationship(@RequestBody Form f) {
		organizationService.createRelationship(f.id, f.stakeholderIds);
	}

	/**
	 * 将所有公司信息导出成Excel文件
	 * 
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@GetMapping("export")
	public void exportExcel(HttpServletResponse response) throws FileNotFoundException, IOException {
		List<Organization> ls = organizationService.findAll();
		ClassPathResource r = new ClassPathResource("excel/organization_template.xlsx");
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		try (InputStream in = r.getInputStream();
				XSSFWorkbook workbook = new XSSFWorkbook(in);
				ServletOutputStream out = response.getOutputStream()) {
			XSSFSheet sheet = workbook.getSheetAt(0);
			int i = 3;
			for (Organization o : ls) {
				XSSFRow row = sheet.getRow(i);
				if (row == null) {
					row = sheet.createRow(i);
				}
				XSSFCell cell = row.createCell(0);
				cell.setCellValue(i - 2);
				cell = row.createCell(1);
				cell.setCellValue(o.getName());
				cell = row.createCell(2);
				cell.setCellValue(o.getTaxNumber());
				cell = row.createCell(3);
				cell.setCellValue(o.getAddress());
				cell = row.createCell(4);
				cell.setCellValue(o.getTelephone());
				cell = row.createCell(5);
				cell.setCellValue(o.getDepositBank());
				cell = row.createCell(6);
				cell.setCellValue(o.getAccount());
				cell = row.createCell(7);
				cell.setCellValue(o.getPrincipal());
				cell = row.createCell(8);
				cell.setCellValue(o.getPrincipalPhone());
				cell = row.createCell(9);
				cell.setCellValue(o.getDeliveryAddress());
				cell = row.createCell(10);
				cell.setCellValue(o.getRemark());
				cell = row.createCell(11);
				cell.setCellValue(o.getReceiver());
				i++;
			}
			workbook.write(out);
		}
	}

	/**
	 * 按照模板批量导入公司信息数据
	 * 
	 * @param excel
	 * @throws IOException
	 */
	@PostMapping("batchCreate")
	public String importExcel(@RequestParam("file") MultipartFile excel) throws IOException {
		try (InputStream in = excel.getInputStream(); XSSFWorkbook workbook = new XSSFWorkbook(in)) {
			XSSFSheet sheet = workbook.getSheetAt(0);
			int i = 3, count = 0;
			while (i < 65535) {
				XSSFRow row = sheet.getRow(i);
				if (row == null) {
					break;
				}
				Organization o = new Organization();
				o.setName(getStringValue(row.getCell(1)));
				o.setTaxNumber(getStringValue(row.getCell(2)));
				o.setAddress(getStringValue(row.getCell(3)));
				o.setTelephone(getStringValue(row.getCell(4)));
				o.setDepositBank(getStringValue(row.getCell(5)));
				o.setAccount(getStringValue(row.getCell(6)));
				o.setPrincipal(getStringValue(row.getCell(7)));
				o.setPrincipalPhone(getStringValue(row.getCell(8)));
				o.setDeliveryAddress(getStringValue(row.getCell(9)));
				o.setRemark(getStringValue(row.getCell(10)));
				o.setReceiver(getStringValue(row.getCell(11)));
				try {
					organizationService.create(o);
					count++;
				} catch (Exception e) {
					LOG.catching(e);
				}
				i++;
			}
			return String.format("{\"count\":%d}", count);
		}
	}

	/**
	 * 全部以字符串读取cell的值，若遇到异常，则捕获之，继续下一步
	 * 
	 * @param cell
	 * @return
	 */
	private String getStringValue(XSSFCell cell) {
		String result = null;
		if (cell == null) {
			return result;
		}
		try {
			result = cell.getStringCellValue();
		} catch (IllegalStateException e) {
			try {
				XSSFRichTextString rts = cell.getRichStringCellValue();
				if (rts != null) {
					result = rts.getString();
				}
			} catch (IllegalStateException e1) {
				try {
					Date d = cell.getDateCellValue();
					if (d != null) {
						result = sdf.format(d);
					}
				} catch (IllegalStateException e2) {
					try {
						result = BigDecimal.valueOf(cell.getNumericCellValue()).toString();
					} catch (IllegalStateException | NumberFormatException e3) {
						LOG.catching(e3);
					}
				}
			}
		}
		return result;
	}

	public static class Form {
		public Long id;
		public String taskId;
		public Boolean reApply;
		public Organization organization;
		public Boolean checkApproved;
		public String checkComment;
		public Set<Long> stakeholderIds;
	}
}
