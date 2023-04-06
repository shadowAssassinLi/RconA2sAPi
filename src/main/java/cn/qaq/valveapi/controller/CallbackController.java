package cn.qaq.valveapi.controller;

import cn.qaq.valveapi.utils.*;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class CallbackController {
    private static final String model = "gpt-3.5-turbo";
    @Value("${openai.secret_key}")
    private  String apiKey ;
    private static final int maxTokens = 3000;
//    @RequestMapping("/gptdata")
//    public String sourcedata(@RequestBody Map body) throws Exception {
//        try {
//            String dataEncrypt = JSON.toJSONString(body);
//            JSONObject jsonObject1 = JSONObject.parseObject(dataEncrypt);
//            String sourceBodyContent = jsonObject1.getString("bodyContent");
//            String userData = jsonObject1.getString("user_data");
//            //获取包含"暂未关联到可用方案"数据信息.接入gpt进行问答
//            boolean otherData = sourceBodyContent.contains("暂未关联到可用方案");
//            if ( otherData ) {
//                //GPT3.5使用方式
//                ChatGpt3 chatGpt3= new ChatGpt3();
//                String targetData = chatGpt3.chaGpt3Result(userData,dataEncrypt);
//                //达芬奇003模型调用
////                ChatGptDavinci chatGptDavinci = new ChatGptDavinci();
////                String targetData = chatGptDavinci.chaGptResult(userData,apiKey,model,maxTokens,dataEncrypt);
//                log.info("gpt响应内容:{}",targetData);
//                return targetData;
//
//            }else {
//                log.info("gpt响应内容:{}",dataEncrypt);
//                return dataEncrypt;
//            }
//        }catch (Exception e){
//            log.error("处理异常:{}",e);
//        }
//        return "响应异常！";
//    }

}

