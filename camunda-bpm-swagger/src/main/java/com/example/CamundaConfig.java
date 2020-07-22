package com.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述
 *
 * @author huihui.peng
 * @version V1.0
 * @date 2020/7/22
 */
@Configuration
public class CamundaConfig {

    @Bean
    public SnowFlakeIdWorker snowFlakeIdWorker() {
        return new SnowFlakeIdWorker(1, 1);
    }


    @Bean
    public SnowFlakeIdGenerator snowFlakeIdGenerator(SnowFlakeIdWorker snowFlakeIdWorker) {
        return new SnowFlakeIdGenerator(snowFlakeIdWorker);
    }

}
