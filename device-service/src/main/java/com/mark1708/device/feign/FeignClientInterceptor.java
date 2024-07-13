package com.mark1708.device.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FeignClientInterceptor implements RequestInterceptor {

    private static final String TENANT_ID_HEADER ="X-TenantID";

    private final HttpServletRequest request;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Optional.ofNullable(
                request.getHeader(TENANT_ID_HEADER)
        ).ifPresent(headerValue -> {
            requestTemplate.header(
                    TENANT_ID_HEADER,
                    headerValue
            );
        });
    }
}

