package the_monitor.infrastructure.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import the_monitor.common.ErrorStatus;

import java.io.IOException;

import static the_monitor.infrastructure.utils.ExceptionHandlerUtil.exceptionHandler;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        // 필요한 권한이 없이 접근하려 할때 403
        try {
            exceptionHandler(response, ErrorStatus._FORBIDDEN, HttpServletResponse.SC_FORBIDDEN);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}