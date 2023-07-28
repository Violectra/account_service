package account.models.dto;

public class PaymentResponseDTO {

    private String name;

    public PaymentResponseDTO(String name, String lastname, String period, String salary) {
        this.name = name;
        this.lastname = lastname;
        this.period = period;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    private String lastname;


    public String getPeriod() {
        return period;
    }

    public String getSalary() {
        return salary;
    }

    private String period;
    private String salary;
}
