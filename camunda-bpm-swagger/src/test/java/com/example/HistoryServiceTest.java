package com.example;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

/**
 * 描述
 *
 * @author huihui.peng
 * @version V1.0
 * @date 2020/7/22
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {CamundaSwaggerApplication.class})
public class HistoryServiceTest {

    @Autowired
    private HistoryService historyService;

    /**
     * 查询某个流程实例执行了多少次任务
     */
    @Test
    public void testQueryHistoryTask() {
        String assignee = "zhangsan";
        String processIntanceId = "6b22496e-ca68-11ea-a3bf-0a0027000006";
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processIntanceId)
                //.taskAssignee(assignee)
                .orderByHistoricActivityInstanceStartTime().asc()
                .list();
        if (list == null || list.isEmpty()) {
            System.err.println("任务为空");
            return;
        }
        list.forEach(t -> {
            System.out.println("任务ID: " + t.getId());
            System.out.println("任务名称: " + t.getName());
            System.out.println("流程实例ID: " + t.getProcessInstanceId());
            System.out.println("开始时间: " + t.getStartTime());
            System.out.println("结束时间: " + t.getEndTime());
            System.out.println("持续时长【毫秒】: " + t.getDurationInMillis());
            System.out.println("--------------------------------------------------------");
        });
    }

    @Test
    public void testQueryHistoryActivity() {
        String processInstanceId = "483320878164217856";
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()//创建历史活动实例的查询
                .processInstanceId(processInstanceId)//
                .orderByHistoricActivityInstanceStartTime().asc()//
                .list();
        if (list == null || list.isEmpty()) {
            System.err.println("活动为空");
            return;
        }
        for (HistoricActivityInstance hai : list) {
            System.out.println("活动ID: " + hai.getId());
            System.out.println("活动名称: "+hai.getActivityName());
            System.out.println("实例定义ID: " + hai.getProcessDefinitionId());
            System.out.println("活动类型: " + hai.getActivityType());
            System.out.println("开始时间: " + hai.getStartTime());
            System.out.println("结束时间: " + hai.getEndTime());
            System.out.println("持续时长【毫秒】: " + hai.getDurationInMillis());
            System.out.println("----------------------------------------------------------");
        }
    }

    /**
     * 查询历史流程实例
     */
    @Test
    public void testQueryHistoryProcessInstance() {
        String bizKey = "biz-leave-20200722002";
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery()//创建历史流程实例查询
                .processInstanceBusinessKey(bizKey)
                .orderByProcessInstanceStartTime().asc()
                .list();

        if (list == null || list.isEmpty()) {
            System.err.println("历史流程实例为空");
            return;
        }
        for (HistoricProcessInstance hpi : list) {
            System.out.println("流程实例ID: " + hpi.getId());
            System.out.println("实例定义ID: " + hpi.getProcessDefinitionId());
            System.out.println("开始时间: " + hpi.getStartTime());
            System.out.println("结束时间: " + hpi.getEndTime());
            System.out.println("持续时长【毫秒】: " + hpi.getDurationInMillis());
            System.out.println("-------------------------------------------------------------");
        }
    }

}
