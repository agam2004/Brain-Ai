package com.example.brainAi.config;

import com.example.brainAi.service.CustomLogoutSuccessHandler;
import com.example.brainAi.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AppSecurity {
    // Instance variables for the custom user details service and JWT utility class
    final private CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    final private CustomLogoutSuccessHandler customLogoutSuccessHandler;

    // Configuring a password encoder to use for encoding passwords
    // In this case, BCryptPasswordEncoder is used for hashing passwords.
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuring the security filter chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // we don't need csrf protection in jwt
        http

                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                // The SessionCreationPolicy.STATELESS setting means that the application will not create or use HTTP sessions.
                // This is a common configuration in RESTful APIs, especially when using token-based authentication like JWT (JSON Web Token).
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Adding a custom JWT authentication filter before the default UsernamePasswordAuthenticationFilter
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class)

                // Configuring authorization for HTTP requests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/index/**", "/login/**", "/logout/**").permitAll()
                        .requestMatchers("/admin_home/**").hasRole("ADMIN")
                        .requestMatchers("/doctors/**").hasRole("DOCTOR")
                        .requestMatchers("/model/**").hasAnyRole("DOCTOR", "ADMIN")  // Corrected role names
                        .requestMatchers("/patients/**").hasRole("PATIENT")
                        .requestMatchers("/articales/**").permitAll()
                        .requestMatchers("/user_home/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/home/**").permitAll()
                        .anyRequest().authenticated())

                .logout(
                        new Customizer<LogoutConfigurer<HttpSecurity>>() {
                            @Override
                            public void customize(LogoutConfigurer<HttpSecurity> logout) {
                                logout
                                        .logoutUrl("/logout")
                                        .logoutSuccessHandler(customLogoutSuccessHandler)
                                        .invalidateHttpSession(true)
                                        .clearAuthentication(true)
                                        .deleteCookies("JSESSIONID")
                                        .permitAll();
                            }
                        });

        // Building and returning the security filter chain
        return http.build();
    }

    // Configuring authentication manager, which is used by the authentication service to authenticate a user
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Configuring web security to ignore requests to the CSS and CSS images directories
    // This is useful for allowing unauthenticated access to static resources such as styles and images.
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/css/**", "/css_images/**", "/favicon.ico", "/health.html", "/robots.txt");
    }
}
