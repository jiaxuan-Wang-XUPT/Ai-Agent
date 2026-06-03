package com.skye.aiagentbackend.app;

import com.skye.aiagentbackend.rag.QueryRewriter;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import com.skye.aiagentbackend.advisor.MyLoggerAdvisor;
import com.skye.aiagentbackend.chatmemory.FileBasedChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;


import java.util.List;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;


@Component
@Slf4j
public class HealthApp {

    @Resource
    private VectorStore healthVectorStore;

    @Resource
    private Advisor healthAppRagCloudAdvisor;

    @Resource
    private QueryRewriter queryRewriter;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ToolCallbackProvider toolCallbackProvider;


    //初始化对话模型
    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "你是一个专业的健康AI助手，可以科普医学知识、分析症状的可能原因、解读常规检查指标，并建议就医科室或识别紧急情况。" +
            "你绝对不能做出最终诊断、开具处方或推荐具体药物，也不能替代线下医生的诊疗。" +
            "每当用户描述症状时，先询问必要的关键信息（如持续时间、伴随症状等），并在回答中明确标注“本建议不构成诊断，请结合临床”。" +
            "遇到胸痛、呼吸困难、意识改变等紧急征象时，必须立即警告用户呼叫急救。请始终以严谨、负责、有同理心的方式交流。";

    //完成构造函数，初始化客户端对象
    public HealthApp(ChatModel dashscopeChatModel) {

        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";

        // 1. 创建底层文件存储仓库（存在文件中，重启服务依然存在）
        ChatMemoryRepository repository = new FileBasedChatMemory(fileDir);

        // 1. 创建底层内存存储仓库(临时存在内存的，重启服务就没有了)
//        ChatMemoryRepository repository = new InMemoryChatMemoryRepository();

        // 2. 使用滑动窗口记忆管理器创建 ChatMemory
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(5)
                .build();

        // 3. 使用建造者模式创建 MessageChatMemoryAdvisor
        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .conversationId("default")
                .build();

        // 4. 初始化对话客户端
        this.chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        memoryAdvisor
//                        new MyLoggerAdvisor()
                    //  new ReReadingAdvisor() 重读请求，按需开启，成本会加倍
                )
                .build();
    }

    /**
     * AI基础对话，支持多轮记忆
     * @param message`
     * @param chatId
     * @return
     */
    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CONVERSATION_ID, chatId))
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
//        log.info("content: {}", content); advisor已经实现了，这里不需要
        return content;
    }

    // 记录语法，可以快速定义变量
    record HealthReport(String title, List<String> suggestions) {

    }


    /**
     * 健康报告功能开发，演示实战结构化输出
     * @param message`
     * @param chatId
     * @return
     */
    public HealthReport doChatWithReport(String message, String chatId) {
        HealthReport healthReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成健康结果，标题为{用户名}的健康报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CONVERSATION_ID, chatId))
                .call()
                .entity(HealthReport.class);
        log.info("healthReport: {}", healthReport);
        return healthReport;
    }


    public String doChatWithRag(String message, String chatId) {

        // ===== 手动测试向量检索(仅测试，可删) =====
        List<Document> docs = healthVectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(message)
                        .topK(3)
                        .build()
        );

        log.info("RAG 检索结果数量: {}", docs.size());

        docs.forEach(doc -> {
            log.info("命中文档: {}", doc.getText());
        });

        //查询重写器
        String rewrittenMessage = queryRewriter.doQueryRewrite(message);

        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CONVERSATION_ID, chatId))
                .advisors(
                        new MyLoggerAdvisor(),
                      QuestionAnswerAdvisor.builder(healthVectorStore).build() //应用增强检索服务（本地知识库）
//                        healthAppRagCloudAdvisor //应用增强检索服务（云知识库）
                )
//                .advisors(HealthAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(
//                        healthVectorStore, "健康")) //应用增强检索服务（本地知识库）
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
//        log.info("content: {}", content);
        return content;
    }

    /**
     * 调用工具
     * @param message`
     * @param chatId
     * @return
     */
    public String doChatWithTools(String message, String chatId) {

        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CONVERSATION_ID, chatId))
                .advisors(new MyLoggerAdvisor())
                .toolCallbacks(allTools)
                .call()
                .chatResponse();
        log.info("allTools length: {}", allTools == null ? "null" : allTools.length);
        String content = response.getResult().getOutput().getText();
        return content;
    }

    /**
     * AI调用MCP  工具调用
     */
    public String doChatWithMcp(String message, String chatId) {

        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CONVERSATION_ID, chatId))
                .advisors(new MyLoggerAdvisor())
                .toolCallbacks(toolCallbackProvider)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        return content;
    }

    public Flux<String> doChatByStream(String message, String chatId) {
        return chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CONVERSATION_ID, chatId)
                        .param(VectorStoreChatMemoryAdvisor.TOP_K, 10))
                .stream()
                .content();
    }





}
