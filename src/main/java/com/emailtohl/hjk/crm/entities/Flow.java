package com.emailtohl.hjk.crm.entities;

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

import com.github.emailtohl.lib.jpa.BaseEntity;

/**
 * 整个流程涉及的数据，既作为接收流程相关的表单数据，也作为显示层的数据承载对象
 * 
 * @author HeLei
 */
@Entity
public class Flow extends BaseEntity {
	private static final long serialVersionUID = 6842886315737049187L;
	// 关联Activiti的流程id
	private String processInstanceId;
	// 表单号
	private String flowNum;
	// 流程类型
	private FlowType flowType;
	// 申请人id
	private String applyUserId;
	// 是否放弃申请
	private Boolean reApply;
	// 最终结果
	private Boolean pass;
	// 历史的审核信息
	private List<Check> checks = new ArrayList<>();
	// 审核是否通过
	private Boolean checkApproved;
	// 审核意见
	private String checkComment;
	// 当前任务id
	private String taskId;
	// 任务的名字
	private String taskName;
	// 当前任务是否被签收
	private String taskAssignee;
	// 当前所在的活动id
	private String activityId;
	// 下一个活动id
	private String nextActivityId;
	// 下一个活动id
	private String nextActivityName;

	@Column(name = "process_instance_id")
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	@Column(name = "flow_num")
	public String getFlowNum() {
		return flowNum;
	}
	public void setFlowNum(String flowNum) {
		this.flowNum = flowNum;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name = "flow_type", nullable = false)
	public FlowType getFlowType() {
		return flowType;
	}
	public void setFlowType(FlowType flowType) {
		this.flowType = flowType;
	}

	@Column(name = "apply_user_id", nullable = false)
	public String getApplyUserId() {
		return applyUserId;
	}
	public void setApplyUserId(String applyUserId) {
		this.applyUserId = applyUserId;
	}
	
	@Column(name = "re_apply")
	public Boolean getReApply() {
		return reApply;
	}
	public void setReApply(Boolean reApply) {
		this.reApply = reApply;
	}

	@Column(name = "pass")
	public Boolean getPass() {
		return pass;
	}
	public void setPass(Boolean pass) {
		this.pass = pass;
	}
	
	@ElementCollection
	@CollectionTable(name = "flow_data_check", joinColumns = @JoinColumn(name = "flow_data_id"))
	@OrderBy("checkTime ASC")
	public List<Check> getChecks() {
		return checks;
	}
	public void setChecks(List<Check> checks) {
		this.checks = checks;
	}
	
	@Transient
	public Boolean getCheckApproved() {
		return checkApproved;
	}
	public void setCheckApproved(Boolean checkApproved) {
		this.checkApproved = checkApproved;
	}
	
	@Transient
	public String getCheckComment() {
		return checkComment;
	}
	public void setCheckComment(String checkComment) {
		this.checkComment = checkComment;
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
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
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
	
}
