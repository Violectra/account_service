package account.models.dto;

import jakarta.validation.constraints.*;

import static account.controllers.EmployeeController.PERIOD;

public class PaymentDTO {

    @NotBlank(message = "Email is mandatory")
    @Email(regexp = ".+@acme\\.com")
    private String employee;
    @Pattern(regexp = PERIOD)
    @NotBlank(message = "Period is mandatory")
    private String period;
    @NotNull
    @Min(value = 0, message = "Salary must not be negative")
    private long salary;

    public PaymentDTO() {
    }

    public String getEmployee() {
        return employee;
    }

    public String getPeriod() {
        return period;
    }

    public long getSalary() {
        return salary;
    }
}
