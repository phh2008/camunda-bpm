package com.example;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author huihui.peng
 * @version V1.0
 * @date 2020/7/24
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {CamundaSwaggerApplication.class})
public class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    private String taskId = "483970470522261504";

    /**
     * 根据业务 key
     * 获取流程执行中的任务
     */
    @Test
    public void testQueryTaskListByBizKey() {
        String bizKey = "biz-leave-20200724002";
        List<Task> list = this.taskService.createTaskQuery()
                .processInstanceBusinessKey(bizKey)
                .list();
        Assertions.assertFalse(CollectionUtils.isEmpty(list), "任务为空");
        list.forEach(t -> {
            System.out.println("任务ID: " + t.getId());
            System.out.println("任务名称: " + t.getName());
            System.out.println("流程实例ID: " + t.getProcessInstanceId());
            System.out.println("代理人: " + t.getAssignee());
            System.out.println("执行ID: " + t.getExecutionId());
            System.out.println("----------------------------------------------");
        });
    }

    /**
     * 完成任务
     * 添加流程变量
     */
    @Test
    public void testCompleteTask() {
        //添加流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("dmList", "zhangsan,wangwu");
        this.taskService.complete(taskId, variables);
    }

    /**
     * 根据代理人或候选人查询任务
     */
    @Test
    public void testQueryTaskByAssigneeOrCandidateUser() {
        String user = "zhangsan";
        List<Task> list = this.taskService.createTaskQuery()
                .taskCandidateUser(user)
                //.taskAssignee(user)
                .list();
        Assertions.assertFalse(CollectionUtils.isEmpty(list), "任务为空");
        list.forEach(t -> {
            System.out.println("任务ID: " + t.getId());
            System.out.println("任务名称: " + t.getName());
            System.out.println("流程实例ID: " + t.getProcessInstanceId());
            System.out.println("代理人: " + t.getAssignee());
            System.out.println("执行ID: " + t.getExecutionId());
            System.out.println("----------------------------------------------");
        });
    }

    /**
     * 拾取任务
     */
    @Test
    public void testClaimTask() {
        //任意用户都可拾取任务，即在非候选人中都可拾取，
        //这种任务权限需要应用自行控制
        String user = "zhangsan";
        this.taskService.claim(taskId, user);
    }

    /**
     * 设置代理人
     */
    @Test
    public void testSetAssignee() {
        //值为 null 表示放弃此任务,候选人又可看到此任务
        String user = null;
        this.taskService.setAssignee(taskId, user);
    }

    /**
     * 根据任务ID查询任务
     */
    @Test
    public void testGetTaskById() {
        Task task = this.taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        Assertions.assertNotNull(task, "任务不存在");
        System.out.println("任务ID: " + task.getId());
        System.out.println("任务名称: " + task.getName());
        System.out.println("流程实例ID: " + task.getProcessInstanceId());
        System.out.println("代理人: " + task.getAssignee());
        System.out.println("执行ID: " + task.getExecutionId());
        System.out.println("----------------------------------------------");
    }


}
