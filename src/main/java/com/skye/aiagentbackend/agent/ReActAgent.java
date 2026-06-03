package com.skye.aiagentbackend.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ReAct（Reasoning and Acting ）模式的代理抽象类
 * 实现了思考-行动的循环模式
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class ReActAgent extends BaseAgent {
    /**
     * 思考,处理当前状态并决定下一步行动
     * @return 是否需要继续行动，ture表示继续行动，false表示停止行动
     */
    public abstract boolean think();

    /**
     * 执行决定的行动
     */
    public abstract String act();

    @Override
    public String step() {
        try {
            boolean shouldAct = think();
            if (!shouldAct) {
                return "思考完成-无需行动";
            }
            return act();
        } catch (Exception e) {
            return "思考失败：" + e.getMessage();
        }
    }


}
