package com.lab.esb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.http.dsl.Http;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class IntegrationConfig {

    @Value("${DOCUMENTS_SERVICE_URL:http://documents:8081}")
    private String documentsServiceUrl;

    @Value("${COMMENTS_SERVICE_URL:http://comments:8083}")
    private String commentsServiceUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public IntegrationFlow documentFlow(RestTemplate restTemplate) {
        return IntegrationFlow
                .from(Http.inboundGateway("/document/{id}")
                        .requestMapping(m -> m.methods(org.springframework.http.HttpMethod.GET))
                        .payloadExpression("#pathVariables.id"))
                .handle((payload, headers) -> {
                    try {
                        String id = payload.toString();

                        System.out.println("DEBUG: Fetching document with ID: " + id);

                        String docUrl = documentsServiceUrl + "/documents/get/" + id;
                        Object document = restTemplate.getForObject(docUrl, Object.class);

                        String commentsUrl = commentsServiceUrl + "/comments/list/" + id;
                        Object comments;
                        try {
                            comments = restTemplate.getForObject(commentsUrl, Object.class);
                        } catch (RestClientException e) {
                            // Comments service down → return empty list (AP behaviour)
                            comments = java.util.Collections.emptyList();
                        }

                        Map<String, Object> response = new HashMap<>();
                        response.put("document", document);
                        response.put("comments", comments);
                        return response;

                    } catch (RestClientException e) {
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("error", "Failed to fetch data from services");
                        errorResponse.put("message", e.getMessage());
                        return errorResponse;
                    } catch (Exception e) {
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("error", "Unexpected error");
                        errorResponse.put("message", e.getMessage());
                        return errorResponse;
                    }
                })
                .get();
    }
}