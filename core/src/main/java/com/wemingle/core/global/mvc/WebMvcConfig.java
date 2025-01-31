package com.wemingle.core.global.mvc;

import com.wemingle.core.global.interceptor.JwtBlacklistInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final JwtBlacklistInterceptor jwtBlacklistInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtBlacklistInterceptor)
                .excludePathPatterns(
                        "/admin",
                        "/members/signin",
                        "members/signup",
                        "members/available-id",
                        "/nickname/{nickname}/available");
    }
}
