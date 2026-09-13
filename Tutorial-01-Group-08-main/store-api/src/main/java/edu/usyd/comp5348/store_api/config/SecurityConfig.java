//package edu.usyd.comp5348.store_api.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                // 纯后端 API，这里先关闭 CSRF（否则 POST 要带 token）
//                .csrf(csrf -> csrf.disable())
//                // 放行你需要的接口
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/auth/**",
//                                "/orders/**",
//                                "/actuator/health",
//                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
//                        ).permitAll()
//                        .anyRequest().permitAll()
//                )
//                // 关闭默认登录页（用自己的 /auth/login）
//                .formLogin(form -> form.disable())
//                .httpBasic(basic -> basic.disable());
//
//        return http.build();
//    }
//}
