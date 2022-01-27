package org.lrth.springtests.controller.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockserver.model.HttpRequest;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebResponse;

// isolated would prevent collisions in concurrent tests
@Isolated
@ActiveProfiles("test")
public class Test302RedirectIntegrationTest
    extends BaseSetupIntegrationTest
{
    @Test
    void testHttp302Redirect() throws Exception {
      var controller1RedirectResponseBodyMock = mockControllerRedirectionRemoteResponse();
      int resourceId = 1;
      WebResponse webResponse1 = callControllerViaHttp(resourceId);
      assertEquals(HttpStatus.OK.value(), webResponse1.getStatusCode());
      assertEquals(controller1RedirectResponseBodyMock, webResponse1.getContentAsString());

      //     we can inspect the request RedirectionController sent during redirection
      var redirectParamsLst = URLEncodedUtils.parse(
          webResponse1.getWebRequest().getUrl().getQuery(), StandardCharsets.UTF_8);
      var redirectParamsMap = redirectParamsLst.stream().collect(Collectors
          .toMap(NameValuePair::getName, Function.identity()));
      NameValuePair nameValue = redirectParamsMap.get("a");
      assertEquals("b", nameValue.getValue());
    }
    
    private WebResponse callControllerViaHttp(final int resourceId) throws Exception {
        // Calling Spring Controller 1 (localServerPort)
        final var controller1Url = "http://localhost:" + localServerPort
          + HttpRedirectionController.INPUT_PATH + "/" + resourceId;

        String authToken = ""; //obtainAccessToken("userX", "passwordX");
        
        // simulate authorized user
        browserClient.addRequestHeader("Authorization", "Bearer " + authToken);

        // Trigger the controller, WebClient will resolve redirects if any, and will even execute
        // JavaScript (if javascript executes form submit onload, it will do it so our response
        // will be the response of that JavaScript submit)
        Page response1 = browserClient.getPage(controller1Url);
        return response1.getWebResponse();
    }

    /** This mock prevents a remote HTTP not found error and provides a response to the test */
    private String mockControllerRedirectionRemoteResponse() throws Exception {
        String controller1RedirectResponseBodyMock = "ctrlr1_response";
        
        // Mock Remote Server Controller1 would redirect to
        String controller1RedirectUrl = HttpRedirectionController.REDIRECT_URL
            + "?a=b"; // ?a=b just to emphasize it's GET
        // MockServer just requires the path, host and port are obviated
        String controller1RedirectPath = new URL(controller1RedirectUrl).getPath();
        HttpRequest req1 = request().withMethod("GET")
            .withPath(controller1RedirectPath);
        
        mockServerClient
          .when(req1)
          .respond(
            response()
              .withStatusCode(HttpStatus.OK.value())
              .withBody(controller1RedirectResponseBodyMock)
          );
        
        return controller1RedirectResponseBodyMock;
    }
}
