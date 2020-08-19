package com.example;

import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.repository.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
        if (define instanceof ProcessDefinitionEntity) {
            System.out.println((ProcessDefinitionEntity) define);
        }
        List<Resource> res = repositoryService.getDeploymentResources(deploymentId);
        System.out.println(res);
        //InputStream is = repositoryService.getResourceAsStreamById(deploymentId, res.get(0).getId());
        InputStream is = repositoryService.getResourceAsStream(deploymentId, define.getResourceName());
        return IOUtils.toString(is);
    }

    /**
     * goto 流程图页面
     * 参考 bpmn-js:
     * https://github.com/bpmn-io/bpmn-js-examples
     *
     * @return
     */
    @GetMapping("/diagram")
    public ModelAndView showDiagram() {

        return new ModelAndView("/diagram");
    }

}
