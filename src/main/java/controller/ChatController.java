package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import service.AiAssistantService;

@RestController
public class ChatController {

    @Autowired
    private AiAssistantService aiService;

    // Test URL: http://localhost:8080/test-ai?id=10001&q=How much is my salary?
    @GetMapping("/test-ai")
    public String testAi(@RequestParam int id, @RequestParam String q) {
        return aiService.askHrBot(id, q);
    }
}