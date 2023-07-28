package account.repositories;

import account.models.entities.GroupEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends CrudRepository<GroupEntity, Long> {
    GroupEntity findByCode(String code);
}

