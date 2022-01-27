package org.lrth.springtests.controller.integration;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HttpRedirectionController {
    public static final String INPUT_PATH = "/process-and-redirect";
    
    // TODO next would come from properties file instead:
    public static final String REMOTE_HOST = "http://localhost";
    public static final int REMOTE_PORT = 8108;

    public static final String REMOTE_SCHEMA = REMOTE_HOST + ":" + REMOTE_PORT;
    public static final String REDIRECT_URL = REMOTE_SCHEMA + "/remote-action";
    
    @GetMapping(INPUT_PATH + "/{param}")
    public void processAndRedirect(
       HttpServletResponse httpServletResponse,
       @PathVariable String param
    ) {
        httpServletResponse.setHeader("Location", REDIRECT_URL + "?a=b");
        httpServletResponse.setStatus(HttpStatus.FOUND.value());
    }
}
