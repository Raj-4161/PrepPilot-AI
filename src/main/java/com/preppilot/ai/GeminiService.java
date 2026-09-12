package com.preppilot.ai;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GeminiService {

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    /*
     * Maximum text sent to Gemini in one request.
     */
    private static final int MAX_TEXT_LENGTH = 30000;

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

        String safeText = prepareText(text);

        String prompt = """
                You are an AI study assistant.

                Create a clear and useful study summary from the
                following educational material.

                Rules:
                - Use only the provided material.
                - Do not add unrelated information.
                - Explain important concepts clearly.
                - Use headings and bullet points where useful.
                - Keep the summary suitable for college students.

                Study Material:

                %s
                """.formatted(safeText);

        return generateContent(prompt);
    }


    // =========================================================
    // QUIZ GENERATION
    // =========================================================

    public String generateQuiz(String text) {

        String safeText = prepareText(text);

        String prompt = """
                You are an AI quiz generator for students.

                Generate exactly 5 multiple-choice questions
                from the provided study material.

                Follow this exact format:

                Question 1:
                Question: <question>
                Option A: <option>
                Option B: <option>
                Option C: <option>
                Option D: <option>
                Correct Answer: <A/B/C/D>

                Question 2:
                Question: <question>
                Option A: <option>
                Option B: <option>
                Option C: <option>
                Option D: <option>
                Correct Answer: <A/B/C/D>

                Continue the same format until Question 5.

                Rules:
                - Generate exactly 5 questions.
                - Each question must have exactly 4 options.
                - Correct Answer must contain only A, B, C or D.
                - Questions must be based only on the provided material.
                - Do not use outside information.
                - Avoid duplicate questions.
                - Test important concepts.

                Study Material:

                %s
                """.formatted(safeText);

        return generateContent(prompt);
    }


    // =========================================================
    // FLASHCARD GENERATION
    // =========================================================

    public String generateFlashcards(String text) {

        String safeText = prepareText(text);

        String prompt = """
                You are an AI study assistant.

                Generate exactly 10 useful study flashcards
                from the provided educational material.

                Use this exact format:

                Question:
                <question>

                Answer:
                <answer>

                Repeat this format for all 10 flashcards.

                Rules:
                - Generate exactly 10 flashcards.
                - Focus on important concepts.
                - Keep questions clear and concise.
                - Keep answers concise but meaningful.
                - Use only the provided material.
                - Do not add outside information.
                - Do not add numbering inside Question or Answer.
                - Do not add explanations before or after the flashcards.

                Study Material:

                %s
                """.formatted(safeText);

        return generateContent(prompt);
    }


    // =========================================================
    // COMMON GEMINI API CALL
    // =========================================================

    @SuppressWarnings("unchecked")
    private String generateContent(String prompt) {

        try {

            Map<String, Object> requestBody = Map.of(
                    "contents",
                    List.of(
                            Map.of(
                                    "parts",
                                    List.of(
                                            Map.of(
                                                    "text",
                                                    prompt
                                            )
                                    )
                            )
                    )
            );


            Map<String, Object> response =
                    restClient.post()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/v1beta/models/{model}:generateContent")
                                    .queryParam("key", apiKey)
                                    .build(model))
                            .body(requestBody)
                            .retrieve()
                            .body(Map.class);


            String generatedText =
                    extractGeneratedText(response);


            if (generatedText == null ||
                    generatedText.isBlank()) {

                throw new RuntimeException(
                        "Gemini returned an empty response"
                );
            }


            return generatedText.trim();

        } catch (Exception e) {

            System.err.println(
                    "Gemini API error: " + e.getMessage()
            );

            throw new RuntimeException(
                    "AI service is temporarily unavailable. Please try again later."
            );
        }
    }


    // =========================================================
    // EXTRACT GENERATED TEXT
    // =========================================================

    private String extractGeneratedText(
            Map<String, Object> response) {

        if (response == null) {

            throw new RuntimeException(
                    "Empty response received from Gemini"
            );
        }


        Object candidatesObject =
                response.get("candidates");


        if (!(candidatesObject instanceof List<?> candidates)
                || candidates.isEmpty()) {

            throw new RuntimeException(
                    "No AI response candidates found"
            );
        }


        Object firstCandidate =
                candidates.get(0);


        if (!(firstCandidate instanceof Map<?, ?> candidate)) {

            throw new RuntimeException(
                    "Invalid Gemini candidate response"
            );
        }


        Object contentObject =
                candidate.get("content");


        if (!(contentObject instanceof Map<?, ?> content)) {

            throw new RuntimeException(
                    "Invalid Gemini content response"
            );
        }


        Object partsObject =
                content.get("parts");


        if (!(partsObject instanceof List<?> parts)
                || parts.isEmpty()) {

            throw new RuntimeException(
                    "No generated content found"
            );
        }


        Object firstPart =
                parts.get(0);


        if (!(firstPart instanceof Map<?, ?> part)) {

            throw new RuntimeException(
                    "Invalid Gemini part response"
            );
        }


        Object textObject =
                part.get("text");


        if (textObject == null) {

            throw new RuntimeException(
                    "Generated text not found"
            );
        }


        return textObject.toString();
    }


    // =========================================================
    // PREPARE TEXT
    // =========================================================

    private String prepareText(String text) {

        if (text == null || text.isBlank()) {

            throw new RuntimeException(
                    "No study material available for AI processing"
            );
        }


        String cleanedText =
                text.trim();


        /*
         * Prevent extremely large PDF text
         * from being sent directly to Gemini.
         */
        if (cleanedText.length() > MAX_TEXT_LENGTH) {

            cleanedText =
                    cleanedText.substring(
                            0,
                            MAX_TEXT_LENGTH
                    );

            cleanedText +=
                    "\n\n[Remaining document content omitted for this AI request.]";
        }


        return cleanedText;
    }
}