package com.vedalingo.controllers;


import com.vedalingo.models.translation.ChatCompletionRequest;
import com.vedalingo.models.translation.ChatCompletionResponse;
import com.vedalingo.services.impl.ImageTextExtractor;
import com.vedalingo.services.impl.PdfTextExtractorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/extract")
public class TextExtractorController {
    @Autowired
    private ImageTextExtractor imageTextExtractor;

    @Autowired
    private PdfTextExtractorService pdfTextExtractorService;

    @Autowired
    private RestTemplate restTemplate;


    @PostMapping("/image")
    public ResponseEntity<String> extractTextFromImage(@RequestParam MultipartFile file, @RequestParam String targetLanguage) {

        System.out.println("request ---");

        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("Image file is empty");
            }
            byte[] imageData = file.getBytes();
            String extractedText = imageTextExtractor.extractTextFromImage(imageData);
            // Check if text extraction was successful
            if (extractedText != null && !extractedText.isEmpty()) {
                // call translation api
                System.out.println(extractedText);
//                return ResponseEntity.ok(extractedText);
                String translatedText=translateFile(extractedText,targetLanguage);
                return ResponseEntity.ok(translatedText);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to extract text from the image");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process the image file");
        }
    }

    @PostMapping("/pdf")
    public ResponseEntity<List<String>> extractTextFromPDF(@RequestParam MultipartFile file, @RequestParam String targetLanguage) {

        System.out.println("request--------------");
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            InputStream inputStream = file.getInputStream();
            List<String> extractedText = pdfTextExtractorService.extractText(inputStream);

//            return ResponseEntity.ok().body(extractedText);
            //call translation api

            List<String> transTexts=new ArrayList<>();

            for (int i=0;i<extractedText.size();i++) {

                System.out.println(i+"extracted text : "+extractedText.get(i));
                String translatedText=translateFile(extractedText.get(i),targetLanguage);
                transTexts.add(translatedText);
            }

            return ResponseEntity.ok().body(transTexts);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private String translateFile(String text, String targetLanguage){
        try {
            String prompt = text + " translate into " + targetLanguage + " only translated text shows as output";
            System.out.println(prompt);
            ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest("gpt-3.5-turbo", prompt);
            ChatCompletionResponse response = restTemplate.postForObject("https://api.openai.com/v1/chat/completions", chatCompletionRequest, ChatCompletionResponse.class);
            String translatedText = response.getChoices().get(0).getMessage().getContent();
            return translatedText;
        }catch (Exception e){
            return "Error while processing OpenAI API request";
        }
    }
}

