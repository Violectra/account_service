package account.controllers;


import account.models.dto.PaymentDTO;
import account.models.dto.ResponseDTO;
import account.models.exceptions.BadRequestException;
import account.services.PaymentService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/acct")
public class AccountController {

    private final PaymentService paymentService;

    public AccountController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @PostMapping("/payments")
    public ResponseDTO postPayment(@RequestBody @Valid List<@Valid PaymentDTO> payments) {
        try {
            paymentService.paymentAdd(payments);
        } catch (RuntimeException e) {
            throw new BadRequestException("Failed to add payments");
        }
        return new ResponseDTO("Added successfully!");
    }

    @PutMapping("/payments")
    public ResponseDTO putPayment(@RequestBody @Valid PaymentDTO payment) {
        paymentService.paymentUpdate(payment);
        return new ResponseDTO("Updated successfully!");
    }
}
