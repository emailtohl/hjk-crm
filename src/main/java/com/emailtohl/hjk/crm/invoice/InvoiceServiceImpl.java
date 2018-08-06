package com.emailtohl.hjk.crm.invoice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.ActivitiTaskAlreadyClaimedException;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.emailtohl.hjk.crm.entities.Check;
import com.emailtohl.hjk.crm.entities.Flow;
import com.emailtohl.hjk.crm.entities.FlowType;
import com.emailtohl.hjk.crm.entities.Image;
import com.emailtohl.hjk.crm.entities.Invoice;
import com.emailtohl.hjk.crm.flow.FlowRepo;
import com.github.emailtohl.lib.StandardService;
import com.github.emailtohl.lib.exception.InnerDataStateException;
import com.github.emailtohl.lib.exception.NotAcceptableException;
import com.github.emailtohl.lib.exception.NotFoundException;
import com.github.emailtohl.lib.jpa.Paging;
/**
 * 发票资料管理接口的实现
 * @author HeLei
 */
@Service
@Transactional
public class InvoiceServiceImpl extends StandardService<Invoice, Long> implements InvoiceService {
	public final static String PROCESS_DEFINITION_KEY = "invoice";
	@Autowired
	private InvoiceRepo invoiceRepo;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private IdentityService identityService;
	@Autowired
	private FormService formService;
	@Autowired
	private FlowRepo flowRepo;
	
	@Override
	public Invoice create(@Valid Invoice invoice) {
		// 校验提交的表单信息
		validate(invoice);
		// 如果没有填写收票地址，那么就把公司地址设置为收票地址
		if (!hasText(invoice.getDeliveryAddress())) {
			invoice.setDeliveryAddress(invoice.getOrganizationAddress());
		}
		// 先保存发票信息，获取ID
		invoiceRepo.persist(invoice);
		
		Flow fd = new Flow();
		fd.setFlowType(FlowType.INVOICE);
		String applyUserId = USERNAME.get();
		fd.setApplyUserId(applyUserId);
		// 计算流程编号
		LocalDate d = LocalDate.now();
		int year = d.getYear(), month = d.getMonthValue(), day = d.getDayOfMonth();
		StringBuilder flowNum = new StringBuilder();
		flowNum.append(FlowType.INVOICE.name()).append('-').append(year);
		if (month < 10) {
			flowNum.append('-').append(0).append(month);
		} else {
			flowNum.append('-').append(month);
		}
		if (day < 10) {
			flowNum.append('-').append(0).append(day);
		} else {
			flowNum.append('-').append(day);
		}
		String businessKey = invoice.getId().toString();
		flowNum.append('-').append(businessKey);
		// 设置流程编号
		fd.setFlowNum(flowNum.toString());
		// 填写点流程的传输信息
		Map<String, Object> variables = new HashMap<>();
		variables.put("applyUserId", applyUserId);
		variables.put("flowNum", flowNum);
		variables.put("businessKey", businessKey);
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, businessKey, variables);
		String processInstanceId = processInstance.getId();
		fd.setProcessInstanceId(processInstanceId);
		fd.setActivityId(processInstance.getActivityId());
		Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
		if (task != null) {
			fd.setTaskId(task.getId());
			fd.setTaskName(task.getName());
		}
		LOG.debug("start process of {key={}, bkey={}, pid={}}",
				new Object[] { PROCESS_DEFINITION_KEY, businessKey, processInstanceId });
		
