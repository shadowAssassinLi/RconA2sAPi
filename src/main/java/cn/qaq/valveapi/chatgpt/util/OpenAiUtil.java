package cn.qaq.valveapi.chatgpt.util;

import cn.qaq.valveapi.chatgpt.OpenAiClient;
import cn.qaq.valveapi.chatgpt.entity.chat.ChatCompletion;
import cn.qaq.valveapi.chatgpt.entity.chat.ChatCompletionResponse;
import cn.qaq.valveapi.chatgpt.entity.chat.Message;
import cn.qaq.valveapi.chatgpt.entity.completions.Completion;
import cn.qaq.valveapi.chatgpt.entity.completions.CompletionResponse;
import cn.qaq.valveapi.chatgpt.interceptor.OpenAILogger;
import cn.qaq.valveapi.chatgpt.interceptor.OpenAiResponseInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component
public class OpenAiUtil {

    @Value("${openai.secret_key}")
    private String token;

    private OpenAiClient v2;

    @PostConstruct
    public void init(){
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("www.shengfirstnet.top", 443));
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .proxy(proxy)
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new OpenAiResponseInterceptor())
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        v2= OpenAiClient.builder()
                //支持多key传入，请求时候随机选择
                .apiKey(Arrays.asList(token))
                .okHttpClient(okHttpClient)
                //自己做了代理就传代理地址，没有可不不传
                .apiHost("https://www.shengfirstnet.top/")
                .build();
    }

    public String chat(String words) {
        //聊天模型：gpt-3.5
        Message message = Message.builder().role(Message.Role.USER).content(words).build();
        ChatCompletion chatCompletion = ChatCompletion.builder().messages(Arrays.asList(message)).build();
        ChatCompletionResponse chatCompletionResponse = v2.chatCompletion(chatCompletion);
        StringBuilder sb = new StringBuilder();
        chatCompletionResponse.getChoices().forEach(e -> {
            sb.append(e.getMessage());
        });
        return sb.toString();
    }


    public String completionsV3(String words) {
        Completion q = Completion.builder()
                .prompt(words)
                .stop(Arrays.asList(" Human:", " Bot:"))

                .echo(true)
                .build();
        CompletionResponse completions = v2.completions(q);
        String text = completions.getChoices()[0].getText();
        return text;
    }
}
