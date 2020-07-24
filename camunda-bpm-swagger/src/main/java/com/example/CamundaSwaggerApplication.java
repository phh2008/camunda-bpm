package com.example;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;

/**
 * 描述
 *
 * @author phh
 * @version V1.0
 * @date 2020/7/16
 */
@SpringBootApplication
@EnableProcessApplication
public class CamundaSwaggerApplication {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;

    public static void main(String[] args) {
        SpringApplication.run(CamundaSwaggerApplication.class, args);
    }


    @EventListener
    public void run(PostDeployEvent event) {
    }

}
