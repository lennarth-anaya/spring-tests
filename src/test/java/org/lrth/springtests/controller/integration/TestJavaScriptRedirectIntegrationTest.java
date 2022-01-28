package org.lrth.springtests.controller.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.NameValuePair;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import org.mockserver.model.HttpRequest;
import org.mockserver.model.RequestDefinition;

import org.springframework.test.context.ActiveProfiles;

import com.gargoylesoftware.htmlunit.WebResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

//isolated would prevent collisions in concurrent tests
@Isolated
@ActiveProfiles("test")
public class TestJavaScriptRedirectIntegrationTest
    extends HttpIntegrationBaseTest
{

    private HttpRequest redirectRequest;
    
    @Autowired
    private JavaScriptRedirectionController jsRedirController;
    
    @Test
    void testJavaScriptRedirect() throws Exception {
        final String resourceId = "field1_ValueX";
        
        String redirectResponseBodyMock = mockJavaScriptRedirectionRemoteResponse();
        
        var ctrlWebResp = callControllerViaHttp(resourceId);
        
        assertEquals( HttpStatus.OK.value(), ctrlWebResp.getStatusCode());
        assertEquals(redirectResponseBodyMock,
            ctrlWebResp.getContentAsString());
    
        // let's inspect what JavaScriptRedirectionController sent to simulated redirect via JS-html-form-post
        RequestDefinition[] requests = mockServerClient.retrieveRecordedRequests(redirectRequest);
        // make sure there was only form post submission
        assertEquals(1, requests.length);

        HttpRequest formSubmitReq = (HttpRequest) requests[0];
        // parameters will come in body part since it was an HTML Form POST
        Map<String, NameValuePair> paramsSentByCtrl2 = URLEncodedUtils
            .parse(formSubmitReq.getBodyAsJsonOrXmlString(), StandardCharsets.UTF_8)
            .stream()
            .collect(Collectors.toMap(NameValuePair::getName, Function.identity()));
        NameValuePair field1NameValuePair = paramsSentByCtrl2.get("field1");
        assertEquals( resourceId, field1NameValuePair.getValue());
    }
    
    private WebResponse callControllerViaHttp(final String resourceId) throws Exception {
        final var controller2Url = "http://localhost:" + localServerPort 
                + JavaScriptRedirectionController.INPUT_PATH + "/" + resourceId;
        var ctrl2Resp = browserClient.getPage(controller2Url);

        return ctrl2Resp.getWebResponse();
    }

    private String mockJavaScriptRedirectionRemoteResponse() throws Exception {
        String redirectResponseBodyMock = "ctrlr2_response";
        
        // Mock Remote Server Controller2 whose HTML form JS would auto-submit
        //   (HTTP POST)
        String redirectUrl = jsRedirController.getFormUrl();
        // MockServer just requires the path, host and port are obviated
        String redirectPath = new URL(redirectUrl).getPath();
        redirectRequest = request().withMethod("POST")
            .withPath(redirectPath);
        
        mockServerClient
          .when(redirectRequest)
          .respond(
            response()
              .withDelay(TimeUnit.SECONDS, 2)
              .withStatusCode(200)
              .withBody(redirectResponseBodyMock)
           );
        
        return redirectResponseBodyMock;
    }
}
