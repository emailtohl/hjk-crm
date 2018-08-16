package com.emailtohl.hjk.crm.organization;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.activiti.engine.ActivitiTaskAlreadyClaimedException;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.emailtohl.hjk.crm.config.SecurityConfig;
import com.emailtohl.hjk.crm.entities.BinFile;
import com.emailtohl.hjk.crm.entities.Check;
import com.emailtohl.hjk.crm.entities.Flow;
import com.emailtohl.hjk.crm.entities.FlowType;
import com.emailtohl.hjk.crm.entities.Organization;
import com.emailtohl.hjk.crm.file.BinFileRepo;
import com.emailtohl.hjk.crm.flow.FlowRepo;
import com.github.emailtohl.lib.StandardService;
import com.github.emailtohl.lib.exception.ForbiddenException;
import com.github.emailtohl.lib.exception.InnerDataStateException;
import com.github.emailtohl.lib.exception.NotAcceptableException;
import com.github.emailtohl.lib.exception.NotFoundException;
import com.github.emailtohl.lib.jpa.AuditedRepository.Tuple;
import com.github.emailtohl.lib.jpa.Paging;

/**
 * 发票资料管理接口的实现
 * 
 * @author HeLei
 */
@Service
@Transactional
public class OrganizationServiceImpl extends StandardService<Organization, Long> implements OrganizationService {
	public final static String PROCESS_DEFINITION_KEY = "organization";
	@Autowired
	private OrganizationRepo organizationRepo;
	@Autowired
	private FlowRepo flowRepo;
	@Autowired
	private BinFileRepo binFileRepo;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private IdentityService identityService;

	private ExampleMatcher taxNumberMatcher = ExampleMatcher.matching().withMatcher("taxNumber", GenericPropertyMatchers.exact());
	private ExampleMatcher accountMatcher = ExampleMatcher.matching().withMatcher("taxNumber", GenericPropertyMatchers.exact());

	@Override
	public boolean isTaxNumberExist(String taxNumber) {
		Organization organization = new Organization();
		organization.setTaxNumber(taxNumber);
		Example<Organization> example = Example.<Organization>of(organization, taxNumberMatcher);
		return organizationRepo.exists(example);
	}
	
	@Override
	public boolean isAccountExist(String account) {
		Organization organization = new Organization();
		organization.setAccount(account);
		Example<Organization> example = Example.<Organization>of(organization, accountMatcher);
		return organizationRepo.exists(example);
	}
	
	@Override
	public Organization create(Organization organization) {
		// 校验提交的表单信息
		validate(organization);
		organization.setPass(false);
		// 如果没有填写收票地址，那么就把公司地址设置为收票地址
		if (!hasText(organization.getDeliveryAddress())) {
			organization.setDeliveryAddress(organization.getAddress());
		}
		// 保存凭证信息
		Set<BinFile> pbf = organization.getCredentials().stream().filter(c -> c.getId() != null).map(BinFile::getId)
				.map(id -> binFileRepo.findById(id).get()).collect(Collectors.toSet());
		organization.getCredentials().clear();// 清空参数里面的凭证
		organization.getCredentials().addAll(pbf);// 再添加上持久化的凭证
		// 先保存开票资料，获取ID
		organizationRepo.persist(organization);

		// 关联流程
		Flow fd = new Flow();
		fd.setFlowType(FlowType.ORGANIZATION);
		String[] username = USER_ID.get().split(SecurityConfig.SEPARATOR);
		fd.setApplyUserId(username[0]);
		fd.setApplyUserName(username[1]);
		// 计算流程编号
		LocalDate d = LocalDate.now();
		int year = d.getYear(), month = d.getMonthValue(), day = d.getDayOfMonth();
		StringBuilder flowNum = new StringBuilder();
		flowNum.append(FlowType.ORGANIZATION.name()).append('-').append(year);
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
		String businessKey = organization.getId().toString();
		flowNum.append('-').append(businessKey);
		// 设置流程编号
		fd.setFlowNum(flowNum.toString());
		// 填写点流程的传输信息
		Map<String, Object> variables = new HashMap<>();
		variables.put("flowType", FlowType.ORGANIZATION);
		variables.put("applyUserId", username[0]);
		variables.put("applyUserName", username[1]);
		variables.put("flowNum", flowNum);
		variables.put("businessKey", businessKey);
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, businessKey,
				variables);
		String processInstanceId = processInstance.getId();
		LOG.debug("start process of {key={}, bkey={}, pid={}}", PROCESS_DEFINITION_KEY, businessKey, processInstanceId);

