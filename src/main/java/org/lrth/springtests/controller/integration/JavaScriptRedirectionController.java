package org.lrth.springtests.controller.integration;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class JavaScriptRedirectionController {
    public static final String INPUT_PATH = "/second-endpoint";
    public static final String FORM_URL = HttpRedirectionController.REMOTE_SCHEMA + "/form-submission";

    @GetMapping(INPUT_PATH + "/{resourceId}")
    @ResponseBody
    public String redirectionRedirectAgainThroughJS(
        @PathVariable String resourceId
    ) {
        return "<html>"
            + "  <body>"
            + "    <form action=\"" + FORM_URL + "\" method=\"POST\" >"
            + "      <input name=\"field1\" value=\"" + resourceId + "\" >"
            + "    </form>"
            // next is the line that automatically (w/o user interaction) redirects with JS:
            + "    <script>document.forms[0].submit();</script>"
            + "  </body>"
            + "</html>";
    }
}
