package com.epita.social.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

//    private final CustomLogoutHandler logoutHandler;
//
//    public SecurityConfig(CustomLogoutHandler logoutHandler) {
//        this.logoutHandler = logoutHandler;
//    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(
                        auth->auth
                                .requestMatchers("/").permitAll()
                                .anyRequest().authenticated()
                ).oauth2Login(oauth2 -> oauth2
                        .loginPage("/")
                        .defaultSuccessUrl("/api/v1/home", true)
                )
                .logout(
                        logout -> logout
                        .logoutSuccessUrl("/").invalidateHttpSession(true).clearAuthentication(true).deleteCookies("JSESSIONID")
                )
                .oauth2Login(Customizer.withDefaults())
                .build();
    }
}
