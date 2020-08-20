package com.example;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.util.Arrays;
import org.camunda.bpm.engine.task.Task;

import lombok.extern.slf4j.Slf4j;

/**
 * 请假单流程验测 base
 * 
 * @author Sand.Chang
 */

@Slf4j
public class LeaveProcessTestBase extends CamundaProcessTestBase {
	// 流程启动者
	protected String startFlowUser = "lisi";
	// 依序审批的上级主管
	protected String[] deptMgrs = new String[] { "zhangsan", "wangwu" };
	// 总经理
	protected String gm = "jacklin";
	// HR
	protected String hr = "zhaoliu";

	static {
		PROCESS_DEFITION_KEY = "Process_leave4_v3";
	}

	/**
	 * 启动请假单流程
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void startLeaveProcessInstanceBase() {
		Map<String, Object> variables = new HashMap();
		variables.put("user", startFlowUser);
		startNewProcessInstance(variables);
	}

	/**
	 * Complete 填寫請假 task （往後至依序審批）
	 */
	public void completeFillFormTaskBase() {
		log.info(LOG_SECTION, "completeFillFormTask");
		Map<String, Object> variables = new HashMap<>();
		variables.put("dmList", Arrays.asList(deptMgrs));
		Task task = getCurrentTask();
		this.taskService.completeWithVariablesInReturn(task.getId(), variables, true);
		log.info("----------------------------------------------");
	}

	/**
	 * Complete 依序審批 task （駁回到填寫請假）
	 */
	public void rejectToFillFormTaskBase() {
		log.info(LOG_SECTION, "rejectToFillFormTask");
		Map<String, Object> variables = new HashMap<>();
		variables.put("day", 0);
		variables.put("option", "N");
		Task task = getCurrentTask();
		this.taskService.complete(task.getId(), variables);
		log.info("----------------------------------------------");
	}

	/**
	 * Complete 依序審批 task （往後至總經理審批）
	 */
	public void completeDepManagerTaskBase() {
		log.info(LOG_SECTION, "completeDepManagerTask");
		Map<String, Object> variables = new HashMap<>();
		variables.put("day", 5);
		variables.put("option", "Y");
		variables.put("gm", gm);
		Task task = getCurrentTask();
		this.taskService.complete(task.getId(), variables);
	}

	/**
	 * Complete 總經理審批 task （往後至 HR）
	 */
	public void completeGMTaskBase() {
		log.info(LOG_SECTION, "completeGMTask");
		Map<String, Object> variables = new HashMap<>();
		variables.put("hr", hr);
		Task task = getCurrentTask();
		this.taskService.complete(task.getId(), variables);
	}

	/**
	 * Complete HR task （流程結束）
	 */
	public void completeHRTaskBase() {
		log.info(LOG_SECTION, "completeHRTask");
		Map<String, Object> variables = new HashMap<>();
		Task task = getCurrentTask();
		this.taskService.complete(task.getId(), variables);
	}
}
