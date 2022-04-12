package com.markcha.ems.controller.analysis;

import com.markcha.ems.controller.GroupController;
import com.markcha.ems.controller.GroupController.GroupSearchDto;
import com.markcha.ems.domain.Group;
import com.markcha.ems.domain.Link;
import com.markcha.ems.dto.response.ApiResponseDto;
import com.markcha.ems.repository.group.impl.GroupDynamicRepositoryImpl;
import com.markcha.ems.repository.group.dto.GroupQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class DataAnalysisController {
    @Value("${response.jpa.DB_INSERT_MSG}")
    private String dbInsertMsg;
    @Value("${response.jpa.DB_UPDATE_MSG}")
    private String dbUpdateMsg;
    @Value("${response.jpa.DB_DELETE_MSG}")
    private String dbDeleteMsg;
    @Value("${response.jpa.DB_ERROR_MSG}")
    private String dbErrorMsg;

    private final GroupDynamicRepositoryImpl groupDynamicRepository;

    @GetMapping(value="/data")
    public List<GroupQueryDto> create(
            GroupSearchDto groupInsertDto
    ) {
        groupInsertDto.setIsUsage(true);
//        groupInsertDto.();

        List<Long> rootGroupIds = groupDynamicRepository.getTypeIds("group");
        List<String> tagNames = new ArrayList<>();
        List<GroupQueryDto> collect = groupDynamicRepository.getAnalysisLocations(rootGroupIds, groupInsertDto, true).stream()
                .map(GroupQueryDto::new)
                .peek(t -> tagNames.addAll(t.getTagNames()))
                .collect(toList());
        System.out.println(tagNames);
        return collect;
    }
}
