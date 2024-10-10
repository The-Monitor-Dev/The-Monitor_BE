package the_monitor.infrastructure.jwt.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.UrlPathHelper;

import java.io.IOException;
import java.util.HashMap;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        String exception = (String)request.getAttribute("exception");
        handlePageResponse(request,response, exception);
    }
    private void handlePageResponse(HttpServletRequest request, HttpServletResponse response, String exception) throws IOException {
        log.error("Page Request - Commence Get Exception : {}", exception);
        response.sendRedirect("/user/login");
    }
    private void handleRestResponse(HttpServletRequest request, HttpServletResponse response, String exception) throws IOException {
        String requestURI = new UrlPathHelper().getPathWithinApplication(request);
        // 허용된 경로 확인
        RequestMatcher permitAllMatcher = new AntPathRequestMatcher(requestURI);
        if (SecurityConfig.PERMIT_ALL_PATHS.stream().anyMatch(path -> permitAllMatcher.matches(request))) {
            return;
        }
        log.error("Rest Request - Commence Get Exception : {}", exception);

        if (exception != null) {
            if (exception.equals(JwtExceptionCode.INVALID_TOKEN.getCode())) {
                log.error("entry point >> invalid token");
                setResponse(response, JwtExceptionCode.INVALID_TOKEN);
            } else if (exception.equals(JwtExceptionCode.EXPIRED_TOKEN.getCode())) {
                log.error("entry point >> expired token");
                setResponse(response, JwtExceptionCode.EXPIRED_TOKEN);
            } else if (exception.equals(JwtExceptionCode.UNSUPPORTED_TOKEN.getCode())) {
                log.error("entry point >> unsupported token");
                setResponse(response, JwtExceptionCode.UNSUPPORTED_TOKEN);
            } else if (exception.equals(JwtExceptionCode.NOT_FOUND_TOKEN.getCode())) {
                log.error("entry point >> not found token");
                setResponse(response, JwtExceptionCode.NOT_FOUND_TOKEN);
            } else {
                setResponse(response, JwtExceptionCode.UNKNOWN_ERROR);
            }
        } else {
            setResponse(response, JwtExceptionCode.UNKNOWN_ERROR);
        }
    }
    private void setResponse(HttpServletResponse response, JwtExceptionCode exceptionCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        HashMap<String, Object> errorInfo = new HashMap<>();
        errorInfo.put("message", exceptionCode.getMessage());
        errorInfo.put("code", exceptionCode.getCode());
        Gson gson = new Gson();
        String responseJson = gson.toJson(errorInfo);
        response.getWriter().print(responseJson);
    }
    private boolean isRestRequest(HttpServletRequest request) {
        String requestedWithHeader = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWithHeader) || request.getRequestURI().startsWith("/api/user");
    }

    public void commence(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String exception = (String)request.getAttribute("exception");
        handlePageResponse(request,response,exception);
    }
}