		fd.setProcessInstanceId(processInstanceId);
		flowRepo.save(fd);
		organization.setFlow(fd);
		return organization;
	}

	@Override
	public Organization read(Long id) {
		Organization source = organizationRepo.findById(id).get();
		return transientDetail(source);
	}

	@Override
	public Organization findByFlowProcessInstanceId(String processInstanceId) {
		Organization source = organizationRepo.findByFlow_ProcessInstanceId(processInstanceId);
		return transientDetail(source);
	}

	@Override
	public Paging<Organization> query(Organization example, Pageable pageable) {
		Page<Organization> page = organizationRepo.queryForPage(example, pageable);
		List<Organization> ls = page.getContent().stream().map(this::toTransient).collect(Collectors.toList());
		return new Paging<>(ls, pageable, page.getTotalElements());
	}

	@Override
	public List<Organization> query(Organization example) {
		return organizationRepo.queryForList(example).stream().map(this::toTransient).collect(Collectors.toList());
	}

	@Override
	public Organization update(Long id, Organization organization) {
		Organization source = organizationRepo.findById(id).get();
		if (hasText(organization.getName())) {
			source.setName(organization.getName());
		}
		if (hasText(organization.getTaxNumber())) {
			source.setTaxNumber(organization.getTaxNumber());
		}
		if (hasText(organization.getAddress())) {
			source.setAddress(organization.getAddress());
		}
		if (hasText(organization.getTelephone())) {
			source.setTelephone(organization.getTelephone());
		}
		if (hasText(organization.getDepositBank())) {
			source.setDepositBank(organization.getDepositBank());
		}
		if (hasText(organization.getAccount())) {
			source.setAccount(organization.getAccount());
		}
		if (hasText(organization.getPrincipal())) {
			source.setPrincipal(organization.getPrincipal());
		}
		if (hasText(organization.getPrincipalPhone())) {
			source.setPrincipalPhone(organization.getPrincipalPhone());
		}
		if (hasText(organization.getDeliveryAddress())) {
			source.setDeliveryAddress(organization.getDeliveryAddress());
		}
		if (organization.getCredentials().size() > 0) {
			Set<BinFile> pbf = organization.getCredentials().stream().filter(c -> c.getId() != null).map(BinFile::getId)
					.map(fid -> binFileRepo.findById(fid).get()).collect(Collectors.toSet());
			source.getCredentials().clear();// 清空参数里面的凭证
			source.getCredentials().addAll(pbf);// 再添加上持久化的凭证
		}
		return transientDetail(source);
	}

	@Override
	public void delete(Long id) {
		organizationRepo.deleteById(id);
	}

	/**
	 * 查询当前用户的任务
	 * 
	 * @return
	 */
	public List<Flow> findTodoTasks() {
		String[] username = USER_ID.get().split(SecurityConfig.SEPARATOR);
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
			flowRepo.findByProcessInstanceId(task.getProcessInstanceId());
			return flow;
		}).collect(Collectors.toList());
	}

	/**
	 * 签收任务
	 * 
	 * @param taskId
	 * @return
	 */
	public Organization claim(String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			throw new NotFoundException("taskId: " + taskId + " not found");
		}
		String[] username = USER_ID.get().split(SecurityConfig.SEPARATOR);
		String userId = username[0];
		try {
			taskService.claim(taskId, userId);
		} catch (ActivitiTaskAlreadyClaimedException e) {
			throw new NotAcceptableException("Activiti task already claimed exception", e);
		}
		String processInstanceId = task.getProcessInstanceId();
		return transientDetail(organizationRepo.findByFlow_ProcessInstanceId(processInstanceId));
	}

	/**
	 * 审核任务，包括申请人重提申请或放弃申请
	 * 
	 * @param taskId
	 * @param checkApproved
	 * @param checkComment
	 */
	public void check(String taskId, boolean checkApproved, String checkComment) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			throw new NotFoundException("taskId: " + taskId + "not found");
		}
		Flow flow = flowRepo.findByProcessInstanceId(task.getProcessInstanceId());
		if (flow == null) {
			throw new InnerDataStateException(
					"not found flow entity by processDefinitionId: " + task.getProcessDefinitionId());
		}
		String[] username = USER_ID.get().split(SecurityConfig.SEPARATOR);
		String userId = username[0];
		if (!userId.equals(task.getAssignee())) {
			throw new ForbiddenException(username[1] + " are not the executor of the task");
		}
		switch (task.getTaskDefinitionKey()) {
		case "administration_audit":
			// 将审核信息添加到流程参数上，并完成此任务
			runtimeService.setVariable(task.getExecutionId(), "checkApproved", checkApproved);
			runtimeService.setVariable(task.getExecutionId(), "checkComment", checkComment);
			break;
		case "modifyApply":
			// 将审核信息添加到流程参数上，并完成此任务
			runtimeService.setVariable(task.getExecutionId(), "reApply", checkApproved);
			runtimeService.setVariable(task.getExecutionId(), "checkComment", checkComment);
			break;
		default:
			return;
		}
		// 维护相关数据
		Check check = new Check(task, checkApproved, checkComment);
		org.activiti.engine.identity.User u = identityService.createUserQuery().userId(check.getCheckerId()).singleResult();
		if (u != null) {
			check.setCheckerName(u.getFirstName());
		}
		flow.getChecks().add(check);
		if (hasText(checkComment)) {
			// 将审批的评论添加进记录中
			taskService.addComment(taskId, task.getProcessInstanceId(), checkComment);
		}
		taskService.complete(taskId);
	}

	@Override
	public Set<BinFile> getCredentials(Long organizationId) {
		Organization source = organizationRepo.findById(organizationId).get();
		return source.getCredentials();
	}

	@Override
	public Paging<Organization> query(String query, Pageable pageable) {
		Page<Organization> page = organizationRepo.search(query, pageable);
		List<Organization> ls = page.getContent().stream().map(this::toTransient).collect(Collectors.toList());
		return new Paging<>(ls, pageable, page.getTotalElements());
	}
	
	@Override
	public List<Tuple<Organization>> getRevisions(Long id) {
		return organizationRepo.getRevisions(id).stream().map(t -> {
			return new Tuple<Organization>(toTransient(t.entity), t.defaultRevisionEntity, t.revisionType);
		}).collect(Collectors.toList());
	}
	
	@Override
	public Organization getEntityAtRevision(Long id, Number revision) {
		return transientDetail(organizationRepo.getEntityAtRevision(id, revision));
	}
	
	@Override
	public List<Organization> myRegisterOrganization() {
		String[] username = USER_ID.get().split(SecurityConfig.SEPARATOR);
		return organizationRepo.findByFlow_ApplyUserId(username[0]).stream().map(this::toTransient)
				.collect(Collectors.toList());
	}

	@Override
	protected Organization toTransient(Organization source) {
		if (source == null) {
			return source;
		}
		Organization target = new Organization();
		BeanUtils.copyProperties(source, target, "credentials", "flow");
		return target;
	}

	@Override
	protected Organization transientDetail(Organization source) {
		Organization target = toTransient(source);
		Flow sourceFlow = source.getFlow();
		Flow targetFlow = new Flow();
		BeanUtils.copyProperties(sourceFlow, targetFlow, "checks");
		Task task = taskService.createTaskQuery().processInstanceId(sourceFlow.getProcessInstanceId()).singleResult();
		String taskAssignee = task.getAssignee();
		if (hasText(taskAssignee)) {
			targetFlow.setTaskAssignee(taskAssignee);
			org.activiti.engine.identity.User u = identityService.createUserQuery().userId(taskAssignee).singleResult();
			if (u != null) {
				targetFlow.setTaskAssigneeName(u.getFirstName());
			}
		}
		targetFlow.setTaskDefinitionKey(task.getTaskDefinitionKey());
		targetFlow.setTaskId(task.getId());
		targetFlow.setTaskName(task.getName());
		targetFlow.getChecks().addAll(sourceFlow.getChecks());// 懒加载所有的check信息
		target.setFlow(targetFlow);
		target.getCredentials().addAll(source.getCredentials());// 懒加载所有的凭证
		return target;
	}

}
