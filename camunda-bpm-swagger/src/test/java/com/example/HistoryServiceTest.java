package com.example;

import org.camunda.bpm.engine.HistoryService;
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
                .taskAssignee(assignee)
                .list();
        if (list == null || list.isEmpty()) {
            System.err.println("任务为空");
            return;
        }
        list.forEach(t -> {
            System.out.println("ID: " + t.getId());
            System.out.println("任务名称: " + t.getName());
            System.out.println("流程实例ID: " + t.getProcessInstanceId());
            System.out.println("开始时间: " + t.getStartTime());
            System.out.println("结束时间: " + t.getEndTime());
            System.out.println("持续时长【毫秒】: " + t.getDurationInMillis());
            System.out.println("--------------------------------------------------------");
        });
    }

}
