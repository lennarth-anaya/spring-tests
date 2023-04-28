package org.lrth.springtests.dto.response;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class ErrorResponse {
    private Map<String, String> errors = new HashMap<>();
}
