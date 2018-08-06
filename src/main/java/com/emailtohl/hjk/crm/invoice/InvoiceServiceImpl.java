package com.emailtohl.hjk.crm.invoice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.activiti.engine.ActivitiTaskAlreadyClaimedException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.emailtohl.hjk.crm.entities.Check;
import com.emailtohl.hjk.crm.entities.Flow;
import com.emailtohl.hjk.crm.entities.FlowType;
import com.emailtohl.hjk.crm.entities.BinFile;
import com.emailtohl.hjk.crm.entities.Invoice;
import com.emailtohl.hjk.crm.flow.FlowRepo;
import com.github.emailtohl.lib.StandardService;
import com.github.emailtohl.lib.exception.ForbiddenException;
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
	private FlowRepo flowRepo;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	
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
	 * @return
	 */
	public List<Flow> findTodoTasks() {
		String userId = USERNAME.get();
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
			String processInstanceId = task.getProcessInstanceId();
			Flow flow = flowRepo.findByProcessInstanceId(processInstanceId);
			if (flow == null) {
				return null;
			}
			flow.setActivityId(task.getTaskDefinitionKey());
			flow.setTaskAssignee(task.getAssignee());
			flow.setTaskId(task.getId());
			flow.setTaskName(task.getName());
			return flow;
		}).filter(flow -> {
			return flow != null;
		}).collect(Collectors.toList());
	}
	
	/**
	 * 签收任务
	 * @param taskId
	 * @return
	 */
	public Invoice claim(String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			throw new NotFoundException("taskId: " + taskId + " not found");
		}
		String userId = USERNAME.get();
		try {
			taskService.claim(taskId, userId);
		} catch (ActivitiTaskAlreadyClaimedException e) {
			throw new NotAcceptableException("Activiti task already claimed exception", e);
		}
		String processInstanceId = task.getProcessInstanceId();
		return transientDetail(invoiceRepo.findByFlowProcessInstanceId(processInstanceId));
	}
	/**
	 * 审核任务
	 * @param taskId
	 * @param checkApproved
	 * @param checkComment
	 */
	public void check(String taskId, boolean checkApproved, String checkComment) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			throw new NotFoundException("taskId: " + taskId + "not found");
		}
		switch (task.getTaskDefinitionKey()) {
		case "administrationAudit":
			// 将审核信息添加到流程参数上，并完成此任务
			runtimeService.setVariable(task.getExecutionId(), "checkApproved", String.valueOf(checkApproved));
			runtimeService.setVariable(task.getExecutionId(), "checkComment", checkComment);
			taskService.complete(taskId);
			// 同时维护相关数据
			Flow flow = flowRepo.findByProcessInstanceId(task.getProcessDefinitionId());
			if (flow == null) {
				throw new InnerDataStateException("not found flow entity by processDefinitionId: " + task.getProcessDefinitionId());
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
	 * @param taskId
	 * @param reApply
	 * @param invoice
	 * @return
	 */
	public Invoice reApply(String taskId, boolean reApply, Invoice invoice) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			throw new NotFoundException("taskId: " + taskId + "not found");
		}
		String processInstanceId = task.getProcessInstanceId();
		Invoice src = invoiceRepo.findByFlowProcessInstanceId(processInstanceId);
		if (src == null) {
			throw new InnerDataStateException("not found invoice entity by processInstanceId: " + processInstanceId);
		}
		String userId = USERNAME.get();
		if (!invoice.getFlow().getApplyUserId().equals(userId)) {
			throw new ForbiddenException("The user: " + userId + " is not the submitter of the task");
		}
		if (!"modifyApply".equals(task.getTaskDefinitionKey())) {
			throw new NotAcceptableException("TaskDefinitionKey is not modifyApply");
		}
		Invoice result;
		if (reApply) {
			result = update(src.getId(), invoice);
		} else {
			result = transientDetail(src);
		}
		runtimeService.setVariable(task.getExecutionId(), "reApply", String.valueOf(reApply));
		taskService.complete(taskId);
		return result;
	}

	@Override
	public Set<BinFile> getCredentials(Long invoiceId) {
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
