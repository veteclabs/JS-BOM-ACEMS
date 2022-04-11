package com.markcha.ems.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WebaccessApiServiceImpl {
    @Value("${spring.webaccess.host}")
    private String host;

    @Value("${spring.webaccess.project}")
    private String project;

    public List<JsonNode> getTagValues(List<String> tagNames) throws JsonProcessingException {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic YWRtaW46dmV0ZWMx"); //YWRtaW46dmV0ZWMx

        Map<String, Object> body = new HashMap<>();

        List<Map<String, String >> tagObject = new ArrayList<Map<String, String>>();

        for (String name: tagNames) {
            Map<String, String> tagSet = Map.of(
                    "Name", name
            );
            tagObject.add(tagSet);
        }

        body.put("Tags", tagObject);
        System.out.println(host + "GetTagValue/" + project);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                host + "GetTagValue/" + project,
                entity,
                String.class
        );
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<JsonNode> bodyParse = mapper.readTree(response.getBody()).findParents("Value");
            return bodyParse;
        } catch (JsonProcessingException var4) {
            throw var4;
        }

    }
//    public Boolean setTagValues(List<PropertyDto> propertyDtos) {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Basic YWRtaW46dmV0ZWMx"); //YWRtaW46dmV0ZWMx
//
//        Map<String, Object> body = new HashMap<>();
//
//        List<Map<String, Object >> tagObject = new ArrayList<Map<String, Object>>();
//
//        for (PropertyDto propertyDto: propertyDtos) {
//            System.out.println(propertyDto);
//            Map<String, Object> tagSet = new HashMap<>();
//            tagSet.put(
//                    "Name", propertyDto.getTagName()
//            );
//            tagSet.put(
//                    "Value", propertyDto.getValue()
//            );
//            tagObject.add(tagSet);
//        }
//
//        body.put("Tags", tagObject);
//
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
//        ResponseEntity<String> response = restTemplate.postForEntity(
//                host + "SetTagValue/" + project,
//                entity,
//                String.class
//        );
//        return true;
//    }
}
