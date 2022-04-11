package com.markcha.ems.dto.tag;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@NoArgsConstructor
public class TagDto {
    private String tagName;
    private Double value;
    private String unit;
    private String propertiyName;

}
