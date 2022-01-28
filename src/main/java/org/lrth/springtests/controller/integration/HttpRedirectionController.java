package org.lrth.springtests.controller.integration;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HttpRedirectionController {
    public static final String INPUT_PATH = "/process-and-redirect";
    
    @Value("${testapp.remote.host}")
    public String remoteHost;
    @Value("${testapp.remote.port}")
    public int remotePort;

    @GetMapping(INPUT_PATH + "/{param}")
    public void processAndRedirect(
       HttpServletResponse httpServletResponse,
       @PathVariable String param
    ) {
        httpServletResponse.setHeader("Location", getRedirectUrl() + "?a=b");
        httpServletResponse.setStatus(HttpStatus.FOUND.value());
    }
    
    public String getRemoteHost() {
        return remoteHost;
    }
    
    public int getRemotePort() {
        return remotePort;
    }
    
    public String getRemoteSchema() {
        return remoteHost + ":" + remotePort;
    }
    
    public String getRedirectUrl() {
        return getRemoteSchema() + "/remote-action";
    }
}
