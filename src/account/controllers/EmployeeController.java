package account.controllers;


import account.models.entities.UserEntity;
import account.services.PaymentService;
import account.services.UserService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/empl")
@Validated
public class EmployeeController {

    public static final String PERIOD = "(0[1-9]|1[1-2])-(19[0-9]{2}|20[0-9]{2})";
    private final UserService service;
    private final PaymentService paymentService;

    public EmployeeController(UserService service, PaymentService paymentService) {
        this.service = service;
        this.paymentService = paymentService;
    }

    @GetMapping("/payment")
    public ResponseEntity<?> getPayments(@AuthenticationPrincipal UserDetails userDetails,
                                         @Nullable @Valid @Pattern(regexp = PERIOD) @RequestParam String period) {
        String name = userDetails.getUsername().toLowerCase();
        UserEntity userEntity = service.findByEmail(name);
        if (period == null) {
            return ResponseEntity.ok().body(paymentService.findByUser(userEntity));
        }
        return ResponseEntity.ok().body(paymentService.findByUser(userEntity, period));
    }

}
