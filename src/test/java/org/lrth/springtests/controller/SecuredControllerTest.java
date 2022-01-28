package org.lrth.springtests.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SecuredControllerTest {

    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    SecuredController securedController;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    
    @Test
    public void nonSecuredEndpoint() throws Exception {
        // given no authentication details are provided for non-secured endpoiint
        this.mockMvc.perform(get("/about"))
            // then request is accepted
            .andExpect(status().isOk())
            .andExpect(content().string("about"));
    }

    @Test
    public void securedEndpointGreeting() throws Exception {
        // given authenticated user is provided (no role needed for this endpoint)
        this.mockMvc
            .perform(get("/greeting").with(user("Lennarth")))
            // then request is authorized
            .andExpect(status().isOk())
            .andExpect(content().string("Hi, Lennarth"));
    }

    @Test
    public void securedEndpointGreetingThatFailsWithoutUser401() throws Exception {
        // given no authenticated user is proivded
        this.mockMvc
            .perform(get("/greeting"))
            // then it's unauthorized
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    public void securedEndpointThatIsForbiddenDueToRole403() throws Exception {
        // given no role is provided
        this.mockMvc
            .perform(get("/submissions").with(user("Lennarth")))
            // then endpoint is forbidden
            .andExpect(status().isForbidden());

        // given invalid role is provided
        this.mockMvc
            .perform(get("/submissions").with(
                user("Lennarth").roles("UNEXISTENT-ROLE")
            ))
            // then endpoint is forbidden
            .andExpect(status().isForbidden());
    }
    
    @Test
    public void securedEndpointThatIsAllowedWithProperRole() throws Exception {
        // given valid role is provided
        this.mockMvc
            .perform(
                get("/submissions")
                .with(user("Lennarth").roles("SPEAKER"))
            )
            // then request is ok
            .andExpect(status().isOk());
    }

    @Test
    public void wrongMethodPostIsForbiddenDueToMissingCsrf() throws Exception {
        // given POST method with no CSRF
        this.mockMvc
            .perform(
                post("/submissions")
                .with(user("Lennarth").roles("SPEAKER"))
            )
            // then forbidden
            .andExpect(status().isForbidden());
    }
    
    @Test
    public void wrongMethodPostIsMethodNotAllowedWithProperCsrf() throws Exception {
        // given POST method with proper CSRF
        this.mockMvc
            .perform(
                post("/submissions")
                .with(user("Lennarth").roles("SPEAKER"))
                .with(csrf())
            )
            // then not allowed method is recognized this time
            .andExpect(status().isMethodNotAllowed());
    }
    
    @Test
    public void checkRolesBasedOnHttpMethods() throws Exception {
        // given GET method with proper role
        this.mockMvc
            .perform(
                get("/broadcast")
                .with(user("Lennarth").roles("LISTENER"))
                .with(csrf())
            )
            // then allowed
            .andExpect(status().isOk());
        
        // given GET method with wrong role
        this.mockMvc
            .perform(
                get("/broadcast")
                .with(user("Lennarth").roles("SPEAKER"))
                .with(csrf())
            )
            // then NOT allowed
            .andExpect(status().isForbidden());
        
        // and the other way around:
        
        // given POST method with proper role
        this.mockMvc
            .perform(
                post("/broadcast")
                .with(user("Lennarth").roles("SPEAKER"))
                .with(csrf())
            )
            // then allowed
            .andExpect(status().isOk());
        
        // given POST method with wrong role
        this.mockMvc
            .perform(
                post("/broadcast")
                .with(user("Lennarth").roles("LISTENER"))
                .with(csrf())
            )
            // then NOT allowed
            .andExpect(status().isForbidden());
    }
}
