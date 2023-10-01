package com.song.projectboard.config;

import com.song.projectboard.dto.UserAccountDto;
import com.song.projectboard.dto.security.BoardPrincipal;
import com.song.projectboard.repository.UserAccountRepository;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .mvcMatchers(
                    HttpMethod.GET, "/", "/articles", "/articles/search-hashtag"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin().and()
            .logout()
                .logoutSuccessUrl("/")
                .and()
            .build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserAccountRepository userAccountRepository) {
        return userId -> userAccountRepository
            .findByUserId(userId)
            .map(UserAccountDto::fromEntity)
            .map(BoardPrincipal::fromDto)
            .orElseThrow(() -> new UsernameNotFoundException("해당 회원을 찾을 수 없습니다. - userId: " + userId));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
