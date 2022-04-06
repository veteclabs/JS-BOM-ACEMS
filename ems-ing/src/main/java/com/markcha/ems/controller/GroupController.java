package com.markcha.ems.controller;

import com.markcha.ems.dto.group.GroupDto;
import com.markcha.ems.dto.response.ApiResponseDto;
import com.markcha.ems.dto.schedule.ScheduleDto;
import com.markcha.ems.repository.group.impl.GroupDslRepositoryImpl;
import com.markcha.ems.service.impl.GroupServiceImpl;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GroupController {
    @Value("${response.jpa.DB_INSERT_MSG}")
    private String dbInsertMsg;
    @Value("${response.jpa.DB_UPDATE_MSG}")
    private String dbUpdateMsg;
    @Value("${response.jpa.DB_DELETE_MSG}")
    private String dbDeleteMsg;
    @Value("${response.jpa.DB_ERROR_MSG}")
    private String dbErrorMsg;
    private final GroupServiceImpl groupService;
    private final GroupDslRepositoryImpl groupDslRepository;
    @GetMapping(value="/groups")
    public List<GroupDto> show() {
        return groupDslRepository.findAllJoinSchedule().stream()
                .map((group)->new GroupDto(group))
                .collect(toList());
    }
    @PostMapping(value="/group")
    public ApiResponseDto create(
            @RequestBody GroupInsertDto groupInsertDto
    ) {
        groupService.createGruops(groupInsertDto);
        return new ApiResponseDto(dbInsertMsg);
    }
    @Data
    @NoArgsConstructor
    public static class GroupInsertDto {
        private String name;
        private ScheduleDto schedule;
    }
}
