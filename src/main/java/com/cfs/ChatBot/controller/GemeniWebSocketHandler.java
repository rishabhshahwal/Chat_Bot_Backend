package com.cfs.ChatBot.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import org.springframework.http.HttpHeaders;

@Component
public class GemeniWebSocketHandler extends TextWebSocketHandler {

     @Value("${gemini.api.key}")
     private String apikey;

     private final String GEMINI_URL="https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=";

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String userInput = message.getPayload().trim().toLowerCase();
        System.out.println("  original User input: " + userInput);

        String response = callGeminiForAnswer(userInput);

        session.sendMessage(new TextMessage(response));
    }

 private String callGeminiForAnswer(String userInput){
        try{
            RestTemplate restTemplate = new RestTemplate();

            String jsonPayLoad = "{\"contents\": [{\"parts\": [{\"text\": \"" + userInput+ "\"  }]}]}";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(jsonPayLoad,headers);

            String url = GEMINI_URL +apikey;

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST,entity,String.class);

            System.out.println(" Sent JSON to Gemini: " + jsonPayLoad);
            System.out.println(" Raw Response from Gemini: " + response.getBody());

            return response.getBody();
        }catch (Exception e){
            System.out.println(" API call Failed: " + e.getMessage());
            return "{\"error\":\"API call Failed\"}";
        }
 }
}
