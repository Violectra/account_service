package account.models.dto;

public class UserStatusResponseDTO {
    private String user;
    private String status;

    public UserStatusResponseDTO(String user, String status) {
        this.user = user;
        this.status = status;
    }

    public UserStatusResponseDTO() {
    }

    public String getUser() {
        return user;
    }

    public String getStatus() {
        return status;
    }
}
