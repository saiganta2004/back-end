package com.smartattendance.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartattendance.dto.VerifyRequest;
import com.smartattendance.payload.response.FaceRecognitionResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Service
@Slf4j
public class FaceRecognitionService {

    private final RestTemplate restTemplate;
    private final String faceRecognitionServiceUrl;
    private final ObjectMapper objectMapper;

    public FaceRecognitionService(RestTemplate restTemplate,
                                  @Value("${face-recognition.service.url}") String faceRecognitionServiceUrl, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.faceRecognitionServiceUrl = faceRecognitionServiceUrl;
        this.objectMapper = objectMapper;
    }

    public FaceRecognitionResponse recognizeFace(String imageBase64) {
        String url = faceRecognitionServiceUrl + "/recognize_face";
        log.debug("Calling face recognition service at URL: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String cleanImageBase64 = imageBase64.contains(",") ? imageBase64.split(",")[1] : imageBase64;

        Map<String, String> requestBody = Collections.singletonMap("image", cleanImageBase64);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);
            String responseBody = responseEntity.getBody();
            log.info("Raw response from face recognition service: {}", responseBody);

            // Manually parse the JSON response
            JSONObject jsonObject = new JSONObject(responseBody);
            FaceRecognitionResponse response = new FaceRecognitionResponse();
            response.setSuccess(jsonObject.optBoolean("success"));
            response.setRecognized(jsonObject.optBoolean("recognized"));
            // Use student_id from Python service as userId
            response.setUserId(jsonObject.optString("student_id", null));
            response.setName(jsonObject.optString("name", null));
            response.setConfidence(jsonObject.optDouble("confidence"));
            response.setMessage(jsonObject.optString("message", null));
            response.setError(jsonObject.optString("error", null));

            return response;
        } catch (Exception e) {
            log.error("Error calling or parsing face recognition service: " + e.getMessage());
            FaceRecognitionResponse errorResponse = new FaceRecognitionResponse();
            errorResponse.setSuccess(false);
            errorResponse.setError("Failed to connect to or parse response from face recognition service.");
            return errorResponse;
        }
    }

    public boolean verifyUser(String uniqueId, String imageData) {
        String url = faceRecognitionServiceUrl + "/verify";
        log.debug("Calling user verification service at URL: {}", url);
        VerifyRequest request = new VerifyRequest(uniqueId, imageData);
        try {
            Boolean result = restTemplate.postForObject(url, request, Boolean.class);
            return result != null && result;
        } catch (Exception e) {
            log.error("Error calling user verification service: " + e.getMessage());
            return false;
        }
    }

    public void reloadFaces() {
        String url = faceRecognitionServiceUrl + "/reload_faces";
        log.info("Requesting face recognition service to reload faces at URL: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("{}", headers); // Empty JSON object as payload

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully triggered face reload: {}", responseEntity.getBody());
            } else {
                log.warn("Failed to trigger face reload. Status: {}, Body: {}", responseEntity.getStatusCode(), responseEntity.getBody());
            }
        } catch (Exception e) {
            log.error("Error calling face reload endpoint: " + e.getMessage());
        }
    }
}
