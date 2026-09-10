package com.preppilot.ai;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GeminiService {

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public GeminiService(
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model}") String model) {

        this.apiKey = apiKey;
        this.model = model;

        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
    }

    // =========================================================
    // SUMMARY GENERATION
    // =========================================================

    public String generateSummary(String text) {

        String prompt = """
                You are an AI study assistant.

                Summarize the following study material for a college student.

                Requirements:
                - Use simple and clear language.
                - Focus on important concepts.
                - Use headings and bullet points where useful.
                - Do not add information that is not present in the study material.

                Study material:
                """ + text;

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[] {
                        Map.of(
                                "parts", new Object[] {
                                        Map.of("text", prompt)
                                }
                        )
                }
        );

        Map response = restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/{model}:generateContent")
                        .queryParam("key", apiKey)
                        .build(model))
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        return extractGeneratedText(response);
    }

    // =========================================================
    // QUIZ GENERATION
    // =========================================================

    public String generateQuiz(String text) {

        String prompt = """
                You are an AI quiz generator for college students.

                Based ONLY on the study material provided below,
                generate exactly 5 multiple-choice questions.

                For each question provide:

                Question:
                Option A:
                Option B:
                Option C:
                Option D:
                Correct Answer:

                Requirements:
                - Questions must be based only on the study material.
                - Do not add outside information.
                - Each question must have exactly four options.
                - The correct answer must be one of A, B, C, or D.
                - Make questions useful for exam preparation.
                - Avoid duplicate questions.
                - Use clear and simple language.

                Study material:
                """ + text;

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[] {
                        Map.of(
                                "parts", new Object[] {
                                        Map.of("text", prompt)
                                }
                        )
                }
        );

        Map response = restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/{model}:generateContent")
                        .queryParam("key", apiKey)
                        .build(model))
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        return extractGeneratedText(response);
    }

    // =========================================================
    // COMMON GEMINI RESPONSE PARSER
    // =========================================================

    @SuppressWarnings("unchecked")
    private String extractGeneratedText(Map response) {

        var candidates =
                (java.util.List<Map<String, Object>>)
                        response.get("candidates");

        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("Gemini returned no response");
        }

        Map<String, Object> firstCandidate =
                candidates.get(0);

        Map<String, Object> content =
                (Map<String, Object>)
                        firstCandidate.get("content");

        if (content == null) {
            throw new RuntimeException(
                    "Gemini response content is missing");
        }

        var parts =
                (java.util.List<Map<String, Object>>)
                        content.get("parts");

        if (parts == null || parts.isEmpty()) {
            throw new RuntimeException(
                    "Gemini returned no generated text");
        }

        return (String) parts.get(0).get("text");
    }
}