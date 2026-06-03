package com.skye.aiagentbackend.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于文件的 ChatMemory 实现
 */
public class FileBasedChatMemory implements ChatMemoryRepository {

    private final String BASE_DIR;

    private static final Kryo kryo = new Kryo();

    //动态注册序列化的类
    static {
        kryo.setRegistrationRequired(false);
        //设置实例化策略
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    //构造对象时指定文件保存目录
    public FileBasedChatMemory(String dir) {
        this.BASE_DIR = dir;
        File basePath  = new File(dir);
        if (!basePath.exists()) {
            basePath.mkdirs();
        }
    }

    /**
     * 返回所有会话ID列表
     * @return
     */
    @Override
    public List<String> findConversationIds() {
        List<String> conversationIds = new ArrayList<>();
        File dir = new File(BASE_DIR);
        if (dir.exists() && dir.isDirectory()) {
            // 只筛选 .kryo 结尾的文件
            File[] files = dir.listFiles((d, name) -> name.endsWith(".kryo"));
            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    // 去掉后缀 ".kryo" 得到 conversationId
                    String conversationId = fileName.substring(0, fileName.length() - 5);
                    conversationIds.add(conversationId);
                }
            }
        }
        return conversationIds;
    }

    /**
     * 根据会话ID获取会话内容
     * @param conversationId
     * @return
     */
    @Override
    public List<Message> findByConversationId(String conversationId) {
        // 复用已有的 getOrCreateConversation 方法，文件不存在时返回空列表
        return getOrCreateConversation(conversationId);
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        List<Message> messageList = getOrCreateConversation(conversationId);
        messageList.addAll(messages);
        saveConversation(conversationId, messageList);
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        File file = getConversationFile(conversationId);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 根据会话id获取会话内容，如果不存在则创建一个空的会话内容
     * @param conversationId
     * @return
     */
    private List<Message> getOrCreateConversation(String conversationId) {
        File file = getConversationFile(conversationId);
        List<Message> messages = new ArrayList<>();
        if (file.exists()) {
            try (Input input = new Input(new FileInputStream(file))) {
                messages = kryo.readObject(input, ArrayList.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return messages;
    }

    /**
     * 保存会话内容
     * @param conversationId
     * @param messages
     */
    private void saveConversation(String conversationId, List<Message> messages) {
        File file = getConversationFile(conversationId);
        try (Output output = new Output(new FileOutputStream(file))) {
            kryo.writeObject(output, messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 每个会话文件单独保存，
     * @param conversationId
     * @return
     */
    private File getConversationFile(String conversationId) {
        return new File(BASE_DIR + "/" + conversationId + ".kryo");
    }


}

