package com.example;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * 请假单流程验测
 * @author Sand.Chang
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { CamundaSwaggerApplication.class })
@TestMethodOrder(OrderAnnotation.class)
public class LeaveProcessEngineTest extends LeaveProcessTestBase {
	//会签人员
	private String singleCounterUser = "tom";
	
	static {
		CURRENT_USER = "lisi";
		BUSINESS_KEY = "sand-leave4-v3-20200820_001";
		QUERY_USER = "lisi";
		QUERY_OWNER = "zhaoliu";
	}
	
	/**
	 * 启动请假单流程
	 */
//	@Test
//	@Order(10)
	public void startLeaveProcessInstance() {
		startLeaveProcessInstanceBase();
	}

	/**
	 * Complete 填寫請假 task （往後至依序審批）
	 */
	@Test
	@Order(11)
	public void completeFillFormTask() {
		completeFillFormTaskBase();
	}
	
	/**
	 * Complete 依序審批 task （駁回到填寫請假）
	 */
//	@Test
//	@Order(21)
	public void rejectToFillFormTask() {
		rejectToFillFormTaskBase();
	}
	
	/**
	 * Add comment
	 */
//	@Test
//	@Order(31)
	public void addComment() {
		addComment("comment test");
	}
	
	/**
	 * 會簽單人功能
	 */
//	@Test
//	@Order(31)
	public void delegateTo() {
		delegateTask(singleCounterUser);
	}

	/**
	 * Complete 依序審批 task （往後至總經理審批）
	 */
//	@Test
//	@Order(50)
	public void completeDepManagerTask() {
		completeDepManagerTaskBase();
	}

	/**
	 * Complete 總經理審批 task （往後至 HR）
	 */
//	@Test
//	@Order(60)
	public void completeGMTask() {
		completeGMTaskBase();
	}

	/**
	 * Complete HR task （流程結束）
	 */
//	@Test
//	@Order(70)
	public void completeHRTask() {
		completeHRTaskBase();
	}
}
