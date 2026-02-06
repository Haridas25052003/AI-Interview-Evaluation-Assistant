package com.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatGptServiceImpl implements ChatGptService{

	@Value("${openai.api.key}")
	private String apiKey;
	
	private final String OPENAI_URL="https://api.openai.com/v1/chat/completions";

	@Override
	public String generateQuestion(String language, String level) {
		
		RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String prompt =
                "Generate one interview question for a " +
                level + " level " + language + " candidate.";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", prompt));
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(OPENAI_URL, entity, Map.class);

        Map<String, Object> responseBody = response.getBody();

        if (responseBody == null) {
            return "No question generated";
        }

        List<Map<String, Object>> choices =
                (List<Map<String, Object>>) responseBody.get("choices");

        Map<String, Object> message =
                (Map<String, Object>) choices.get(0).get("message");

        return message.get("content").toString();
		
	}
}
