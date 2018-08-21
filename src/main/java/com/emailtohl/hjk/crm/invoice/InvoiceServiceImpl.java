package com.emailtohl.hjk.crm.invoice;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.activiti.engine.ActivitiTaskAlreadyClaimedException;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emailtohl.hjk.crm.config.SecurityConfig;
import com.emailtohl.hjk.crm.entities.Check;
import com.emailtohl.hjk.crm.entities.Flow;
import com.emailtohl.hjk.crm.entities.FlowType;
import com.emailtohl.hjk.crm.entities.Invoice;
import com.emailtohl.hjk.crm.entities.Organization;
import com.emailtohl.hjk.crm.flow.FlowRepo;
import com.emailtohl.hjk.crm.organization.OrganizationRepo;
import com.github.emailtohl.lib.StandardService;
import com.github.emailtohl.lib.exception.ForbiddenException;
import com.github.emailtohl.lib.exception.InnerDataStateException;
import com.github.emailtohl.lib.exception.InvalidDataException;
import com.github.emailtohl.lib.exception.NotAcceptableException;
import com.github.emailtohl.lib.exception.NotFoundException;
import com.github.emailtohl.lib.jpa.Paging;

@Service
@Transactional
public class InvoiceServiceImpl extends StandardService<Invoice, Long> implements InvoiceService {
	public final static String PROCESS_DEFINITION_KEY = "invoice";
	@Autowired
	private InvoiceRepo invoiceRepo;
	@Autowired
	private FlowRepo flowRepo;
	@Autowired
	private OrganizationRepo organizationRepo;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private IdentityService identityService;

