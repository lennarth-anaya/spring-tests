package org.lrth.springtests.controller.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// @Configuration <-- comment out to avoid Spring scanner to automatically
//      include it on every test and instead you can use @Import to use it
//      only on the tests of your interest
public class TestConfig {
    
    /** Import this bean to your tests if you want to simplify them and 
     *  avoid security checks. Might be a good idea for integration tests
     *  that use HTTP calls rather than MockMvc. In any other case it would
     *  be better if you include your users and roles in the tests themselves.
     */
    @Bean
    public SecurityFilterChain securityFilterChainTest(HttpSecurity http) throws Exception {
        http
            .httpBasic(Customizer.withDefaults())
            .authorizeRequests(authz -> authz
                .anyRequest().permitAll()
            );
        
        return http.build();
    }
}
