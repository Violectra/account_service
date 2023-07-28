package account.config;

import account.config.handlers.CustomAccessDeniedHandler;
import account.config.handlers.CustomAuthEntryPoint;
import account.config.handlers.SuccessFilter;
import account.models.entities.enums.Role;
import account.services.UserService;
import account.services.EventService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
public class Config {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, EventService eventService, HttpServletRequest request,
                                           UserService userService) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/auth/signup").permitAll()
                .requestMatchers("/api/auth/changepass").authenticated()
                .requestMatchers("/actuator/shutdown").permitAll()
                .requestMatchers("/api/acct/payments").hasRole(Role.ACCOUNTANT.name())
                .requestMatchers("/api/empl/payment").hasAnyRole(Role.USER.name(),
                        Role.ACCOUNTANT.name().toUpperCase())
                .requestMatchers("/api/admin/user").hasRole(Role.ADMINISTRATOR.name())
                .requestMatchers("/api/admin/**").hasRole(Role.ADMINISTRATOR.name())
                .requestMatchers("/api/security/**").hasRole(Role.AUDITOR.name())
                .requestMatchers("/h2-console").permitAll()
                .anyRequest().authenticated() // or .anyRequest().authenticated()
                .and()
                .httpBasic().authenticationEntryPoint(entryPoint())
                .and().exceptionHandling()
                .accessDeniedHandler(new CustomAccessDeniedHandler(eventService))
                .and().addFilterAfter(new SuccessFilter(request, userService), AuthorizationFilter.class);
        ;
        return http.build();
    }


    @Bean
    public AuthenticationEntryPoint entryPoint() {
        return new CustomAuthEntryPoint();
    }


    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}