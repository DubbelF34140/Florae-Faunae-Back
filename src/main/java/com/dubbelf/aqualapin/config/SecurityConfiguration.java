package com.dubbelf.aqualapin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final MyUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfiguration(@Lazy MyUserDetailsService userDetailsService,
                                 JwtUtils jwtUtils,
                                 AuthEntryPointJwt unauthorizedHandler,
                                 CorsConfigurationSource corsConfigurationSource) {
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.unauthorizedHandler = unauthorizedHandler;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public JwtFilter authenticationJwtTokenFilter() {
        return new JwtFilter(jwtUtils, userDetailsService);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. OPTIONS requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. Endpoints publics
                        .requestMatchers("/auth/signin", "/auth/signup", "/auth/resetPassword", "/auth/savePassword").permitAll()

                        // 3. Endpoints ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .requestMatchers("/upload/**", "/upload", "/upload/", "/upload/list").authenticated()
                        .requestMatchers("/user/edit").authenticated()
                        .requestMatchers("/categories").authenticated()
                        .requestMatchers("/posts").authenticated()
                        .requestMatchers("/profil/**").authenticated()
                        .requestMatchers("/posts/stats", "/posts/recent", "/posts/**").permitAll()
                        .requestMatchers("/categories/best", "/categories").permitAll()


                        // 6. Tout le reste
                        .anyRequest().authenticated()

                ).formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

}
