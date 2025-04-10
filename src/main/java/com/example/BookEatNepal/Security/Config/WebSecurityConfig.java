package com.example.BookEatNepal.Security.Config;

import com.example.BookEatNepal.Security.JWT.AuthEntryPointJwt;
import com.example.BookEatNepal.Security.JWT.AuthTokenFilter;
import com.example.BookEatNepal.ServiceImpl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200","https://bbangular.onrender.com","https://uat.bandobasta.com","https://adminbandobasta.onrender.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allowed HTTP methods
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Accept", "Authorization")); // Allowed headers
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // Apply config to all endpoints
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource())) // Use custom CORS config
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/package/findAll").permitAll()
                        .requestMatchers("/hall/findAll").permitAll()
                        .requestMatchers("/menu/findAll").permitAll()
                        .requestMatchers("/venue/findAll").permitAll()
                        .requestMatchers("/venue/checkVenueAvailability").permitAll()
                        .requestMatchers("/images/venues/**").permitAll()
                        .requestMatchers("/user/authenticate/register").permitAll()
                        .requestMatchers("/user/authenticate/login").permitAll()
                        .requestMatchers("/user/forgotPassword").permitAll()
                        .requestMatchers("/user/resetUserPassword").permitAll()
                        .requestMatchers("/user/validateOTP").permitAll()
                        .requestMatchers("/user/logOut").permitAll()
                        .requestMatchers("/user/authenticate/register/confirm").permitAll()
                        .requestMatchers("/event/order/save").permitAll()
                        .requestMatchers("/event/order/confirmOrder").permitAll()
                        .requestMatchers("/event/order/checkIn").permitAll()
                        .requestMatchers("/event/ticket/findAll").permitAll()
                        .requestMatchers("/event/ticket/findAllByUser").permitAll()
                        .requestMatchers("/event/order/findAllTicketOrderByEvent").permitAll()
                        .requestMatchers("/event/order/countOfBookedAndCheckedInTicket").permitAll()
                        .requestMatchers("/send").permitAll()
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}
