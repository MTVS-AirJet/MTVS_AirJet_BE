package com.meta.air_jet._core.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CORSFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // 특정 출처에서의 요청 허용 여부
        response.setHeader("Access-Control-Allow-Origin", "*");
        // 클라이언트 측 자격 증명 요청 (쿠키)
        response.setHeader("Access-Control-Allow-Credentials", "true");
        // 허용하는 HTTP 메소드
        response.setHeader("Access-Control-Allow-Methods", "*");
        // 동일한 CORS 요청 시 설정 시간 동안 새로운 검증 없이 캐시된 CORS 설정 사용
        response.setHeader("Access-Control-Max-Age", "3600");
        // 클라이언트가 요청할 때 허용할 헤더 목록 설정
        response.setHeader("Access-Control-Allow-Headers",
            "Origin, X-Requested-With, Content-Type, Accept, Authorization, Refresh-Token");
        // 클라이언트가 응답에서 접근할 수 있는 헤더 목록 설정
        response.setHeader("Access-Control-Expose-Headers", "Authorization, Refresh-Token");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
