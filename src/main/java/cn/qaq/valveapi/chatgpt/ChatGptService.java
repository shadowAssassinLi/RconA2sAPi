package cn.qaq.valveapi.chatgpt;

import cn.qaq.valveapi.chatgpt.util.OpenAiUtil;
import cn.qaq.valveapi.dao.ParamMapper;
import cn.qaq.valveapi.utils.ChatGpt3;
import cn.qaq.valveapi.vo.ParamVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
@Slf4j
public class ChatGptService {

    @Autowired
    private OpenAiUtil openAiUtil;

    @Resource
    private ParamMapper paramMapper;

    public String chatWithGPT(String words){
        ChatGpt3 chatGpt3 = new ChatGpt3();
        String result= "";
        ParamVo apiKeyParam = paramMapper.getParamByKeyAndCode("openai","apikey");
        try {
            if(null != apiKeyParam) {
                result = chatGpt3.chaGpt3Result(words, apiKeyParam.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        String chat1 = openAiUtil.chat(words);
//        System.out.println(chat1);
//        String s2 = openAiUtil.completionsV3(words);
//        System.out.println(s2);
//        return JSONUtils.toJSONString(completionChoices.stream().map(it->it.getText()).collect(Collectors.toList()));
        return result;
    }

}
