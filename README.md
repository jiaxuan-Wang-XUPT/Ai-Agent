# 🧠 AI Agent – 智能健康问答 + 自主规划智能体

基于 Spring AI Alibaba 框架构建的智能体应用，包含两大核心能力：  
👉 **AI 健康问答应用**：依赖大模型解决用户健康问题，支持多轮对话、自定义知识库 RAG、工具调用与 MCP 服务编排。  
👉 **SkyeManus 超级智能体**：自主感知-规划-执行（ReAct 模式）的 Manus 类智能体，可调用网页搜索、资源下载与 PDF 生成，完成复杂任务如制定完整约会计划并导出文档。
<img width="1909" height="945" alt="dfebf3740b33cca5a6c379ba1b735bb0" src="https://github.com/user-attachments/assets/e9634d18-9b93-4ed6-b449-a633ca64382b" />

## ✨ 核心特性

### 🩺 AI 健康问答应用
<img width="1913" height="945" alt="b267f9be18cd56f2be7fd6aeef0f1fd0" src="https://github.com/user-attachments/assets/5877aed5-ce88-4e6b-bb11-71eb1c4af8bf" />


- 💬 **多轮对话**：基于记忆（Kryo 序列化持久化）保持上下文连贯性  
- 📚 **RAG 知识库**：结合 Markdown/HTML 文档加载器、向量检索（PgVector + 云数据库）  
- 🔧 **Tool Calling**：调用外部工具（如地图服务）获取附近地点，智能生成治疗/出行计划  
- 🌐 **MCP 服务集成**：自定义 MCP Server/Client，打通模型与第三方服务  
- 📄 **结构化输出**：利用 JsonSchema Generator 生成规范响应，支持 PDF 导出（iText）

### 🤖 SkyeManus 超级智能体
<img width="1912" height="945" alt="f62c5e6f8b16dca603bf9892249a3c63" src="https://github.com/user-attachments/assets/c48e7781-cb30-469a-9569-672a211f5eed" />

- 🧠 **ReAct 自主规划**：Observation → Thought → Action 循环，动态拆解任务目标  
- 🔍 **网页搜索**：集成搜索引擎 + Jsoup 解析，获取实时信息  
- 📥 **资源下载**：自动抓取文档、图片等资源并存储本地  
- 📑 **智能文档生成**：基于检索内容与大模型，生成完整计划（如约会行程）并导出为 PDF  
- 🧩 **模块化工具**：所有能力均可通过 Tool Calling 或 MCP 协议暴露给智能体

## 🛠️ 技术栈

| 类别            | 技术                                                          |
|----------------|---------------------------------------------------------------|
| 基础框架        | Spring Boot 3.3.0, Java 21, Maven                            |
| AI 大模型接入   | 阿里云百炼 DashScope (通义千问), Spring AI Alibaba Agent Framework |
| 4 种接入方式    | SDK 直连、Spring AI Starter、ReAct Agent、MCP Client/Server  |
| 开发框架        | Spring AI + LangChain4j（对比实践）                           |
| 本地部署        | 支持大模型本地部署（如 Ollama）                              |
| Prompt 工程     | 结构化提示、Few-shot、Chain-of-Thought                       |
| 对话记忆        | Kryo 序列化 + 文件持久化                                      |
| RAG 实战        | 文档加载器（Markdown, HTML, JSoup）、向量存储（PgVector）、相似度检索优化 |
| 向量数据库      | PgVector（云数据库服务）                                     |
| Tool Calling    | 自定义 Function Calling，原理封装                             |
| MCP 协议        | Spring AI MCP Starter，实现 MCP Server 与 Client               |
| 结构化输出      | victools jsonschema-generator + Spring AI 输出转换器          |
| 文档处理        | iText PDF 生成, Jsoup HTML 解析                               |
| 部署            | Serverless（阿里云函数计算 / 容器化）                         |
| 其他概念实践    | 多模态、智能体工作流、A2A 协议、大模型评估                    |

## 🚀 快速开始

### 环境要求
- JDK 21
- Maven 3.8+
- 阿里云 DashScope API Key（或本地模型端点）

### 配置步骤

1. **克隆仓库**
```bash
git clone https://github.com/yourname/ai-agent.git
cd ai-agent
