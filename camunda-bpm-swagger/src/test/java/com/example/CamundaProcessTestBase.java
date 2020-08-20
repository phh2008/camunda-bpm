package com.example;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Comment;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CamundaProcessTestBase {
	@Autowired
	protected RuntimeService runtimeService;
	@Autowired
	protected TaskService taskService;
	@Autowired
	protected HistoryService historyService;
	@Autowired
	protected IdentityService identityService;

	protected static String LOG_SECTION = "************************* {} *************************";

	protected static String PROCESS_DEFITION_KEY = "";
	protected static String BUSINESS_KEY = "";

	protected static String CURRENT_USER = "";
	protected static String QUERY_USER = "";
	protected static String QUERY_OWNER = "";

	@BeforeEach
	public void initializeAuthorizeUser() {
		log.info("Current user is {}", CURRENT_USER);
		identityService.setAuthenticatedUserId(CURRENT_USER);
	}

	/**
	 * 實作會簽單人功能
	 */
	public void delegateTask(String delegateTo) {
		log.info(LOG_SECTION, "delegateTask");
		Task task = getCurrentTask();
		addComment("task assignee is " + task.getAssignee() + " , delegateTask to " + delegateTo + " now");
		log.info("task assignee is {}, delegateTask to {} now", task.getAssignee(), delegateTo);
		this.taskService.delegateTask(task.getId(), delegateTo);
		log.info("----------------------------------------------");
		queryTaskListByBusinessKey();
	}

	protected void startNewProcessInstance(Map<String, Object> variables) {
		log.info(LOG_SECTION, "startNewProcessInstance");
		ProcessInstance instance = runtimeService.startProcessInstanceByKey(PROCESS_DEFITION_KEY, BUSINESS_KEY, variables);
		log.info("ProcessDefinitionId: {} ", instance.getProcessDefinitionId());
		log.info("ProcessInstanceId: {} ", instance.getProcessInstanceId());
		log.info("RootProcessInstanceId: {} ", instance.getRootProcessInstanceId());
	}

	/*
	 * query task list by business key
	 */
	@Test
	@Order(90)
	public void queryTaskListByBusinessKey() {
		log.info(LOG_SECTION, "queryTaskListByBusinessKey");
		List<Task> list = this.taskService.createTaskQuery().processInstanceBusinessKey(BUSINESS_KEY).list();
		log.info(" get task list by key: {} ", BUSINESS_KEY);
		printTaskInfo(list);
	}

	/**
	 * Query Candidate task info by User
	 */
	@Test
	@Order(90)
	public void queryCandidateTaskByUser() {
		log.info(LOG_SECTION, "queryCandidateTaskByUser user is " + QUERY_USER);
		// List<Task> list =
		// this.taskService.createTaskQuery().taskAssignee(user).list();
		List<Task> list = this.taskService.createTaskQuery().taskCandidateUser(QUERY_USER).list();
		// TODO 排除已經有 taskcomment 的人
		printTaskInfo(list);
	}

	/**
	 * Query Owner task info by User
	 */
	@Test
	@Order(90)
	public void queryTaskByOwner() {
		log.info(LOG_SECTION, "queryTaskByOwner owner is " + QUERY_OWNER);
		// 只能查到 onwer 的，查不到 assignee!
		List<Task> list = this.taskService.createTaskQuery().taskOwner(QUERY_OWNER).list();
		printTaskInfo(list);
	}

	/**
	 * Query task info by User
	 */
	@Test
	@Order(91)
	public void queryTaskByUser() {
		log.info(LOG_SECTION, "queryTaskByUser user is " + QUERY_USER);
		// List<Task> list =
		// this.taskService.createTaskQuery().taskAssignee(user).list();
		List<Task> list = this.taskService.createTaskQuery().taskInvolvedUser(QUERY_USER).list();
		printTaskInfo(list);
	}

	/**
	 * Query history task info by business key
	 */
	@Test
	@Order(92)
	public void queryHistoryTaskByBusinessKey() {
		log.info(LOG_SECTION,
				"queryHistoryTaskByBusinessKey" + " key is " + BUSINESS_KEY);
		List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
				.processInstanceBusinessKey(BUSINESS_KEY).orderByHistoricActivityInstanceStartTime().asc().list();
		printHistoryInfo(list);
	}

	/**
	 * Add comment
	 */
//	@Test
//	@Order(31)
	public void addComment(String msg) {
		Task task = getCurrentTask();
		log.info(LOG_SECTION, "addComment");
		taskService.createComment(task.getId(), task.getProcessInstanceId(), msg);
		log.info("----------------------------------------------");
	}

	/**
	 * Query history task info by User
	 */
//	@Test
//	@Order(99)
	public void queryHistoryTaskByUser() {
		log.info(LOG_SECTION,
				"queryHistoryTaskByUser user is" + QUERY_USER);
		List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskInvolvedUser(QUERY_USER)
				.orderByHistoricActivityInstanceStartTime().asc().list();
		// 包含 change assignee & delegate task 都可以查得到
		printHistoryInfo(list);
	}

	protected void printTaskInfo(List<Task> list) {
		list.forEach(t -> {
			log.info("task id: {} ", t.getId());
			log.info("activity name: {} ", t.getName());
			log.info("process instance id: {} ", t.getProcessInstanceId());
			log.info("owner {} ", t.getOwner());
			log.info("assignee {} ", t.getAssignee());
			log.info("execution id: {} ", t.getExecutionId());
			printComments(taskService.getTaskComments(t.getId()));
			log.info("----------------------------------------------");
		});
	}

	protected Task getCurrentTask() {
		List<Task> tasks = this.taskService.createTaskQuery().processInstanceBusinessKey(BUSINESS_KEY).list();
		Assertions.assertFalse(CollectionUtils.isEmpty(tasks), " task is empty!");
		Task task = tasks.get(0);
		log.info("task id: {} ", task.getId());
		return task;
	}

	protected void printHistoryInfo(List<HistoricTaskInstance> list) {
		list.forEach(t -> {
			log.info("task id: {} ", t.getId());
			log.info("activity name: {} ", t.getName());
			log.info("process instance id: {} ", t.getProcessInstanceId());
			log.info("owner {} ", t.getOwner());
			log.info("assignee {} ", t.getAssignee());
			log.info("execution id: {} ", t.getExecutionId());
			log.info("start time: {} ", t.getStartTime());
			log.info("end time: {} ", t.getEndTime());
			log.info("spend time: {} ", t.getDurationInMillis());
			printComments(taskService.getTaskComments(t.getId()));
			log.info("----------------------------------------------");
		});
	}

	private void printComments(List<Comment> comments) {
		log.info("comment records: {}", comments.size());
		comments.forEach(c -> {
			log.info("  comment is {} by {} at {}", c.getFullMessage(), c.getUserId(), c.getTime());
		});

	}

	/**
	 * Get task comment users
	 * 
	 * @param task
	 * @return users
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Set<String> getTaskCommentedUsers(Task task) {
		List<Comment> comments = taskService.getTaskComments(task.getId());
		log.info("commented records: {}", comments.size());
		Set<String> commentedUsers = new HashSet();
		comments.forEach(c -> {
			commentedUsers.add(c.getUserId());
		});
		return commentedUsers;
	}

}
