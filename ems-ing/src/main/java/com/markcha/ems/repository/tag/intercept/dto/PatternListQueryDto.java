package com.markcha.ems.repository.tag.intercept.dto;


import com.markcha.ems.domain.pattern.PatternList;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PatternListQueryDto extends PatternListDto {
    private List<PatternQueryDto> patterns;
    public PatternListQueryDto(PatternList patternList) {
        this.id = patternList.getId();
        this.name = patternList.getName();
        this.ioType= patternList.getIOType();
        this.formula = patternList.getFormula();
        this.remoteMaxValue = patternList.getRemoteMaxValue();
        this.remoteMinValue = patternList.getRemoteMinValue();
        this.description = patternList.getDescription();
        this.patterns = patternList.getPatterns().stream()
                .map(PatternQueryDto::new)
                .collect(Collectors.toList());
    }
}
