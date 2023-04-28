package org.lrth.springtests.dto.request;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class TopicRequest {
    @NotBlank
    private String name;
}
