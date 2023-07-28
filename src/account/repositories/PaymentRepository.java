package account.repositories;

import account.models.entities.PaymentEntity;
import account.models.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends CrudRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByEmployeeAndPeriod(UserEntity user, String period);
    List<PaymentEntity> findByEmployee(UserEntity user);
}

