package com.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * 描述
 *
 * @author huihui.peng
 * @version V1.0
 * @date 2020/7/22
 */
public class SnowFlakeIdWorkerTest {

    private static SnowFlakeIdWorker snowFlakeIdWorker;

    @BeforeAll
    public static void init() {
        snowFlakeIdWorker = new SnowFlakeIdWorker(1, 1);
    }

    @Test
    public void test() {
        for (int i = 1; i <= 1 << 6; i++) {
            System.out.println(i + " > " + snowFlakeIdWorker.nextId());
        }
    }

}
