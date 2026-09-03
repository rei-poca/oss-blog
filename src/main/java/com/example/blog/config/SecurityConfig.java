package com.example.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置：表单登录、角色授权、登出。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 管理端仅管理员可访问
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 评论需要登录
                        .requestMatchers("/post/*/comment").authenticated()
                        // 前台与静态资源公开
                        .requestMatchers("/", "/login", "/register", "/post/**", "/tag/**",
                                "/search", "/css/**", "/js/**", "/images/**",
                                "/favicon.ico", "/error").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", false)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll())
                // 本实验为本地演示应用，关闭 CSRF 以简化表单提交
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
