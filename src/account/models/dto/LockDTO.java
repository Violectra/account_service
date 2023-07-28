package account.models.dto;

public class LockDTO {
    private String user;
    private LockOperation operation;

    public LockDTO() {
    }

    public String getUser() {
        return user;
    }

    public LockOperation getOperation() {
        return operation;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setOperation(LockOperation operation) {
        this.operation = operation;
    }
}
