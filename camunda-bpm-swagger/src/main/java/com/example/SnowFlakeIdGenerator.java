package com.example;

import org.camunda.bpm.engine.impl.cfg.IdGenerator;

/**
 * 描述
 *
 * @author huihui.peng
 * @version V1.0
 * @date 2020/7/22
 */
public class SnowFlakeIdGenerator implements IdGenerator {

    private SnowFlakeIdWorker snowFlakeIdWorker;

    public SnowFlakeIdGenerator(SnowFlakeIdWorker snowFlakeIdWorker) {
        this.snowFlakeIdWorker = snowFlakeIdWorker;
    }

    @Override
    public String getNextId() {
        return String.valueOf(snowFlakeIdWorker.nextId());
    }

}
