package com.emailtohl.hjk.crm.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.activiti.engine.task.Task;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 保存每一次的审核信息，包括提交人自己的重提或取消
 * 
 * @author HeLei
 */
@Embeddable
public class Check implements Serializable {
	private static final long serialVersionUID = 3468053227931998584L;
	// 所处节点
	private String taskDefinitionKey;
	// 所审核的任务名
	private String taskName;
	// 审核人id
	private String checkerId;
	// 审核人姓名
	private String checkerName;
	// 审核是否通过
	private Boolean checkApproved;
	// 审核意见
	private String checkComment;
	// 审核时间
	private Date checkTime;
	
	public Check() {}
	
	public Check(Task task, boolean checkApproved, String checkComment) {
		this.taskDefinitionKey = task.getTaskDefinitionKey();
		this.taskName = task.getName();
		this.checkerId = task.getOwner();
		this.checkTime = new Date();
		this.checkApproved = checkApproved;
		this.checkComment = checkComment;
	}

	@Column(nullable = false, updatable = false)
	public String getTaskDefinitionKey() {
		return taskDefinitionKey;
	}
	public void setTaskDefinitionKey(String taskDefinitionKey) {
		this.taskDefinitionKey = taskDefinitionKey;
	}

	@Column(name = "task_name", nullable = false, updatable = false)
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	@Column(name = "checker_id", nullable = false, updatable = false)
	public String getCheckerId() {
		return checkerId;
	}
	public void setCheckerId(String checkerId) {
		this.checkerId = checkerId;
	}

	@Column(name = "checker_name")
	public String getCheckerName() {
		return checkerName;
	}
	public void setCheckerName(String checkerName) {
		this.checkerName = checkerName;
	}

	@Column(name = "check_approved", nullable = false, updatable = false)
	public Boolean getCheckApproved() {
		return checkApproved;
	}
	public void setCheckApproved(Boolean checkApproved) {
		this.checkApproved = checkApproved;
	}

	@Column(name = "check_comment", nullable = false, updatable = false)
	public String getCheckComment() {
		return checkComment;
	}
	public void setCheckComment(String checkComment) {
		this.checkComment = checkComment;
	}

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "check_time", nullable = false, updatable = false)
	public Date getCheckTime() {
		return checkTime;
	}
	public void setCheckTime(Date checkTime) {
		this.checkTime = checkTime;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((checkTime == null) ? 0 : checkTime.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Check other = (Check) obj;
		if (checkTime == null) {
			if (other.checkTime != null)
				return false;
		} else if (!checkTime.equals(other.checkTime))
			return false;
		return true;
	}

}
