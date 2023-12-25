package com.kaifamiao.dingding_chatgpt2;

import com.kaifamiao.dingding.ChatGPTService;
import com.kaifamiao.dingding.DingTalkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@ComponentScan(basePackages = {"com.kaifamiao.dingding"})
@SpringBootApplication
@RestController
public class DingdingChatgptApplication {

    @Autowired
    private DingTalkService dingTalkService;

    @Autowired
    private ChatGPTService chatGPTService;

    public static void main(String[] args) {
        SpringApplication.run(DingdingChatgptApplication.class, args);
    }
    @PostMapping("/webhook")
    public void handleWebhook(@RequestBody String payload) throws IOException {

        String message = "你的问题：" + payload;
        String chatGPTResponse = chatGPTService.generateResponse("123",payload);
//        dingTalkService.sendMessage(message + "\nChatGPT 回复：" + chatGPTResponse);
        dingTalkService.sendMessage(chatGPTResponse);

    }
}
