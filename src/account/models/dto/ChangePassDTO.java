package account.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChangePassDTO {
    @JsonProperty("new_password")
    private String newPassword;

    public ChangePassDTO(String newPassword) {
        this.newPassword = newPassword;
    }

    public ChangePassDTO() {
    }

    public String getNewPassword() {
        return newPassword;
    }
}
