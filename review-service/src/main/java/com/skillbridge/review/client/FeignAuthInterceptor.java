package com.skillbridge.review.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign request interceptor that propagates the Authorization header
 * from the incoming HTTP request to all outbound Feign calls.
 *
 * This ensures that when review-service calls booking-service or
 * user-service, the original JWT token is forwarded transparently.
 *
 * Applied automatically to all Feign clients registered in this service
 * because it implements RequestInterceptor and is a Spring @Component.
 */
@Component
public class FeignAuthInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                template.header("Authorization", authHeader);
            }
        }
    }
}
