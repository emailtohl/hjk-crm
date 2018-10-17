package com.emailtohl.hjk.crm.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.activiti.engine.task.Task;
import org.springframework.beans.BeanUtils;

import com.github.emailtohl.lib.jpa.EntityBase;

/**
 * 流程中涉及的数据，以下字段在业务流程中不断变化，所以不做存储：
 * checkApproved、checkComment、taskId、taskName、taskAssignee、taskAssigneeName、taskDefinitionKey、nextActivityId、nextActivityName
 * 
 * @author HeLei
 */
@Entity
public class Flow extends EntityBase {
	private static final long serialVersionUID = 6842886315737049187L;
	// 业务主键
	private String businessKey;
	// 关联Activiti的流程id
	private String processInstanceId;
	// 表单号
	private String flowNum;
	// 流程类型
	private FlowType flowType;
	// 申请人id
	private String applyUserId;
	// 申请人姓名
	private String applyUserName;
	// 描述流程最后的结果
	private Boolean pass;
	// 历史的审核信息
	private List<Check> checks = new ArrayList<>();
	
	/* 下面与过程中的状态有关，不做存储 */
	
	// 当前任务id
	private String taskId;
	// 任务的名字
	private String taskName;
	// 当前任务签收人的ID
	private String taskAssignee;
	// 当前任务签收人的名字
	private String taskAssigneeName;
	// 当前所在的活动id
	private String taskDefinitionKey;
	// 下一个活动id
	private String nextActivityId;
	// 下一个活动id
	private String nextActivityName;
	
	public Flow() {}

	public Flow(String businessKey, FlowType flowType, String applyUserId) {
		this.businessKey = businessKey;
		this.flowType = flowType;
		this.applyUserId = applyUserId;
		// 计算流程编号
		LocalDate d = LocalDate.now();
		int year = d.getYear(), month = d.getMonthValue(), day = d.getDayOfMonth();
		StringBuilder flowNum = new StringBuilder();
		flowNum.append(flowType.name()).append('-').append(year);
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
		flowNum.append('-').append(businessKey);
		this.flowNum = flowNum.toString();
	}

	public void taskInfo(Task task) {
		this.processInstanceId = task.getProcessInstanceId();
		this.taskId = task.getId();
		this.taskName = task.getName();
		this.taskAssignee = task.getAssignee();
		this.taskDefinitionKey = task.getTaskDefinitionKey();
	}
	
	@Column(name = "business_key", nullable = false, updatable = false)
	public String getBusinessKey() {
		return businessKey;
	}
	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	@Column(name = "process_instance_id", nullable = false, updatable = false)
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	@Column(name = "flow_num", updatable = false)
	public String getFlowNum() {
		return flowNum;
	}
	public void setFlowNum(String flowNum) {
		this.flowNum = flowNum;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name = "flow_type", nullable = false, updatable = false)
	public FlowType getFlowType() {
		return flowType;
	}
	public void setFlowType(FlowType flowType) {
		this.flowType = flowType;
	}

	@Column(name = "apply_user_id", nullable = false, updatable = false)
	public String getApplyUserId() {
		return applyUserId;
	}
	public void setApplyUserId(String applyUserId) {
		this.applyUserId = applyUserId;
	}
	
	@Column(name = "apply_user_name", nullable = false, updatable = false)
	public String getApplyUserName() {
		return applyUserName;
	}
	public void setApplyUserName(String applyUserName) {
		this.applyUserName = applyUserName;
	}

	public Boolean getPass() {
		return pass;
	}
	public void setPass(Boolean pass) {
		this.pass = pass;
	}

	@ElementCollection
	@CollectionTable(name = "flow_check", joinColumns = @JoinColumn(name = "flow_data_id"))
	@OrderBy("checkTime ASC")
	public List<Check> getChecks() {
		return checks;
	}
	public void setChecks(List<Check> checks) {
		this.checks = checks;
	}
	
	@Transient
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@Transient
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	@Transient
	public String getTaskAssignee() {
		return taskAssignee;
	}
	public void setTaskAssignee(String taskAssignee) {
		this.taskAssignee = taskAssignee;
	}

	@Transient
	public String getTaskAssigneeName() {
		return taskAssigneeName;
	}
	public void setTaskAssigneeName(String taskAssignee) {
		this.taskAssigneeName = taskAssignee;
	}

	@Transient
	public String getTaskDefinitionKey() {
		return taskDefinitionKey;
	}
	public void setTaskDefinitionKey(String taskDefinitionKey) {
		this.taskDefinitionKey = taskDefinitionKey;
	}
	
	@Transient
	public String getNextActivityId() {
		return nextActivityId;
	}
	public void setNextActivityId(String nextActivityId) {
		this.nextActivityId = nextActivityId;
	}
	
	@Transient
	public String getNextActivityName() {
		return nextActivityName;
	}
	public void setNextActivityName(String nextActivityName) {
		this.nextActivityName = nextActivityName;
	}
	
	public Flow toTransient() {
		Flow target = new Flow();
		BeanUtils.copyProperties(this, target, "checks");
		return target;
	}

	public Flow transientDetail() {
		Flow target = toTransient();
		// 将Hibernate封装的Set改为普通的HashSet
		target.getChecks().addAll(getChecks());
		return target;
	}
	
}
