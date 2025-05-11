package com.bajaj.health.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class QualifierService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String registerUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private final String submitUrl = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

    private final String name = "John Doe";
    private final String regNo = "REG12347"; // Replace with your actual regNo
    private final String email = "john@example.com";

    public void startProcess() {
        // Step 1: Generate webhook and token
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", name);
        requestBody.put("regNo", regNo);
        requestBody.put("email", email);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(registerUrl, request, Map.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            System.out.println("Failed to register.");
            return;
        }

        String webhookUrl = (String) response.getBody().get("webhookUrl");
        String accessToken = (String) response.getBody().get("accessToken");

        // Step 2: Solve SQL question manually (based on regNo)
        int lastTwoDigits = Integer.parseInt(regNo.replaceAll("[^0-9]", "").substring(regNo.length() - 2));
        String finalQuery;

        if (lastTwoDigits % 2 == 1) {
            // Question 1
            finalQuery = "SELECT doctor_name, COUNT(patient_id) AS total_patients " +
                         "FROM appointments GROUP BY doctor_name ORDER BY total_patients DESC LIMIT 1;";
        } else {
            // Question 2
            finalQuery = "SELECT patient_id, SUM(bill_amount) AS total_bill " +
                         "FROM billing GROUP BY patient_id ORDER BY total_bill DESC LIMIT 1;";
        }

        // Step 3: Submit final SQL query
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
        authHeaders.setBearerAuth(accessToken);

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("finalQuery", finalQuery);
        HttpEntity<Map<String, String>> submitRequest = new HttpEntity<>(queryMap, authHeaders);

        ResponseEntity<String> submitResponse = restTemplate.postForEntity(submitUrl, submitRequest, String.class);
        System.out.println("Submission Response: " + submitResponse.getBody());
    }
}
