package org.lrth.springtests.controller;

import javax.validation.Valid;

import org.lrth.springtests.dto.request.SaveAllRequest;
import org.lrth.springtests.dto.response.SaveAllResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@RequestMapping(value = "someResource", produces = MediaType.APPLICATION_JSON_VALUE)
public class BeanValidationController {

    @PostMapping("/addAll")
    public SaveAllResponse saveall(
            @Valid @RequestBody SaveAllRequest request) {
        log.info("Controller.saveAll() start, this methods does nothing, it's intended to be Bean Validation tested");
        SaveAllResponse response = new SaveAllResponse();
        response.setMessage("nothing was really done");
        
        return response;
    }

}
