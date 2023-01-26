package com.markcha.scheduler.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@Data
@Embeddable
public class TagKey implements Serializable {
    private LocalDateTime timestamp;
    private String tagname;
}
