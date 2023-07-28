package account.models.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "payments", uniqueConstraints = @UniqueConstraint(columnNames = {"period", "user_id"}))
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_id_seq")
    @SequenceGenerator(name = "payment_id_seq", sequenceName = "PAYMENT_ID_SEQ", allocationSize = 100)
    private long id;
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private UserEntity employee;
    private String period;

    public void setSalary(long salary) {
        this.salary = salary;
    }

    private long salary;

    public PaymentEntity(UserEntity employee, String period, long salary) {
        this.employee = employee;
        this.period = period;
        this.salary = salary;
    }

    public PaymentEntity() {
    }

    public long getId() {
        return id;
    }

    public UserEntity getEmployee() {
        return employee;
    }

    public String getPeriod() {
        return period;
    }

    public long getSalary() {
        return salary;
    }
}
