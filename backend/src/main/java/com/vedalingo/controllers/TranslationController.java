package com.vedalingo.controllers;

import com.vedalingo.dtos.TranslationRequestDto;
import com.vedalingo.models.translation.ChatCompletionRequest;
import com.vedalingo.models.translation.ChatCompletionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class TranslationController {


    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/translate")
    public ResponseEntity<String> getOpenaiResponse(@RequestParam String file, @RequestParam String targetLanguage) {
        System.out.println("request received :"+file+", "+targetLanguage);
        try {
            String prompt = file + " translate into " + targetLanguage + " only translated text shows as output";
            ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest("gpt-3.5-turbo", prompt);
            ChatCompletionResponse response = restTemplate.postForObject("https://api.openai.com/v1/chat/completions", chatCompletionRequest, ChatCompletionResponse.class);

            String translatedText = response.getChoices().get(0).getMessage().getContent();
            return ResponseEntity.ok(translatedText);
        } catch (HttpClientErrorException.Unauthorized unauthorized) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access to OpenAI API.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while processing OpenAI API request.");
        }
    }


}
