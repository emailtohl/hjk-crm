package com.emailtohl.hjk.crm.organization;

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
import com.emailtohl.hjk.crm.entities.Organization;
import com.emailtohl.hjk.crm.entities.User;
import com.emailtohl.hjk.crm.organization.OrganizationService;
import com.github.emailtohl.lib.StandardService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrganizationServiceImplTest {
	@Autowired
	@Qualifier("Lily")
	private User Lily;
	@Autowired
	@Qualifier("Lisa")
	private User Lisa;
	@Autowired
	private IdentityService identityService;
	@Autowired
	private OrganizationService organizationService;
	private Long id;

	@Before
	public void setUp() throws Exception {
		changeUser(Lily);
		Organization organization = new Organization();
		organization.setName("浙江基恒康门业有限公司");
		organization.setTaxNumber("91330702790992808Q");
		organization.setAddress("金华市婺城新城区临江工业园区星康路99号");
		organization.setTelephone("0579-82212987");
		organization.setDepositBank("中国银行金磐支行");
		organization.setAccount("362358336605");
		organization.setPrincipal("周先生");
		organization.setPrincipalPhone("15657964888");
		organization.setDeliveryAddress("金华市婺城新城区临江工业园区星康路99号");
		organization.setRemark("新");
		organization.setReceiver("徐一峰");
		organization = organizationService.create(organization);
		id = organization.getId();
	}

	@After
	public void tearDown() throws Exception {
		organizationService.delete(id);
	}

	@Test
	public void testFlow() {
		changeUser(Lisa);
		List<Flow> flows = organizationService.findTodoTasks();
		assertFalse(flows.isEmpty());
		Flow flow = flows.get(0);
		Organization organization = organizationService.claim(flow.getTaskId());
		assertNotNull(organization);
		System.out.println("阅读开票信息，然后进行审批：\n" + organization);
		organizationService.check(flow.getTaskId(), false, "修改备注信息");
		
		changeUser(Lily);
		flows = organizationService.findTodoTasks();
		assertFalse(flows.isEmpty());
		flow = flows.get(0);
		organization = organizationService.findByFlowProcessInstanceId(flow.getProcessInstanceId());
		assertNotNull(organization);
		organization.setRemark("这是新提交的");
		organization = organizationService.update(id, organization);
//		若是提交人修改了内容，自动为其处理流程
//		organizationService.check(flows.get(0).getTaskId(), true, "已修改");
		
		changeUser(Lisa);
		flows = organizationService.findTodoTasks();
		assertFalse(flows.isEmpty());
		flow = flows.get(0);
		organization = organizationService.claim(flow.getTaskId());
		System.out.println("再次阅读开票信息，然后进行审批：\n" + organization);
		organizationService.check(flow.getTaskId(), true, "可以了");
		
		organization = organizationService.read(id);
		assertTrue(organization.getPass());
	}
	
	private void changeUser(User user) {
		String userId = user.getId().toString();
		identityService.setAuthenticatedUserId(userId);
		StandardService.USER_ID.set(userId + SecurityConfig.SEPARATOR + user.getName());
	}

}