	@Override
	public Invoice create(Invoice invoice) {
		validate(invoice);
		Long organizationId = invoice.getOrganization().getId();
		if (organizationId == null) {
			throw new InvalidDataException("not exist id of organization");
		}
		Organization organization = organizationRepo.findById(organizationId).get();
		if (organization.getPass() != null && !organization.getPass()) {
			throw new NotAcceptableException("The organization of " + organization.getName() + " is not accept");
		}
		invoice.setOrganization(organization);
		invoiceRepo.persist(invoice);
		String[] username = CURRENT_USER_INFO.get().split(SecurityConfig.SEPARATOR);
		String businessKey = invoice.getId().toString();
		// 关联流程
		Flow fd = new Flow(businessKey, FlowType.INVOICE, username[0]);
		fd.setApplyUserName(username[1]);

		// 填写点流程的传输信息
		Map<String, Object> variables = new HashMap<>();
		variables.put("businessKey", businessKey);
		variables.put("flowType", FlowType.INVOICE);
		variables.put("applyUserId", username[0]);
		variables.put("applyUserName", username[1]);
		variables.put("flowNum", fd.getFlowNum());
		variables.put("InvoiceType", invoice.getType());
		variables.put("organizationId", organization.getId());
		variables.put("organizationName", organization.getName());
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, businessKey,
				variables);
		String processInstanceId = processInstance.getId();
		LOG.debug("start process of {key={}, bkey={}, pid={}}", PROCESS_DEFINITION_KEY, businessKey, processInstanceId);
		fd.setProcessInstanceId(processInstanceId);
//		cascade = CascadeType.ALL 所以由Hibernate级联保存
		invoice.setFlow(fd);
		return transientDetail(invoice);
	}

	/**
	 * 审核，并完善信息
	 * 
	 * @param taskId
	 * @param checkApproved
	 * @param checkComment
	 * @param supplement
	 */
	public void check(String taskId, boolean checkApproved, String checkComment, Invoice supplement) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			throw new NotFoundException("taskId: " + taskId + " not found");
		}
		Invoice invoice = invoiceRepo.findByFlowProcessInstanceId(task.getProcessInstanceId());
		if (invoice == null) {
			throw new NotFoundException("The invoice entity was not found through taskId " + taskId);
		}
		Flow flow = flowRepo.findByProcessInstanceId(task.getProcessInstanceId());
		if (flow == null) {
			throw new InnerDataStateException(
					"not found flow entity by processDefinitionId: " + task.getProcessDefinitionId());
		}
		String[] username = CURRENT_USER_INFO.get().split(SecurityConfig.SEPARATOR);
		String userId = username[0];
		if (!userId.equals(task.getAssignee())) {
			throw new ForbiddenException(username[1] + " are not the executor of the task");
		}
		Map<String, Object> variables = new HashMap<>();
		switch (task.getTaskDefinitionKey()) {
		case "finance_handle":
			Double income = supplement.getIncome();
			Date receiveTime = supplement.getReceiveTime();
			Double ticketfee = supplement.getTicketfee();
			Double tax = supplement.getTax();
			Double deduct = supplement.getDeduct();
			String detail = supplement.getDetail();
			if (checkApproved && (income == null || receiveTime == null || ticketfee == null)) {
				throw new InvalidDataException("Missing amount received or time of receipt or amount invoiced");
			}
			invoice.setIncome(income);
			invoice.setReceiveTime(receiveTime);
			invoice.setTicketfee(ticketfee);
			invoice.setTax(tax);
			invoice.setDeduct(deduct);
			invoice.setDetail(detail);
			variables.put("income", income);
			variables.put("receiveTime", receiveTime);
			variables.put("ticketfee", ticketfee);
			variables.put("tax", tax);
			variables.put("deduct", deduct);
			variables.put("detail", detail);
			variables.put("checkApproved", checkApproved);
			variables.put("checkComment", checkComment);
			break;
		case "foreign_handle":
			Date ticketTime = supplement.getTicketTime();
			String content = supplement.getContent();
			String invoiceNumber = supplement.getInvoiceNumber();
			Date expressTime = supplement.getExpressTime();
			String expressCompany = supplement.getExpressCompany();
			String expressNumber = supplement.getExpressNumber();
			Double expressFee = supplement.getExpressFee();
			Double paymentOn = supplement.getPaymentOn();
			if (checkApproved && (invoiceNumber == null || content == null)) {
				throw new InvalidDataException("Missing invoice number and invoice content");
			}
			invoice.setTicketTime(ticketTime);
			invoice.setContent(content);
			invoice.setInvoiceNumber(invoiceNumber);
			invoice.setExpressTime(expressTime);
			invoice.setExpressCompany(expressCompany);
			invoice.setExpressNumber(expressNumber);
			invoice.setExpressFee(expressFee);
			invoice.setPaymentOn(paymentOn);
			variables.put("ticketTime", ticketTime);
			variables.put("content", content);
			variables.put("invoiceNumber", invoiceNumber);
			variables.put("expressTime", expressTime);
			variables.put("expressCompany", expressCompany);
			variables.put("expressNumber", expressNumber);
			variables.put("expressFee", expressFee);
			variables.put("paymentOn", paymentOn);
			variables.put("paymentOn", paymentOn);
			variables.put("checkApproved", checkApproved);
			variables.put("checkComment", checkComment);
			break;
		default:
			return;
		}
		String _checkComment = checkApproved && !hasText(checkComment) ? "已处理" : checkComment;
		// 维护相关数据
		Check check = new Check(userId, checkApproved, _checkComment, task);
		org.activiti.engine.identity.User u = identityService.createUserQuery().userId(check.getCheckerId())
				.singleResult();
		if (u != null) {
			check.setCheckerName(u.getFirstName());
		}
		flow.getChecks().add(check);
		// 将审批的评论添加进记录中
		taskService.addComment(taskId, task.getProcessInstanceId(), _checkComment);
		taskService.complete(taskId, variables);
	}

	@Override
	public Invoice read(Long id) {
		return transientDetail(invoiceRepo.findById(id).get());
	}

	@Override
	public Invoice findByFlowProcessInstanceId(String processInstanceId) {
		return transientDetail(invoiceRepo.findByFlowProcessInstanceId(processInstanceId));
	}

	@Override
	public Paging<Invoice> query(Invoice example, Pageable pageable) {
		Page<Invoice> page = invoiceRepo.queryForPage(example, pageable);
		List<Invoice> ls = page.getContent().stream().map(this::toTransient).collect(Collectors.toList());
		return new Paging<>(ls, pageable, page.getTotalElements());
	}
	
	@Override
	public Paging<Invoice> search(String query, Pageable pageable) {
		Page<Invoice> page;
		if (hasText(query)) {
			page = invoiceRepo.search(query, pageable);
		} else {
			page = invoiceRepo.findAll(pageable);
		}
		List<Invoice> ls = page.getContent().stream().map(this::toTransient).collect(Collectors.toList());
		return new Paging<>(ls, pageable, page.getTotalElements());
	}

	@Override
	public List<Invoice> query(Invoice example) {
		return invoiceRepo.queryForList(example).stream().map(this::toTransient).collect(Collectors.toList());
	}

	@Override
	public List<Invoice> myApply() {
		String[] username = CURRENT_USER_INFO.get().split(SecurityConfig.SEPARATOR);
		String userId = username[0];
		return invoiceRepo.findByFlowApplyUserId(userId).stream().map(this::toTransient).collect(Collectors.toList());
	}

	@Override
	public Invoice update(Long id, Invoice newInvoice) {
		Invoice target = invoiceRepo.findById(id).get();
		// 开票本身就是一个流程，所以不允许更改关联的公司信息以及流程信息
		BeanUtils.copyProperties(newInvoice, target, Invoice.getIgnoreProperties("organization", "flow"));
		return transientDetail(target);
	}

	@Override
	public void delete(Long id) {
		invoiceRepo.deleteById(id);
		ProcessInstance p = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(id.toString(), PROCESS_DEFINITION_KEY).singleResult();
		if (p != null) {
			String[] username = CURRENT_USER_INFO.get().split(SecurityConfig.SEPARATOR);
			runtimeService.deleteProcessInstance(p.getId(), "delete by " + username);
		}
	}
	

	/**
	 * 查询当前用户的任务
	 * 
	 * @return
	 */
	@Override
	public List<Flow> findTodoTasks() {
		String[] username = CURRENT_USER_INFO.get().split(SecurityConfig.SEPARATOR);
		String userId = username[0];
		List<Task> tasks = new ArrayList<>();
		// 根据当前人的ID查询
		List<Task> todoList = taskService.createTaskQuery().processDefinitionKey(PROCESS_DEFINITION_KEY)
				.taskAssignee(userId).list();
		// 根据当前人未签收的任务
		List<Task> unsignedTasks = taskService.createTaskQuery().processDefinitionKey(PROCESS_DEFINITION_KEY)
				.taskCandidateUser(userId).list();
		// 合并
		tasks.addAll(todoList);
		tasks.addAll(unsignedTasks);
		// 根据流程的业务ID查询实体并关联
		return tasks.stream().map(task -> {
			Flow flow = flowRepo.findByProcessInstanceId(task.getProcessInstanceId());
			flow = flow.toTransient();
			flow.taskInfo(task);
			return flow;
		}).collect(Collectors.toList());
	}

	/**
	 * 签收任务
	 * 
	 * @param taskId
	 * @return
	 */
	@Override
	public Invoice claim(String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			throw new NotFoundException("taskId: " + taskId + " not found");
		}
		String[] username = CURRENT_USER_INFO.get().split(SecurityConfig.SEPARATOR);
		String userId = username[0];
		try {
			taskService.claim(taskId, userId);
		} catch (ActivitiTaskAlreadyClaimedException e) {
			throw new NotAcceptableException("Activiti task already claimed exception", e);
		}
		return transientDetail(invoiceRepo.findByFlowProcessInstanceId(task.getProcessInstanceId()));
	}

	@Override
	protected Invoice toTransient(Invoice source) {
		if (source == null) {
			return source;
		}
		Invoice target = new Invoice();
		BeanUtils.copyProperties(source, target, "organization", "flow");
		Organization targetOrganization = new Organization();
		BeanUtils.copyProperties(source.getOrganization(), targetOrganization, "credentials", "flows");
		target.setFlow(source.getFlow().toTransient());
		target.setOrganization(targetOrganization);
		return target;
	}

	@Override
	protected Invoice transientDetail(Invoice source) {
		if (source == null) {
			return source;
		}
		Invoice target = toTransient(source);
		Flow flow = source.getFlow().transientDetail();
		appendTaskInfo(flow);
		target.setFlow(flow);
		return target;
	}
	
	private void appendTaskInfo(Flow flow) {
		Task task = taskService.createTaskQuery().processInstanceId(flow.getProcessInstanceId()).singleResult();
		if (task == null) {
			return;
		}
		String taskAssignee = task.getAssignee();
		if (hasText(taskAssignee)) {
			flow.setTaskAssignee(taskAssignee);
			org.activiti.engine.identity.User u = identityService.createUserQuery().userId(taskAssignee).singleResult();
			if (u != null) {
				flow.setTaskAssigneeName(u.getFirstName());
			}
		}
		flow.taskInfo(task);
	}
	
}