		flowRepo.save(fd);
		invoice.setFlow(fd);
		return invoice;
	}

	@Override
	public Invoice read(Long id) {
		Invoice source = invoiceRepo.findById(id).get();
		return transientDetail(source);
	}

	@Override
	public Paging<Invoice> query(Invoice example, Pageable pageable) {
		Page<Invoice> page = invoiceRepo.queryForPage(example, pageable);
		List<Invoice> ls = page.getContent().stream().map(this::toTransient).collect(Collectors.toList());
		return new Paging<>(ls, pageable, page.getTotalElements());
	}

	@Override
	public List<Invoice> query(Invoice example) {
		return invoiceRepo.queryForList(example).stream().map(this::toTransient).collect(Collectors.toList());
	}

	@Override
	public Invoice update(Long id, Invoice invoice) {
		Invoice source = invoiceRepo.findById(id).get();
		if (!"modifyApply".equals(source.getFlow().getActivityId())) {
			throw new NotAcceptableException("Changes cannot be submitted at this point");
		}
		if (invoice.getType() != null) {
			source.setType(invoice.getType());
		}
		if (hasText(invoice.getOrganization())) {
			source.setOrganization(invoice.getOrganization());
		}
		if (hasText(invoice.getTaxNumber())) {
			source.setTaxNumber(invoice.getTaxNumber());
		}
		if (hasText(invoice.getOrganizationAddress())) {
			source.setOrganizationAddress(invoice.getOrganizationAddress());
		}
		if (hasText(invoice.getTelephone())) {
			source.setTelephone(invoice.getTelephone());
		}
		if (hasText(invoice.getDepositBank())) {
			source.setDepositBank(invoice.getDepositBank());
		}
		if (hasText(invoice.getAccount())) {
			source.setAccount(invoice.getAccount());
		}
		if (hasText(invoice.getPrincipal())) {
			source.setPrincipal(invoice.getPrincipal());
		}
		if (hasText(invoice.getPrincipalPhone())) {
			source.setPrincipalPhone(invoice.getPrincipalPhone());
		}
		if (hasText(invoice.getDeliveryAddress())) {
			source.setDeliveryAddress(invoice.getDeliveryAddress());
		}
		return transientDetail(source);
	}

	@Override
	public void delete(Long id) {
		invoiceRepo.deleteById(id);
	}
	
	/**
	 * 查询当前用户的任务
	 * 需要参数：
	 * 已登录，可以获取到用户id
	 * @return
	 */
	public List<Flow> findTodoTasks() {
		String userId = USERNAME.get();
		List<Flow> results = new ArrayList<>();
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
		for (Task task : tasks) {
			String processInstanceId = task.getProcessInstanceId();
			ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
					.processInstanceId(processInstanceId).singleResult();
			String businessKey = processInstance.getBusinessKey();
			Optional<Flow> FlowOpt = flowRepo.findById(Long.valueOf(businessKey));
			if (!FlowOpt.isPresent()) {
				continue;
			}
			Flow Flow = FlowOpt.get();
			Flow.setTaskId(task.getId());
			Flow.setTaskName(task.getName());
			Flow.setTaskAssignee(task.getAssignee());
			Flow.setActivityId(processInstance.getActivityId());
			results.add(Flow);
		}
		return results;
	}
	
	/**
	 * 签收任务
	 * 表单需要参数：
	 * taskId：任务id
	 * 已登录，可以获取到用户id
	 * @param taskId
	 */
	public void claim(String taskId) {
		String userId = USERNAME.get();
		try {
			taskService.claim(taskId, userId);
		} catch (ActivitiTaskAlreadyClaimedException e) {
			throw new NotAcceptableException("Activiti task already claimed exception", e);
		}
	}
	
	/**
	 * 审核任务
	 * 表单需要参数：
	 * id：流程单的id或者是流程实例id（processInstanceId）
	 * taskId： 任务id
	 * 已登录，可以获取到用户id
	 * assignee：任务签收人的id
	 * checkApproved：审核是否通过
	 * checkComment： 审核意见可选
	 * @return 执行是否成功
	 */
	public void check(String taskId, boolean checkApproved, String checkComment) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			throw new NotFoundException("taskId: " + taskId + "not found");
		}
		switch (task.getTaskDefinitionKey()) {
		case "administrationAudit":
			runtimeService.setVariable(task.getExecutionId(), "checkApproved", String.valueOf(checkApproved));
			runtimeService.setVariable(task.getExecutionId(), "checkComment", checkComment);
			taskService.complete(taskId);
			// 维护相关数据
			Flow flow = flowRepo.findByTaskId(taskId);
			if (flow == null) {
				throw new InnerDataStateException("not found flow entity by taskId: " + taskId);
			}
			flow.setCheckApproved(checkApproved);
			flow.setCheckComment(checkComment);
			Check check = new Check();
			check.setActivityId(task.getTaskDefinitionKey());
			check.setCheckApproved(checkApproved);
			check.setCheckComment(checkComment);
			check.setCheckerId(USERNAME.get());
			check.setCheckTime(new Date());
			check.setTaskName(task.getName());
			flow.getChecks().add(check);
			break;
		case "recheck":
			break;
		default:
		}
		if (hasText(checkComment)) {
			// 将审批的评论添加进记录中
			taskService.addComment(taskId, task.getProcessInstanceId(), checkComment);
		}
	}

	/**
	 * 重新申请
	 * 表单reApply必填：若为true则重新申请，若为false则结束流程
	 * content：若重新申请，则content需填写
	 * @return 执行是否成功
	 *//*
	public void reApply(Flow form) {
		Flow source = getFlow(form);
		if (source == null) {
			return new ExecResult(false, "未查找到流程数据", null);
		}
		String userId = getCurrentUserId();
		if (!source.getApplicantId().equals(Long.valueOf(userId))) {
			return new ExecResult(false, "不是任务提交人", null);
		}
		String taskId = form.getTaskId();
		if (!StringUtils.hasText(taskId)) {
			return new ExecResult(false, "没有提交任务id", null);
		}
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(source.getProcessInstanceId()).singleResult();
		if (processInstance == null || !"modifyApply".equals(processInstance.getActivityId())) {
			return new ExecResult(false, "modifyApply才能修改申请内容", null);
		}
		Boolean reApply = form.getReApply();
		if (reApply == null) {
			return new ExecResult(false, "没有reApply字段", null);
		}
		Map<String, Object> variables = new HashMap<>();
		if (reApply) {
			if (!StringUtils.hasText(form.getContent())) {
				return new ExecResult(false, "更新内容不能为空", null);
			}
			source.setContent(form.getContent());
			variables.put("content", form.getContent());
			source.setReApply(reApply);
			variables.put("reApply", reApply);
		} else {
			source.setReApply(reApply);
			variables.put("reApply", reApply);
			variables.put("pass", false);
		}
		try {
			taskService.complete(taskId, variables);
		} catch (ActivitiObjectNotFoundException e) {
			return new ExecResult(false, "没查找到此id为“" + taskId +"”的任务", null);
		}
		return new ExecResult(true, "", null);
	}*/

	@Override
	public Set<Image> getCredentials(Long invoiceId) {
		Invoice source = invoiceRepo.findById(invoiceId).get();
		return source.getCredentials();
	}

	@Override
	public Paging<Invoice> query(String query, Pageable pageable) {
		Page<Invoice> page = invoiceRepo.search(query, pageable);
		List<Invoice> ls = page.getContent().stream().map(this::toTransient).collect(Collectors.toList());
		return new Paging<>(ls, pageable, page.getTotalElements());
	}

	@Override
	protected Invoice toTransient(Invoice source) {
		if (source == null) {
			return source;
		}
		Invoice target = new Invoice();
		BeanUtils.copyProperties(source, target, Invoice.getIgnoreProperties("credentials"));
		return target;
	}

	@Override
	protected Invoice transientDetail(@Valid Invoice source) {
		return source;
	}
	
}
