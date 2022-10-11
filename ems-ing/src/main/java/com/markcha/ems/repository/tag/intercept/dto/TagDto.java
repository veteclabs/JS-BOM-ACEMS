package com.markcha.ems.repository.tag.intercept.dto;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagDto {
    protected Long id;
    protected String tagName;
    protected Object value;
}
