package com.markcha.ems.dto.tag;

import com.markcha.ems.domain.Tag;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.isNull;

@Data
@Setter
@Getter
@NoArgsConstructor
public class TagDto {
    private String tagName;
    private Double value;
    private String unit;
    public TagDto(Tag tag) {
        this.tagName = tag.getTagName();
        this.unit = isNull(tag.getUnit())?"":tag.getUnit();
    }
}
