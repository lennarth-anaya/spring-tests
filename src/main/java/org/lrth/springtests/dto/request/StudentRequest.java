package org.lrth.springtests.dto.request;

import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class StudentRequest {
    @NotBlank(message = "name is mandatory")
    @NotNull(message = "name is mandatory")
    private String name;
    
    @NotNull(message = "student must have at least one subject")
    @NotEmpty(message = "student must have at least one subject")
    private Set<@Valid SubjectRequest> subjects;
}
