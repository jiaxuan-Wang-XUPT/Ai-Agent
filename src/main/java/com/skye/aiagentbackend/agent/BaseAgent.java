package com.skye.aiagentbackend.agent;

import com.itextpdf.styledxmlparser.jsoup.internal.StringUtil;
import com.skye.aiagentbackend.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 抽象基础代理类 用于管理代理状态和执行流程
 *
 * 提供状态转化、内存管理和基于步骤的执行循环的基础功能
 * 子类必须实现step方法（因为抽象）
 *
 * 1.包含chatClient属性，由调用方传入具体大模型对象，而不是写死使用的大模型
 * 2.包含messageList属性，用于记录消息上下文
 * 3.包含state属性，用于记录代理状态
 */
@Data
@Slf4j
public abstract class BaseAgent {

    //核心属性
    private String name;

    //prompt
    private String systemPrompt;
    private String nextStepPrompt; //ai完成后，引导下一步继续的提示词

    //状态
    private AgentState state = AgentState.IDLE;

    //执行控制
    private int maxSteps = 5; //最大执行步骤
    private int currentStep = 0; //当前执行步骤

    //LLM
    private ChatClient chatClient;

    //memory
    private List<Message> messageList = new ArrayList<>();

    /**
     * 运行代理
     * @param userPrompt 用户输入
     * @return 代理输出
     */
    public String run(String userPrompt) {
        if (this.state != AgentState.IDLE) {
            throw new RuntimeException("Agent is not idle");
        }
        if (userPrompt == null || userPrompt.isEmpty()) {
            throw new RuntimeException("User prompt is empty");
        }
        //update state
        state = AgentState.RUNNING;
        //记录消息上下文
        messageList.add(new UserMessage(userPrompt));
        //结果保存列表
        List<String> results = new ArrayList<>();

        try {
            for (int i = 0; i < maxSteps && state!=AgentState.FINISHED; i++) {
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("Step {} of {}", stepNumber, maxSteps);
                //单步执行
                String stepResult = step();
                String result = "step " + stepNumber + ": " + stepResult;
                results.add(result);
            }
            //检查是否超出步骤限制
            if (currentStep >= maxSteps) {
                state = AgentState.FINISHED;
                results.add("Max steps reached");
            }
            return String.join("\n", results);
        } catch (Exception e) {
            state = AgentState.ERROR;
            log.error("Agent error", e);
            return "Agent error: " + e.getMessage();
        } finally {
            this.cleanup(); //清理资源
        }
    }

    /**
     * 运行代理（流式输出）
     *
     * @param userPrompt 用户提示词
     * @return SseEmitter实例
     */
    public SseEmitter runStream(String userPrompt) {
        // 创建SseEmitter，设置较长的超时时间
        SseEmitter emitter = new SseEmitter(300000L); // 5分钟超时

        // 使用线程异步处理，避免阻塞主线程
        CompletableFuture.runAsync(() -> {
            try {
                if (this.state != AgentState.IDLE) {
                    emitter.send("错误：无法从状态运行代理: " + this.state);
                    emitter.complete();
                    return;
                }
                if (StringUtil.isBlank(userPrompt)) {
                    emitter.send("错误：不能使用空提示词运行代理");
                    emitter.complete();
                    return;
                }

                // 更改状态
                state = AgentState.RUNNING;
                // 记录消息上下文
                messageList.add(new UserMessage(userPrompt));

                try {
                    for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                        int stepNumber = i + 1;
                        currentStep = stepNumber;
                        log.info("Executing step " + stepNumber + "/" + maxSteps);

                        // 单步执行
                        String stepResult = step();
                        String result = "Step " + stepNumber + ": " + stepResult;

                        // 发送每一步的结果
                        emitter.send(result);
                    }
                    // 检查是否超出步骤限制
                    if (currentStep >= maxSteps) {
                        state = AgentState.FINISHED;
                        emitter.send("执行结束: 达到最大步骤 (" + maxSteps + ")");
                    }
                    // 正常完成
                    emitter.complete();
                } catch (Exception e) {
                    state = AgentState.ERROR;
                    log.error("执行智能体失败", e);
                    try {
                        emitter.send("执行错误: " + e.getMessage());
                        emitter.complete();
                    } catch (Exception ex) {
                        emitter.completeWithError(ex);
                    }
                } finally {
                    // 清理资源
                    this.cleanup();
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        // 设置超时和完成回调
        emitter.onTimeout(() -> {
            this.state = AgentState.ERROR;
            this.cleanup();
            log.warn("SSE connection timed out");
        });

        emitter.onCompletion(() -> {
            if (this.state == AgentState.RUNNING) {
                this.state = AgentState.FINISHED;
            }
            this.cleanup();
            log.info("SSE connection completed");
        });

        return emitter;
    }


    /**
     * 执行单个步骤
     * @return
     */
    public abstract String step();

    protected void cleanup() {
        //子类可以重写此方法清理资源
    }

}
