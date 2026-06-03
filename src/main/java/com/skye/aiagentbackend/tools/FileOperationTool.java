package com.skye.aiagentbackend.tools;

import cn.hutool.core.io.FileUtil;
import com.skye.aiagentbackend.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 文件操作工具类（实现文件的读写）
 * 让 AI 能够读写服务器上指定目录中的文件
 * 举例：
 * 用户要求保存信息
 * 例如：“帮我记录下明天的会议要点” → AI 调用 writeFile 将内容写入一个 .txt 或 .md 文件。
 * 用户要求读取已有文件
 * 例如：“显示我上次保存的笔记” → AI 调用 readFile 读取对应文件内容并返回给用户
 */
public class FileOperationTool {

    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";

    @Tool(description = "Read content from a file")
    public String readFile(@ToolParam(description = "Name of the file to read") String fileName) {
        String filePath = FILE_DIR + "/" + fileName;
        try {
            return FileUtil.readUtf8String(filePath);
        } catch (Exception e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    @Tool(description = "Write content to a file")
    public String writeFile(
            @ToolParam(description = "Name of the file to write") String fileName,
            @ToolParam(description = "Content to write to the file") String content) {
        String filePath = FILE_DIR + "/" + fileName;
        try {
            // 创建目录
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(content, filePath);
            return "File written successfully to: " + filePath;
        } catch (Exception e) {
            return "Error writing to file: " + e.getMessage();
        }
    }
}

