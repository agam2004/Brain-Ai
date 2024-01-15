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
    final private CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    final private   CustomLogoutSuccessHandler customLogoutSuccessHandler;


    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/Login/**").permitAll()
                        .requestMatchers("/Admin").hasRole("ADMIN")
                        .requestMatchers("/SignUp/**").permitAll()
                        .requestMatchers("/Model/**").hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers("/Articals/**").permitAll()
                        .requestMatchers("/doctors/**").permitAll()
                        .requestMatchers("/patients/**").permitAll()
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
        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() throws Exception {
        return (web) -> web.ignoring().requestMatchers("/css/**", "/css_images/**", "/js/**", "/favicon.ico",
                "/health.html",
                "/robots.txt");
    }
}