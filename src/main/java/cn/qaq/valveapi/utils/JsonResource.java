package cn.qaq.valveapi.utils;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonResource {

    public static  String requestBody(String model, int maxTokens, String user, String prompt) throws JsonProcessingException {
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("model",model);
        reqMap.put("max_tokens",maxTokens);
        List<Map<String, Object>> messagesList = new ArrayList<>();
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("role", "user");
        messageMap.put("content", prompt);
        ObjectMapper objectMapper = new ObjectMapper();
        if (Guava.GuavaDataGet(user) != null ){
            String GuavaDataGetData = Guava.GuavaDataGet(user);
            Map<String, Object> GuavaDataGetDataMap = objectMapper.readValue(GuavaDataGetData, Map.class);
            messagesList.add(GuavaDataGetDataMap);
        }
        messagesList.add(messageMap);
        String messageJson =  objectMapper.writeValueAsString(messageMap);
        log.info("插入缓存"+ messageJson);
        Guava.GuavaDataSet(user,messageJson);
        reqMap.put("messages", messagesList);
        String requestBody = JSON.toJSONString(reqMap);
        return requestBody;
    }

}


