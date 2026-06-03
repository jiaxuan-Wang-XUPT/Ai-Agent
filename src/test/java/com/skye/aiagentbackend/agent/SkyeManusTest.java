package com.skye.aiagentbackend.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SkyeManusTest {
    @Resource
    private SkyeManus skyeManus;

    @Test
    void run() {
        //要测试的话检查是sse还是stdio模式，sse模式要手动启动一下
        String userPrompt = """
                我在洛阳市火车站附件，帮我找到5公里内最近的医院，
                并结合一些实地图片，制定一份详细看病指南，
                并以PDF格式输出
                """;
        String answer = skyeManus.run(userPrompt);
        Assertions.assertNotNull(answer);
    }
}