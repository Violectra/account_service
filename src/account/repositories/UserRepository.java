package account.repositories;

import account.models.entities.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
    List<UserEntity> findByEmailIn(List<String> emails);

    void deleteByEmail(String email);

    @Query("UPDATE UserEntity SET attempts = attempts + 1 WHERE email = ?1")
    @Modifying
    public void incrementAttempts(String email);

    @Query("UPDATE UserEntity SET attempts = 0 WHERE email = ?1")
    @Modifying
    public void resetAttempts(String email);
}

