package com.markcha.ems.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.markcha.ems.domain.Tag;
import com.markcha.ems.domain.pattern.Pattern;
import com.markcha.ems.domain.pattern.PatternList;
import com.markcha.ems.dto.tag.TagDto;
import com.markcha.ems.dto.tag.response.ReciveTagSetDto;
import com.markcha.ems.dto.tag.response.TagResultDto;
import com.markcha.ems.repository.tag.intercept.impl.TagInterceptRepositoryImpl;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

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

        List<Map<String, String>> tagObject = new ArrayList<Map<String, String>>();

        for (String name : tagNames) {
            Map<String, String> tagSet = Map.of(
                    "Name", name
            );
            tagObject.add(tagSet);
        }

        body.put("Tags", tagObject);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                host + "GetTagValue/" + project,
                entity,
                String.class
        );
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree("Value");
        try {

            List<JsonNode> bodyParse = mapper.readTree(response.getBody()).findParents("Value");
            return bodyParse;
        } catch (JsonProcessingException var4) {
            throw var4;
        }
    }

    @Autowired
    private RestTemplate restTemplate;

    public Map<String, Object> getTagValuesV2(List<String> tagNames) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic YWRtaW46dmV0ZWMx"); //YWRtaW46dmV0ZWMx
        headers.add("Content-Type", "application/json");

        Map<String, Object> body = new HashMap<>();

        List<Map<String, String>> tagObject = new ArrayList<Map<String, String>>();

        for (String name : tagNames) {
            Map<String, String> tagSet = Map.of(
                    "Name", name
            );
            tagObject.add(tagSet);
        }


        body.put("Tags", tagObject);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<ReciveTagSetDto> response = restTemplate.postForEntity(
                host + "GetTagValue/" + project,
                entity,
                ReciveTagSetDto.class
        );
        List<TagResultDto> tagValues = response.getBody().getValues();
        Map<String, Object> tagValueMap = new HashMap<>();
        tagValues.forEach(t -> {
            tagValueMap.put(t.getName(), t.getValue());

        });

        return tagValueMap;
    }

    public Object getTagValuesV2(String tagNames) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic YWRtaW46dmV0ZWMx"); //YWRtaW46dmV0ZWMx

        Map<String, Object> body = new HashMap<>();

        List<Map<String, String>> tagObject = new ArrayList<Map<String, String>>();

        Map<String, String> tagSet = Map.of(
                "Name", tagNames
        );
        tagObject.add(tagSet);


        body.put("Tags", tagObject);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<ReciveTagSetDto> response = restTemplate.postForEntity(
                host + "GetTagValue/" + project,
                entity,
                ReciveTagSetDto.class
        );
        List<TagResultDto> tagValues = response.getBody().getValues();
        Map<String, Object> tagValueMap = new HashMap<>();
        tagValues.forEach(t -> {
            tagValueMap.put(t.getName(), t.getValue());

        });
        if (!isNull(tagValues) && tagValues.size() == 1) {
            return tagValues.get(0).getValue();
        } else {

            return null;
        }
    }

    public Double getTagValuesV2Double(String tagNames) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic YWRtaW46dmV0ZWMx"); //YWRtaW46dmV0ZWMx

        Map<String, Object> body = new HashMap<>();

        List<Map<String, String>> tagObject = new ArrayList<Map<String, String>>();

        Map<String, String> tagSet = Map.of(
                "Name", tagNames
        );
        tagObject.add(tagSet);


        body.put("Tags", tagObject);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<ReciveTagSetDto> response = restTemplate.postForEntity(
                host + "GetTagValue/" + project,
                entity,
                ReciveTagSetDto.class
        );
        List<TagResultDto> tagValues = response.getBody().getValues();
        Map<String, Object> tagValueMap = new HashMap<>();
        tagValues.forEach(t -> {
            tagValueMap.put(t.getName(), t.getValue());

        });
        if (!isNull(tagValues) && tagValues.size() == 1) {
            return new Double(tagValues.get(0).getValue().toString());
        } else {

            return null;
        }
    }

    @Autowired
    private TagInterceptRepositoryImpl tagInterceptRepository;

    @SneakyThrows
    public Boolean setTagValueV2(TagDto propertyDto) {
        RestTemplate restTemplate = new RestTemplate();
        PatternList patternList = tagInterceptRepository.getInputPattern(Arrays.asList(propertyDto.getTagName()), new Double(propertyDto.getValue().toString()));
        if (!isNull(patternList)) {
            for (Pattern pattern : patternList.getPatterns().stream()
                    .sorted(comparing(t -> t.getOrder())).collect(toList())) {
                Thread.sleep(pattern.getBeforeSleep());
                Tag controlTag = pattern.getTagList().getTags().iterator().next();
                setTagValue(TagDto.builder()
                        .tagName(controlTag.getTagName())
                        .value(pattern.getRequireValue())
                        .build());
                Thread.sleep(pattern.getAfterSleep());
            }
            return false;
        }

        return true;
    }

    public Integer getTagValuesV2Int(String tagNames) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic YWRtaW46dmV0ZWMx"); //YWRtaW46dmV0ZWMx

        Map<String, Object> body = new HashMap<>();

        List<Map<String, String>> tagObject = new ArrayList<Map<String, String>>();

        Map<String, String> tagSet = Map.of(
                "Name", tagNames
        );
        tagObject.add(tagSet);


        body.put("Tags", tagObject);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<ReciveTagSetDto> response = restTemplate.postForEntity(
                host + "GetTagValue/" + project,
                entity,
                ReciveTagSetDto.class
        );
        List<TagResultDto> tagValues = response.getBody().getValues();
        Map<String, Object> tagValueMap = new HashMap<>();
        tagValues.forEach(t -> {
            tagValueMap.put(t.getName(), t.getValue());

        });
        if (!isNull(tagValues) && tagValues.size() == 1) {
            return new Double(tagValues.get(0).getValue().toString()).intValue();
        } else {

            return null;
        }
    }

    public Boolean setTagValues(List<TagDto> propertyDtos) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic YWRtaW46dmV0ZWMx"); //YWRtaW46dmV0ZWMx

        Map<String, Object> body = new HashMap<>();

        List<Map<String, Object>> tagObject = new ArrayList<Map<String, Object>>();

        for (TagDto propertyDto : propertyDtos) {
            Map<String, Object> tagSet = new HashMap<>();
            tagSet.put("Name", propertyDto.getTagName());
            tagSet.put("Value", propertyDto.getValue());
            tagObject.add(tagSet);
        }

        body.put("Tags", tagObject);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                host + "SetTagValue/" + project,
                entity,
                String.class
        );
        return true;
    }

    public Boolean setTagValue(TagDto propertyDto) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic YWRtaW46dmV0ZWMx"); //YWRtaW46dmV0ZWMx

        Map<String, Object> body = new HashMap<>();

        List<Map<String, Object>> tagObject = new ArrayList<Map<String, Object>>();


        Map<String, Object> tagSet = new HashMap<>();
        tagSet.put(
                "Name", propertyDto.getTagName()
        );
        tagSet.put(
                "Value", propertyDto.getValue()
        );
        tagObject.add(tagSet);

        body.put("Tags", tagObject);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                host + "SetTagValue/" + project,
                entity,
                String.class
        );
        return true;
    }
}
