package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

@Component
class WebhookInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        Map<String, String> request = Map.of(
            "name", "Atharv Sharma",
            "regNo", "REG532",
            "email", "atharvsharma231126@acropolis.in"
        );

        ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, request, JsonNode.class);
        JsonNode body = response.getBody();

        if (body != null && body.has("webhookUrl") && body.has("accessToken")) {
            String webhookUrl = body.get("webhookUrl").asText();
            String accessToken = body.get("accessToken").asText();

            // Call the webhook sender with this data
            submitSolution(webhookUrl, accessToken);
        } else {
            System.out.println("Error: Missing fields in response body");
        }
    }

    private void submitSolution(String webhookUrl, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken); // sets "Authorization: Bearer <token>"

        String sqlQuery =
                "SELECT p.AMOUNT AS SALARY, " +
                "CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
                "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, " +
                "d.DEPARTMENT_NAME " +
                "FROM PAYMENTS p " +
                "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
                "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                "WHERE DAY(p.PAYMENT_TIME) != 1 " +
                "ORDER BY p.AMOUNT DESC LIMIT 1;";

        Map<String, String> body = Map.of("finalQuery", sqlQuery);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                webhookUrl,
                request,
                String.class
        );

        System.out.println("Webhook response: " + response.getBody());
    }
}
