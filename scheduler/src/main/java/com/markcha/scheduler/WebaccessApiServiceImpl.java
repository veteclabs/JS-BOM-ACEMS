package com.markcha.scheduler;

import com.markcha.scheduler.dto.tag.TagDto;
import com.markcha.scheduler.dto.tag.response.ReciveTagSetDto;
import com.markcha.scheduler.dto.tag.response.TagResultDto;
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

import static java.util.Objects.isNull;

@Service
public class WebaccessApiServiceImpl {
    @Value("${spring.webaccess.host}")
    private String host;

    @Value("${spring.webaccess.project}")
    private String project;


    public Map<String, Object> getTagValuesV2(List<String> tagNames) {

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
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<ReciveTagSetDto> response = restTemplate.postForEntity(
                host + "GetTagValue/" + project,
                entity,
                ReciveTagSetDto.class
        );
        List<TagResultDto> tagValues = response.getBody().getValues();
        Map<String, Object> tagValueMap = new HashMap<>();
        tagValues.forEach(t->{
            tagValueMap.put(t.getName(), t.getValue());

        });

        return tagValueMap;
    }
    public Object getTagValuesV2(String tagNames) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic YWRtaW46dmV0ZWMx"); //YWRtaW46dmV0ZWMx

        Map<String, Object> body = new HashMap<>();

        List<Map<String, String >> tagObject = new ArrayList<Map<String, String>>();

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
        tagValues.forEach(t->{
            tagValueMap.put(t.getName(), t.getValue());

        });
        if(!isNull(tagValues) && tagValues.size() == 1) {
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

        List<Map<String, String >> tagObject = new ArrayList<Map<String, String>>();

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
        tagValues.forEach(t->{
            tagValueMap.put(t.getName(), t.getValue());

        });
        if(!isNull(tagValues) && tagValues.size() == 1) {
            return new Double(tagValues.get(0).getValue().toString());
        } else {

            return null;
        }
    }
    public Integer getTagValuesV2Int(String tagNames) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic YWRtaW46dmV0ZWMx"); //YWRtaW46dmV0ZWMx

        Map<String, Object> body = new HashMap<>();

        List<Map<String, String >> tagObject = new ArrayList<Map<String, String>>();

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
        tagValues.forEach(t->{
            tagValueMap.put(t.getName(), t.getValue());

        });
        if(!isNull(tagValues) && tagValues.size() == 1) {
            return new Double(tagValues.get(0).getValue().toString()).intValue();
        } else {

            return null;
        }
    }
    public Boolean setTagValues(List<TagDto> propertyDtos) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic YWRtaW46dmV0ZWMx"); //YWRtaW46dmV0ZWMx

        Map<String, Object> body = new HashMap<>();

        List<Map<String, Object >> tagObject = new ArrayList<Map<String, Object>>();

        for (TagDto propertyDto: propertyDtos) {
            Map<String, Object> tagSet = new HashMap<>();
            tagSet.put(
                    "Name", propertyDto.getTagName()
            );
            tagSet.put(
                    "Value", propertyDto.getValue()
            );
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

        List<Map<String, Object >> tagObject = new ArrayList<Map<String, Object>>();


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
