package account.models.dto;

public class EmailStatusResponseDTO {
    private String email;
    private String status;

    public EmailStatusResponseDTO(String email, String status) {
        this.email = email;
        this.status = status;
    }

    public EmailStatusResponseDTO() {
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }
}
