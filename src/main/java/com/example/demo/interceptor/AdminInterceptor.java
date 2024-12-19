package com.example.demo.interceptor;

import com.example.demo.constants.GlobalConstants;
import com.example.demo.user.type.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String role = (String) request.getAttribute(GlobalConstants.ADMIN_AUTH);
        if (!Role.ADMIN.name().equals(role)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("admin 권한이 필요합니다.");
            return false;
        }
        return true;
    }
}
