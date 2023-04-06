package cn.qaq.valveapi.chatgpt.entity.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

/**
 * 描述：
 *
 * @author https:www.unfbx.com
 * @since 2023-03-02
 */
@Data
public class Message implements Serializable {

    /**
     * 目前支持三中角色参考官网，进行情景输入：https://platform.openai.com/docs/guides/chat/introduction
     */
    private String role;

    private String content;

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 构造函数
     *
     * @param role
     * @param content 描述主题信息
     */
    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public Message() {
    }

    private Message(Builder builder) {
        setRole(builder.role);
        setContent(builder.content);
    }


    @Getter
    @AllArgsConstructor
    public enum Role {

        SYSTEM("system"),
        USER("user"),
        ASSISTANT("assistant"),
        ;
        private String name;
    }

    public static final class Builder {
        private
        String role;
        private
        String content;

        public Builder() {
        }

        public Builder role( Role role) {
            this.role = role.getName();
            return this;
        }

        public Builder content( String content) {
            this.content = content;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }
}
