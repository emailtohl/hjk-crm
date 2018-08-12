package com.emailtohl.hjk.crm.invoice;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import com.emailtohl.hjk.crm.entities.User;
import com.github.emailtohl.lib.StandardService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InvoiceServiceImplTest {
	@Autowired
	@Qualifier("troungSon")
	private User troungSon;
	@Autowired
	@Qualifier("lisa")
	private User lisa;
	@Autowired
	private IdentityService identityService;
	@Autowired
	private InvoiceService invoiceService;
	private Long id;

	@Before
	public void setUp() throws Exception {
		changeUser(troungSon);
		Invoice invoice = new Invoice();
		invoice.setOrganization("浙江基恒康门业有限公司");
		invoice.setTaxNumber("91330702790992808Q");
		invoice.setOrganizationAddress("金华市婺城新城区临江工业园区星康路99号");
		invoice.setTelephone("0579-82212987");
		invoice.setDepositBank("中国银行金磐支行");
		invoice.setAccount("362358336605");
		invoice.setPrincipal("周先生");
		invoice.setPrincipalPhone("15657964888");
		invoice.setDeliveryAddress("金华市婺城新城区临江工业园区星康路99号");
		invoice.setRemark("新");
		invoice.setReceiver("徐一峰");
		invoice = invoiceService.create(invoice);
		id = invoice.getId();
		
	}

	@After
	public void tearDown() throws Exception {
		invoiceService.delete(id);
	}

	@Test
	public void testFlow() {
		changeUser(lisa);
		List<Flow> flows = invoiceService.findTodoTasks();
		assertFalse(flows.isEmpty());
		Flow flow = flows.get(0);
		Invoice invoice = invoiceService.claim(flow.getTaskId());
		assertNotNull(invoice);
		System.out.println("阅读开票信息，然后进行审批：\n" + invoice);
		invoiceService.check(flow.getTaskId(), false, "修改备注信息");
		
		changeUser(troungSon);
		flows = invoiceService.findTodoTasks();
		assertFalse(flows.isEmpty());
		flow = flows.get(0);
		invoice = invoiceService.findByFlowProcessInstanceId(flow.getProcessInstanceId());
		assertNotNull(invoice);
		invoice.setRemark("这是新提交的");
		invoice = invoiceService.update(id, invoice);
		invoiceService.check(flows.get(0).getTaskId(), true, "已修改");
		
		changeUser(lisa);
		flows = invoiceService.findTodoTasks();
		assertFalse(flows.isEmpty());
		flow = flows.get(0);
		invoice = invoiceService.claim(flow.getTaskId());
		System.out.println("再次阅读开票信息，然后进行审批：\n" + invoice);
		invoiceService.check(flow.getTaskId(), true, "可以了");
		
		invoice = invoiceService.read(id);
		assertTrue(invoice.getPass());
	}
	
	private void changeUser(User user) {
		String userId = user.getId().toString();
		identityService.setAuthenticatedUserId(userId);
		StandardService.USER_ID.set(userId + SecurityConfig.SEPARATOR + user.getName());
	}

}
