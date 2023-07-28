package account.models.dto;

import jakarta.validation.constraints.NotBlank;

public class RoleRequestDTO {
    @NotBlank(message = "Email is mandatory")
    private String user;
    @NotBlank
    private String role;
    @NotBlank
    private String operation;


    public RoleRequestDTO() {
    }

    public String getUser() {
        return user;
    }

    public String getRole() {
        return role;
    }

    public String getOperation() {
        return operation;
    }
}
