package service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class AiClient {
    private final String apiKey;
    private final String apiUrl = "https://api.groq.com/openai/v1/chat/completions";

    public AiClient(String apiKey) {
        this.apiKey = apiKey;
    }

    public String generate(String prompt) {
        try {
            JSONObject body = new JSONObject();
            
            body.put("model", "llama-3.3-70b-versatile"); 
            
            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);
            messages.put(message);
            body.put("messages", messages);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            System.out.println("--- Groq Raw Response ---");
            System.out.println(responseBody);
            System.out.println("---------------------------");

            JSONObject jsonResponse = new JSONObject(responseBody);

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
