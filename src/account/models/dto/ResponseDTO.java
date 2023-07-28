package account.models.dto;

public class ResponseDTO {
    private String status;

    public ResponseDTO(String status) {
        this.status = status;
    }

    public ResponseDTO() {
    }


    public String getStatus() {
        return status;
    }
}
