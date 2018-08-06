package com.emailtohl.hjk.crm.flow;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emailtohl.hjk.crm.entities.Flow;

/**
 * 流程数据的存档
 * @author HeLei
 */
public interface FlowRepo extends JpaRepository<Flow, Long> {
	
	Flow findByProcessInstanceId(String processInstanceId);
	
	Flow findByFlowNum(String flowNum);
}
