package cn.qaq.valveapi.utils;

import cn.qaq.valveapi.chatgpt.interceptor.OpenAILogger;
import cn.qaq.valveapi.chatgpt.interceptor.OpenAiResponseInterceptor;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ChatGpt3 {

//    @Value("${openai.secret_key}")
//    private  String apiKey = "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";

    private static final String model = "gpt-3.5-turbo";
    private static final int maxTokens = 3000;

    public String chaGptResource(String requestBody,String apiKey) throws Exception {
        // API endpoint URL
        SslUtils.ignoreSsl();
//        String apiEndpoint = "https://api.openai.com/v1/chat/completions";
        String apiEndpoint = "http://www.shengfirstnet.top:8000/v1/chat/completions";
        // Set up the API request
        URL url = new URL(apiEndpoint);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + apiKey);
        String response = "";
        try {
            con.setDoOutput(true);
            con.getOutputStream().write(requestBody.getBytes());
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response += inputLine;
            }
            in.close();
            // Print the response
        }catch (Exception e){
            log.error("Gpt响应异常:{}",e);
            response = "{\"choices\":[{\"message\":{\"role\":\"assistant\"," +
                    "\"content\":\"抱歉, 我不太清楚, 请换一个提问的方式!\"}}]}";
            return response;
        }
        log.info("gpt响应具体数据: {}",response);
        return response;
    }
    public String chaGpt3Result(String userQuery,String apiKey) throws Exception {
        String userId = "user";
        //字符串中多个空格替换成1个
        String blank = "\\s+";
        String userQuery1 = userQuery.replaceAll(blank, " ");
        ChatGpt3 chatGpt3 = new ChatGpt3();
        JsonResource jsonResource = new JsonResource();
        String jsonResource1 = jsonResource.requestBody(model,maxTokens,userId,userQuery1);
        String gptData = chatGpt3.chaGptResource(jsonResource1,apiKey);
        JSONObject jsonObject = JSONObject.parseObject(gptData);
        JSONArray results = jsonObject.getJSONArray("choices");
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode sourceData1 = objectMapper.readTree(dataEncrypt);
//        JsonNode bodyContent1 = sourceData1.path("bodyContent");
//        String targetData = "";
//        for (int n = 0; n < results.size(); n++) {
//            String textData = results.getJSONObject(n).getString("message");
//            JSONObject jsonMessage= JSONObject.parseObject(textData);
//            //插入响应gpt数据到缓存
//            Guava.GuavaDataSet(userId,textData);
//            String jsonContent = jsonMessage.getString("content");
//            String regex = "^.*\n\n?";
//            String regexextData  = jsonContent .replaceAll(regex, "");
//            String blank1 = "\n\n";
//            String regexextData1 = regexextData.replaceAll(blank1, "\n");
//            if (!bodyContent1.isMissingNode()) {
//                ((ObjectNode) sourceData1).put("bodyContent", regexextData1);
//            }
//
//            targetData = objectMapper.writeValueAsString(sourceData1);
//        }
        JSONObject jsonObject1 = JSONObject.parseObject(results.getJSONObject(0).getString("message"));
        return jsonObject1.getString("content");
    }
}
