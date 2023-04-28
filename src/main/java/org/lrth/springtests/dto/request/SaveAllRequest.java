package org.lrth.springtests.dto.request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveAllRequest {
    
    @NotNull(message = "student is mandatory")
    private @Valid StudentRequest student;
}
