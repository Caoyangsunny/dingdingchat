package com.kaifamiao.dingding;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class DingTalkService {

    @Value("${dingtalk.webhook}")
    private String webhook;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendMessage(String message) throws IOException {
        Map<String, Object> payload = new HashMap<>();
        payload.put("msgtype", "text");

        Map<String, String> text = new HashMap<>();
        text.put("content", message);
        payload.put("text", text);

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                objectMapper.writeValueAsString(payload));

        Request request = new Request.Builder()
                .url(webhook)
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
        }
    }
}
