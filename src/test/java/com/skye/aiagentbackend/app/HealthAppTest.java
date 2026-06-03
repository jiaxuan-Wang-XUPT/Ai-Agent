package com.skye.aiagentbackend.app;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HealthAppTest {

    @Resource
    private HealthApp healthApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        //第一轮
        String message = "你好，我是skye";
        String answer = healthApp.doChat(message, chatId);
        Assertions.assertNotNull(answer); //断言不为空
        //第二轮
        message = "我经常感冒怎么办";
        answer = healthApp.doChat(message, chatId);
        Assertions.assertNotNull(answer); //断言不为空
        //第三轮
        message = "我刚和你说我怎么了？我是谁来着？";
        answer = healthApp.doChat(message, chatId);
        Assertions.assertNotNull(answer); //断言不为空
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是skye，我想知道我为什么爱出汗";
        HealthApp.HealthReport healthReport = healthApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(healthReport); //断言不为空
    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "每天走一万步能预防心血管疾病吗？研究建议的步数是多少？";
        String answer =  healthApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        testMessage("鼻炎怎么缓解？");

        // 测试网页抓取：恋爱案例分析
        testMessage("在http://www.ahetyy.com/online_message_1552.html看看如何治疗发烧");

        // 测试资源下载：图片下载
        testMessage("直接下载一张适合做手机壁纸的星空情侣图片为文件");

        // 测试终端操作：执行代码
        testMessage("执行 Python3 脚本来生成数据分析报告");

        // 测试文件操作：保存用户档案
        testMessage("保存我的病例档案为文件");

        // 测试 PDF 生成
        testMessage("生成一份‘预防生病锻炼计划’PDF，包含如何预防，如何锻炼，锻炼时长");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = healthApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();
//        // 测试地图 MCP
//        String message = "我住在河南省洛阳市白马寺，帮我找到附件5公里有吃烩面的地点";
//        String answer =  healthApp.doChatWithMcp(message, chatId);
//        Assertions.assertNotNull(answer);
        //测试search MCP （自己搭建的）
        String message = "帮我搜索一些小猫图片";
        String answer =  healthApp.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(answer);
    }


}