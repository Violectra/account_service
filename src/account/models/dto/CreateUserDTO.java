package account.models.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreateUserDTO {
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank(message = "Last name is mandatory")
    private String lastname;
    @NotBlank(message = "Email is mandatory")
    @Email(regexp = ".+@acme\\.com")
    private String email;
    @NotBlank(message = "Password is mandatory")
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
