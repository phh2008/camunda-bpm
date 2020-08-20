package com.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.util.Arrays;
import org.camunda.bpm.engine.task.IdentityLink;
import org.camunda.bpm.engine.task.IdentityLinkType;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.slf4j.Slf4j;

/**
 * 会签多人验测
 * @author Sand.Chang
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { CamundaSwaggerApplication.class })
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
public class CounterSignEngineTest extends LeaveProcessTestBase {
	//会签人员
	private String[] counterUsers = new String[] {"tom","lisi"};
	
	static {
		CURRENT_USER = "zhaoliu";
		BUSINESS_KEY = "biz-leave4-v3-202008190005";
		QUERY_USER = "tom";
		QUERY_OWNER = "zhaoliu";
	}	

	/**
	 * 会签人员
	 */
//	@Test
//	@Order(11)
	public void addCounterSignUsers() {
		List<Object> counterSignUsers = Arrays.asList(counterUsers) ;
		log.info("*************************{} counterSignUsers are {} *************************", "addCounterSignUser", counterSignUsers);
		Task task = getCurrentTask();
		log.info("set task assignee to owner, user is {}", task.getAssignee());
		//登入者改為 task assignee 進行 comment 作業
		identityService.setAuthenticatedUserId(task.getAssignee());
		addComment("please provide any comment for reference, thanks.");
		this.taskService.setOwner(task.getId(), task.getAssignee());
		log.info("and claim task to null for later countersign action");
		taskService.claim(task.getId(), null);
		log.info("add counter sign users be Candidate users");
		counterSignUsers.forEach(c -> {
			this.taskService.addCandidateUser(task.getId(), c.toString());
		});
	}

	/**
	 * 會簽人員進行簽核（提供 comment） 
	 */
//	@Test
//	@Order(21)  // 记得改 CURRENT_USER 再行测试
	public void processCounterSignByUser() {
		log.info("*************************{} by {} *************************", "processCounterSignByUser", CURRENT_USER);
		Task task = getCurrentTask();
		// 需要先做 claim 後才可以進行作業??  經測試，可以不需要 claim 即可進行 comment
		// taskService.claim(task.getId(), user);
		addComment("counter sign completed by " + CURRENT_USER + ".");
		taskService.claim(task.getId(), null);
		checkCompleteCounterSign();
	}

	/**
	 * 檢查當前任務的會簽是否都結束？
	 */
	public void checkCompleteCounterSign() {
		log.info(LOG_SECTION, "checkCompleteCounterSign");
		Task task = getCurrentTask();
		List<String> counterSignUsers = getCounterSignUsers(task);
		log.info("counterSign users are {}", counterSignUsers);
		if(counterSignUsers.isEmpty()) {
			log.info("There is no any counter sign users!");
		}
		else {
			Set<String> commentedUsers = getTaskCommentedUsers(task);
			log.info("commented users are {}", commentedUsers);
			Collection<String> unSignedUsers = CollectionUtils.removeAll(counterSignUsers, commentedUsers);
			if(unSignedUsers.isEmpty()) {
				log.info("counter sign completed ! And return to owner {}" , task.getOwner());
				// 完成會簽，將  task assignee 設定回去給 task owner
				taskService.claim(task.getId(), task.getOwner());
			}
			else {
				log.info("counter sign not completed ! There are unsigned users:{}" , unSignedUsers);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<String> getCounterSignUsers(Task task) {
		//TODO  如果同一活動多個參與者會簽給同一人的議題待處理
		// 同一活動不同參與者的 task id 是不一樣的，所以 by task 去取得的數據都是明確的。
		log.info(LOG_SECTION, "getCounterSignUsers");
		List<IdentityLink> identities = this.taskService.getIdentityLinksForTask(task.getId());
		List<String> counterSignUsers = new ArrayList();
		for (IdentityLink identity : identities) {
			String type = identity.getType();
			// 這邊拿不到 operation_type (add/delete)，有點可惜
			log.info(" identity type is {}, userId is {}", identity.getType(), identity.getUserId());
			switch (type) {
				case IdentityLinkType.ASSIGNEE:
					break;
				case IdentityLinkType.CANDIDATE:
//					if (identity.getGroupId() != null) {
//						camundaTask.setCandidateGroup(identity.getGroupId());
//					}
					if (identity.getUserId() != null) {
						counterSignUsers.add(identity.getUserId());
					}
				case IdentityLinkType.OWNER:
					break;
				default:
					break;
			}
		}
		return counterSignUsers;
	}

}