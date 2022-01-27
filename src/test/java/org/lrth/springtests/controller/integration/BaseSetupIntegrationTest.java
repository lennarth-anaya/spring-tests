package org.lrth.springtests.controller.integration;

import com.gargoylesoftware.htmlunit.WebClient;

import org.mockserver.client.MockServerClient;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.junit.jupiter.MockServerSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
//import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lrth.springtests.SpringTestsApplication;

@SpringBootTest(
    // instead of import below
    classes = SpringTestsApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@ExtendWith(MockServerExtension.class)
@MockServerSettings(ports = {BaseSetupIntegrationTest.MOCKED_REMOTE_SERVER_PORT})
@Import({TestConfig.class})
public class BaseSetupIntegrationTest {
    // any available port
    public static final int MOCKED_REMOTE_SERVER_PORT = HttpRedirectionController.REMOTE_PORT;

    MockServerClient mockServerClient;
    WebClient browserClient = new WebClient();

    // Spring MVC utilities and controllers to test:
    MockMvc mvc;
    // server port MVC and Rest controllers will be served on
    @LocalServerPort
    int localServerPort;

    @Autowired
    HttpRedirectionController httpRedirectionController;

    @Autowired
    JavaScriptRedirectionController jsRedirectionController;

    @BeforeEach
    void beforeEachTest(MockServerClient mockServerClient) {
      this.mockServerClient = mockServerClient;
      mockServerClient.reset();
      browserClient = new WebClient();

      // even if we don't use mvc variable to trigger controllers, this will make them
      //    available to WebClient at port localServerPort
      mvc = MockMvcBuilders
          .standaloneSetup(httpRedirectionController, jsRedirectionController)
          .build();
    }

    /** This method calls security end point with MockMvc rather
     *  than WebClient because WebClient should only be used
     *  to test HTTP conversion params, rather than to merely
     *  complicate the test.
     */
    protected String obtainAccessToken(String username, String password) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", "fooClientIdPassword");
        params.add("username", username);
        params.add("password", password);

        ResultActions result 
          = mvc.perform(post("/oauth/token")
            .params(params)
            .with(httpBasic("fooClientIdPassword","secret"))
            .accept("application/json;charset=UTF-8"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json;charset=UTF-8"));

        String resultString = result.andReturn().getResponse().getContentAsString();

        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }

}
