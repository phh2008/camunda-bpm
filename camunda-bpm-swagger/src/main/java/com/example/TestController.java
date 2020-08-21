package com.example;

import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 描述
 *
 * @author huihui.peng
 * @version V1.0
 * @date 2020/8/18
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;

    /**
     * 获取流程图xml文件
     *
     * @return
     * @throws IOException
     */
    @ResponseBody
    @GetMapping("/bpmn")
    public String getBpmn() throws IOException {
        String deploymentId = "483956166611832832";
        ProcessDefinition define = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deploymentId)
                .orderByVersionTag().asc()
                .list().get(0);
        InputStream is = repositoryService.getResourceAsStream(deploymentId, define.getResourceName());
        return IOUtils.toString(is);
    }

    /**
     * 根据流程实例ID获取流程图与活动节点
     *
     * @param processInstanceId 流程实例ID
     * @return
     * @throws IOException
     */
    @ResponseBody
    @GetMapping("/getBpmnByProcessInstanceId")
    public Map<String, Object> getBpmnByProcessInstanceId(String processInstanceId) throws IOException {
        /*ProcessInstance processInstance = this.runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();*/
        HistoricProcessInstance processInstance = this.historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        ProcessDefinition processDefinition = this.repositoryService
                .getProcessDefinition(processInstance.getProcessDefinitionId());
        InputStream is = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getResourceName());
        //获取流程图
        String bpmnXml = IOUtils.toString(is);
        System.out.println(bpmnXml);
        //获取流程实例经过的活动节点
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()//创建历史活动实例的查询
                .processInstanceId(processInstanceId)//
                .orderByHistoricActivityInstanceStartTime().asc()//
                .list();
        Set<String> actSets = list.stream()
                .map(HistoricActivityInstance::getActivityId)
                .filter(e->!e.endsWith("#multiInstanceBody"))
                .collect(Collectors.toSet());
        System.out.println("activity-id: " + actSets);
        //获取连线
        BpmnModelInstance instance = repositoryService.getBpmnModelInstance(processDefinition.getId());
        List<SequenceFlow> sequenceFlows = (List<SequenceFlow>) instance.getModelElementsByType(SequenceFlow.class);
        List<String> seqFlows = sequenceFlows.stream()
                .peek(e -> {
                    System.out.println("sequenceFlow: " + e.getId() + " : " + e.getName());
                })
                .filter(e -> actSets.contains(e.getTarget().getId()) && actSets.contains(e.getSource().getId()))
                .map(BaseElement::getId)
                .collect(Collectors.toList());
        System.out.println("sequenceFlow-id: " + seqFlows);
        Map<String, Object> result = new HashMap<>();
        actSets.addAll(seqFlows);
        result.put("bpmnXml", bpmnXml);
        result.put("nodes", actSets);
        return result;
    }

    /**
     * goto 流程图页面
     * 参考 bpmn-js:
     * https://github.com/bpmn-io/bpmn-js-examples
     *
     * @return
     */
    @GetMapping("/diagram/{procInstId}")
    public ModelAndView showDiagram(@PathVariable String procInstId, Model model) {
        model.addAttribute("procInstId", procInstId);
        return new ModelAndView("/diagram");
    }

}
