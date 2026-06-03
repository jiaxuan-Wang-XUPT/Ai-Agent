package com.skye.aiagentbackend.controller;

import com.skye.aiagentbackend.agent.SkyeManus;
import com.skye.aiagentbackend.app.HealthApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private HealthApp healthApp;

    @Resource
    private ToolCallback[] allTools;

//    @Resource
//    private ChatMemory dashscopeChatMemory;

    @Resource
    private ChatModel dashscopeChatModel;

    /**
     * 同步调用AI healthApp应用
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/health_app/chat/sync")
    public String doChatWithHealthAppSync(String message, String chatId) {
        return healthApp.doChat(message, chatId);
    }

    /**
     * 第一种方式最方便（SSE流式接口）
     * SSE 流式调用AI app应用
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/health_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithHealthAppSSE(String message, String chatId) {
        return healthApp.doChatByStream(message, chatId);
    }

    /**
     *第二种方法（SSE流式接口）
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/health_app/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithHealthAppServerSentEvent(String message, String chatId) {
        return healthApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    /**
     * 第三种方法（SSE流式接口）
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/health_app/chat/sse/emitter")
    public SseEmitter doChatWithHealthAppSseEmitter(String message, String chatId) {
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter emitter = new SseEmitter(180000L); // 3分钟超时
        // 获取 Flux 数据流并直接订阅
        healthApp.doChatByStream(message, chatId)
                .subscribe(
                        // 处理每条消息
                        chunk -> {
                            try {
                                emitter.send(chunk);
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        // 处理错误
                        emitter::completeWithError,
                        // 处理完成
                        emitter::complete
                );
        // 返回emitter
        return emitter;
    }


    /**
     * 流式调用 Manus 超级智能体
     *
     * @param message
     * @return
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        //每次调用都要new一个实例，否则用户会同时调用同一个manus，遭不住
        SkyeManus skyeManus = new SkyeManus(allTools, dashscopeChatModel);
        return skyeManus.runStream(message);
    }



}
