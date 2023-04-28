package org.lrth.springtests.dto.request;

import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.lrth.springtests.enums.DifficultyEnum;

import lombok.Data;

@Data
public class SubjectRequest {
    @NotBlank(message = "name is mandatory")
    private String name;
    
    @NotBlank(message = "introduction is mandatory")
    private String introduction;
    
    @Valid
    @NotNull(message = "subject must have at least one topic")
    private Set<TopicRequest> topics;
    
    @NotNull(message = "difficulty is mandatory")
    private DifficultyEnum difficulty;
}
