package com.kaifamiao.dingding;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class ChatGPTService {

    @Value("${chatgpt.api_key}")
    private String apiKey;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, List<Map<String, String>>> sessionHistory = new HashMap<>();

    public String generateResponse(String sessionId, String userMessage) throws IOException {
        String url = "https://api.openai.com/v1/chat/completions";

        // 获取或初始化会话历史
        List<Map<String, String>> messages = sessionHistory.getOrDefault(sessionId, new ArrayList<>(Arrays.asList(
                new HashMap<String, String>() {{
                    put("role", "system");
                    put("content", "You are a weight loss coach.");
                }}
        )));
        System.out.println(messages);
        // 添加新的用户消息到会话历史
        messages.add(new HashMap<String, String>() {{
            put("role", "user");
            put("content", userMessage);
        }});

        // 构建请求payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("messages", messages);
        payload.put("max_tokens", 1000 );
        payload.put("model", "gpt-3.5-turbo");
        payload.put("temperature", 0.9);

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                objectMapper.writeValueAsString(payload));

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            Map<?, ?> responseJson = objectMapper.readValue(responseBody, Map.class);
            Map<String, Object> choice = (Map<String, Object>) (((List<?>) responseJson.get("choices")).get(0));
            Map<String, String> message = (Map<String, String>) choice.get("message");

            // 更新会话历史
            messages.add(message);
            sessionHistory.put(sessionId, messages);

            return message.get("content");
        }
    }

}

