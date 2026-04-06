package service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class AiClient {
    private final String apiKey;
    // Updated to Groq API URL
    private final String apiUrl = "https://api.groq.com/openai/v1/chat/completions";

    public AiClient(String apiKey) {
        this.apiKey = apiKey;
    }

    public String generate(String prompt) {
        try {
            // 1. Create the JSON request body
            JSONObject body = new JSONObject();
            
            // FIXED: Changed model to one supported by Groq
            body.put("model", "llama-3.3-70b-versatile"); 
            
            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);
            messages.put(message);
            body.put("messages", messages);

            // 2. Build the HTTP Request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

            // 3. Send and Get Response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            // --- DEBUGGING: Useful for monitoring the Groq response ---
            System.out.println("--- Groq Raw Response ---");
            System.out.println(responseBody);
            System.out.println("---------------------------");

            JSONObject jsonResponse = new JSONObject(responseBody);

            // 4. Safety Check: Handle errors from Groq
            if (jsonResponse.has("error")) {
                JSONObject errorObj = jsonResponse.getJSONObject("error");
                return "AI Error: " + errorObj.getString("message");
            }

            if (jsonResponse.has("choices")) {
                return jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
            }

            return "Error: Unexpected response format from AI.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling AI: " + e.getMessage();
        }
    }
}