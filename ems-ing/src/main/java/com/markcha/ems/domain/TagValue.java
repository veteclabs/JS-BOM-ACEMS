package com.markcha.ems.domain;

import com.markcha.ems.dto.tag.request.Tags;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class TagValue {
    @Id
    private String Name;

    private Double Value;

    private Integer Quality;
    public TagValue(Tags tags ) {
        this.Name = tags.getName();
        this.Value = tags.getValue();
        this.Quality = 1;
    }
}
