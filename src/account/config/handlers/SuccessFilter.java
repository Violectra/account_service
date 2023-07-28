package account.config.handlers;

import account.services.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;

import java.io.IOException;

public class SuccessFilter implements Filter {

    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();
    private HttpServletRequest httpServletRequest;
    private UserService userService;

    public SuccessFilter(HttpServletRequest httpServletRequest, UserService userService) {
        this.httpServletRequest = httpServletRequest;
        this.userService = userService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Authentication authentication = securityContextHolderStrategy.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String name = authentication.getName().toLowerCase();
            userService.resetAttempts(name);
            System.out.println("!!!!! attempts reset for: " + name);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
