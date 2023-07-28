package account.config.handlers;

import account.services.UserService;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener implements
        ApplicationListener<AuthenticationSuccessEvent> {

    private UserService userService;


    public AuthenticationSuccessListener(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String name = event.getAuthentication().getName().toLowerCase();
        userService.resetAttempts(name);
    }
}
