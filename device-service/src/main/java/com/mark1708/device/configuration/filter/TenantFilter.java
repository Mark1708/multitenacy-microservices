package com.mark1708.device.configuration.filter;

import com.mark1708.device.configuration.context.TenantContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
class TenantFilter implements Filter {

    private static final String TENANT_HEADER = "X-TenantID";
    private static final String ACTUATOR_PATH_PREFIX = "/actuator";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        if (req.getRequestURI().startsWith(ACTUATOR_PATH_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        String tenantName = req.getHeader(TENANT_HEADER);
        if (tenantName == null || tenantName.isBlank()) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or blank " + TENANT_HEADER + " header");
            return;
        }

        TenantContext.setCurrentTenant(tenantName);

        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
