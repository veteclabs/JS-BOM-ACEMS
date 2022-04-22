package com.markcha.ems.controller;


import com.markcha.ems.domain.TagValue;
import com.markcha.ems.dto.response.ApiResponseDto;
import com.markcha.ems.dto.tag.request.SendTagSetDto;
import com.markcha.ems.dto.tag.request.Tags;
import com.markcha.ems.dto.tag.response.ReciveTagSetDto;
import com.markcha.ems.dto.tag.response.Result;
import com.markcha.ems.dto.tag.response.TagResultDto;
import com.markcha.ems.repository.TagValueDataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/WaWebService/Json")
public class WebAccessController {

    @Value("${response.jpa.DB_INSERT_MSG}")
    private String dbInsertMsg;
    @Value("${response.jpa.DB_UPDATE_MSG}")
    private String dbUpdateMsg;
    @Value("${response.jpa.DB_DELETE_MSG}")
    private String dbDeleteMsg;
    @Value("${response.jpa.DB_ERROR_MSG}")
    private String dbErrorMsg;

    private final TagValueDataRepository tagValueRepository;

    public WebAccessController(TagValueDataRepository tagValueRepository) {
        this.tagValueRepository = tagValueRepository;
    }

    @PostMapping("/GetTagValue/BOM")
    public ReciveTagSetDto get(
            @RequestBody SendTagSetDto sendTagSetDto
            ) {
        List<String> names = new ArrayList<>();
        for (Tags tag: sendTagSetDto.getTags()) {
            names.add(tag.getName());
        }
        List<TagResultDto> tagValues = tagValueRepository.findAllByNameIn(names).stream()
                .map(TagResultDto::new)
                .collect(Collectors.toList());
        System.out.println(tagValues);
        Result result = new Result(0, tagValues.size());
        ReciveTagSetDto reciveTagSetDto = new ReciveTagSetDto();
        reciveTagSetDto.setResult(result);
        reciveTagSetDto.setValues(tagValues);
        return reciveTagSetDto;
    }
    @PostMapping("/SetTagValue/BOM")
    public ApiResponseDto set(
            @RequestBody SendTagSetDto sendTagSetDto
    ) {
        List<TagValue> tagValues = sendTagSetDto.getTags().stream()
                .map(TagValue::new)
                .collect(Collectors.toList());
        tagValueRepository.saveAll(tagValues);


        return new ApiResponseDto(dbUpdateMsg);
    }
}
