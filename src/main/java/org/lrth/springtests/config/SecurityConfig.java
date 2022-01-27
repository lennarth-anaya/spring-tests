package org.lrth.springtests.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .httpBasic(Customizer.withDefaults())
            .authorizeRequests(authz -> authz
               .antMatchers("/about").permitAll()
               .antMatchers("/submissions").hasRole("SPEAKER")
               .antMatchers(HttpMethod.GET, "/broadcast").hasRole("LISTENER")
               .antMatchers(HttpMethod.POST, "/broadcast").hasRole("SPEAKER")
               .anyRequest().authenticated()
            );
        
        return http.build();
    }
}
