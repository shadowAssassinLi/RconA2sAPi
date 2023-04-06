package cn.qaq.valveapi.chatgpt.entity.completions;

import cn.qaq.valveapi.chatgpt.entity.common.Choice;
import cn.qaq.valveapi.chatgpt.entity.common.OpenAiResponse;
import cn.qaq.valveapi.chatgpt.entity.common.Usage;
import lombok.Data;

import java.io.Serializable;

/**
 * 描述： 答案类
 *
 * @author https:www.unfbx.com
 *  2023-02-11
 */
@Data
public class CompletionResponse extends OpenAiResponse implements Serializable {
    private String id;
    private String object;
    private long created;
    private String model;
    private Choice[] choices;
    private Usage usage;
}
