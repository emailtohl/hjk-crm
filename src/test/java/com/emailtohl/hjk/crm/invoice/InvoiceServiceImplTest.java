package com.emailtohl.hjk.crm.invoice;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.activiti.engine.IdentityService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.emailtohl.hjk.crm.config.SecurityConfig;
import com.emailtohl.hjk.crm.entities.Flow;
import com.emailtohl.hjk.crm.entities.Invoice;
import com.emailtohl.hjk.crm.entities.InvoiceType;
import com.emailtohl.hjk.crm.entities.Organization;
import com.emailtohl.hjk.crm.entities.User;
import com.emailtohl.hjk.crm.organization.OrganizationService;
import com.github.emailtohl.lib.StandardService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InvoiceServiceImplTest {
	// 客户
	@Autowired
	@Qualifier("Thomas")
	private User Thomas;
	// 行政
	@Autowired
	@Qualifier("Lisa")
	private User Lisa;
	// 财务
	@Autowired
	@Qualifier("Amy")
	private User Amy;
	// 外务
	@Autowired
	@Qualifier("Andy")
	private User Andy;
	@Autowired
	private IdentityService identityService;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private InvoiceService invoiceService;
	private Long invoiceId;
	private Long organizationId;

	@Before
	public void setUp() throws Exception {
		changeUser(Thomas);
		Organization organization = new Organization();
		organization.setName("广东科杰机械自动化有限公司");
		organization.setTaxNumber("91440700768414040K");
		organization.setAddress("江门市蓬江区永盛路61号");
		organization.setTelephone("0750-3500201");
		organization.setDepositBank("中国工商银行江门市城西支行");
		organization.setAccount("2012002209024226424");
		organization.setPrincipal("李能裕");
		organization.setPrincipalPhone("18823067275");
		organization.setDeliveryAddress("江门市蓬江区永盛路61号");
		organization.setRemark("新");
		organization.setReceiver("周斌");
		organization = organizationService.create(organization);
		organizationId = organization.getId();
		changeUser(Lisa);
		organizationService.findTodoTasks().forEach(flow -> {
			String taskId = flow.getTaskId();
			organizationService.claim(taskId);
			organizationService.check(taskId, true, "agree");
		});
		
	}

	@After
	public void tearDown() throws Exception {
		invoiceService.delete(invoiceId);
		organizationService.delete(organizationId);
	}

	@Test
	public void testFlow() {
		// 客户提交开票申请
		changeUser(Thomas);
		Invoice invoice = new Invoice();
		invoice.setType(InvoiceType.SPECIAL);
		Organization organization = new Organization();
		organization.setId(organizationId);
		invoice.setOrganization(organization);
		invoice = invoiceService.create(invoice);
		invoiceId = invoice.getId();
		// 转到财务人员
		changeUser(Amy);
		List<Flow> flows = invoiceService.findTodoTasks();
		assertFalse(flows.isEmpty());
		for (Flow flow : flows) {
			String taskId = flow.getTaskId();
			invoiceService.claim(taskId);
			Invoice supplement = new Invoice();
			supplement.setIncome(27460.00);
			supplement.setReceiveTime(new Date());
			supplement.setTicketfee(27460.00);
			supplement.setTax(1165.05);
			supplement.setDeduct(100.00);
			supplement.setDetail("测试服务费/认证服务费");
			invoiceService.check(taskId, true, "", supplement);
		}
		// 切到外务人员
		changeUser(Andy);
		flows = invoiceService.findTodoTasks();
		assertFalse(flows.isEmpty());
		for (Flow flow : flows) {
			String taskId = flow.getTaskId();
			invoiceService.claim(taskId);
			Invoice supplement = new Invoice();
			supplement.setTicketTime(new Date());
			supplement.setContent("认证服务费");
			supplement.setInvoiceNumber("01501333");
			supplement.setExpressCompany("申通");
			supplement.setExpressNumber("402937529169");
			supplement.setExpressFee(50.00);
			supplement.setPaymentOn(50.00);
			supplement.setRemark("专票已申请");
			invoiceService.check(taskId, true, "", supplement);
		}
	}

	private void changeUser(User user) {
		String userId = user.getId().toString();
		identityService.setAuthenticatedUserId(userId);
		StandardService.CURRENT_USER_INFO.set(userId + SecurityConfig.SEPARATOR + user.getName() + SecurityConfig.SEPARATOR);
	}
}
