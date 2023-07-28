package account.utils;

import account.models.dto.UserDTO;
import account.models.entities.GroupEntity;
import account.models.entities.UserEntity;

import java.util.List;

public class UserUtils {

    private UserUtils() {}
    public static UserDTO convertToDto(UserEntity u) {
        List<String> groups = u.getUserGroups().stream().map(GroupEntity::getName).sorted().toList();
        return new UserDTO(u.getId(), u.getName(), u.getLastname(), u.getEmail(), groups);
    }
}