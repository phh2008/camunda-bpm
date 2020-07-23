package com.example;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.core.model.Properties;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.pvm.PvmTransition;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * 描述
 *
 * @author huihui.peng
 * @version V1.0
 * @date 2020/7/23
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {CamundaSwaggerApplication.class})
public class RepositoryServiceTest {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;

    /**
     * 获取当前流程活动节点信息
     */
    @Test
    public void testGetActivity() {
        // 1,根据任务ID查询任务实例
        String taskId = "483682134905720833";
        Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
        // 2,取出流程定义ID
        String processDefinitionId = task.getProcessDefinitionId();
        // 3,取出流程实例ID
        String processInstanceId = task.getProcessInstanceId();
        // 4,根据流程实例ID查询流程实例
        ProcessInstance processInstance = this.runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        // 5,根据流程定义ID查询流程定义的XML信息
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) this.repositoryService
                .getProcessDefinition(processDefinitionId);
        // 6,从流程实例对象里面取出当前活动节点ID
        List<ExecutionEntity> executions = this.runtimeService.createExecutionQuery()
                .processInstanceId(processInstanceId)
                .list()
                .stream()
                .map(e -> (ExecutionEntity) e)
                .collect(toList());
        // 7,使用活动ID取出xml和当前活动ID相关节点数据
        if (executions == null || executions.isEmpty()) {
            System.err.println("流程execution为空");
            return;
        }
        executions.forEach(e -> {
            ActivityImpl activityImpl = processDefinition.findActivity(e.getActivityId());
            System.out.println("X: " + activityImpl.getX());
            System.out.println("Y: " + activityImpl.getY());
            System.out.println("With: " + activityImpl.getWidth());
            System.out.println("Height: " + activityImpl.getHeight());
            System.out.println("--------------------------------------------------------");
        });
    }

    /**
     * 获取当前任务活动节点的连接线路由
     */
    @Test
    public void testGetOutGoingTransitions() {
        // 1,根据任务ID查询任务实例
        String taskId = "483682134905720833";
        Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
        System.out.println("任务名称：" + task.getName());
        // 2,根据流程定义ID查询流程定义的XML信息
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) this.repositoryService
                .getProcessDefinition(task.getProcessDefinitionId());
        // 3,获取流程执行实例
        ExecutionEntity execution = (ExecutionEntity) this.runtimeService.createExecutionQuery()
                .executionId(task.getExecutionId())
                .singleResult();
        // 4,获取活动节点
        ActivityImpl activity = processDefinition.findActivity(execution.getActivityId());
        System.out.println("活动名称：" + activity.getName());
        // 5,取出连线信息
        List<PvmTransition> pvmTransitions = activity.getOutgoingTransitions();
        if (pvmTransitions == null || pvmTransitions.isEmpty()) {
            System.err.println("连线信息为空");
            return;
        }
        for (PvmTransition pvm : pvmTransitions) {
            Properties props = pvm.getProperties();
            props.toMap().forEach((k, v) -> {
                System.out.println(k + " : " + v);
            });
            System.out.println("------------------------------------------------");
        }
    }

}
