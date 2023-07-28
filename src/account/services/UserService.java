package account.services;


import account.models.dto.CreateUserDTO;
import account.models.exceptions.BadRequestException;
import account.models.exceptions.NotFoundException;
import account.models.dto.LockOperation;
import account.utils.UserUtils;
import account.models.dto.UserDTO;
import account.models.entities.GroupEntity;
import account.models.entities.enums.Role;
import account.models.entities.UserEntity;
import account.repositories.GroupRepository;
import account.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService implements UserDetailsService, UserDetailsPasswordService {

    public static final int MIN_PASSWORD_LENGTH = 12;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    private final PasswordEncoder passwordEncoder;

    private final List<String> blackPasswords = List.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");


    public UserService(UserRepository userRepository, GroupRepository groupRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public UserEntity signup(@Valid CreateUserDTO userDto) {
        validate(userDto.getPassword());
        UserEntity user = convert(userDto);
        if (userRepository.count() == 0) {
            user.getUserGroups().clear();
            user.getUserGroups().add(groupRepository.findByCode("administrator"));
        }
        return userRepository.save(user);

    }

    private UserEntity convert(CreateUserDTO userDto) {
        String password = passwordEncoder.encode(userDto.getPassword());
        HashSet<GroupEntity> roles = new HashSet<>();
        UserEntity userEntity = new UserEntity(userDto.getName(), userDto.getLastname(), userDto.getEmail().toLowerCase(), password, roles);
        try {
            GroupEntity group = groupRepository.findByCode(Role.USER.name().toLowerCase());
            roles.add(group);
        } catch (RuntimeException e) {
            throw new BadRequestException("Group not found");
        }
        return userEntity;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(username.toLowerCase());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    @Override
    public UserDetails updatePassword(UserDetails userDetails, String newPassword) {
        validate(newPassword);
        if (passwordEncoder.matches(newPassword, userDetails.getPassword())) {
            throw new BadRequestException("The passwords must be different!");
        }
        String encode = passwordEncoder.encode(newPassword);
        UserEntity userEntity = userRepository.findByEmail(userDetails.getUsername());
        userEntity.setPassword(encode);
        return userRepository.save(userEntity);
    }

    private void validate(String newPassword) {
        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new BadRequestException(String.format("Password length must be %d chars minimum!", MIN_PASSWORD_LENGTH));
        } else if (blackPasswords.contains(newPassword)) {
            throw new BadRequestException("The password is in the hacker's database!");
        }
    }

    public List<UserEntity> findByEmails(List<String> emails) {
        return userRepository.findByEmailIn(emails);
    }

    public void deleteByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found!");
        }
        if (user.isAdmin()) {
            throw new BadRequestException("Can't remove ADMINISTRATOR role!");
        }
        userRepository.delete(user);
    }

    @Transactional
    public UserDTO addUserRole(String user, Role role) {
        UserEntity userEntity = userRepository.findByEmail(user);
        if (userEntity == null) {
            throw new NotFoundException("User not found!");
        }
        if (role == Role.ADMINISTRATOR || userEntity.isAdmin()) {
            throw new BadRequestException("The user cannot combine administrative and business roles!");
        }
        GroupEntity gr = groupRepository.findByCode(role.name().toLowerCase());

        userEntity.getUserGroups().add(gr);
        return UserUtils.convertToDto(userEntity);
    }

    @Transactional
    public UserDTO removeUserRole(String user, Role role) {
        if (role == Role.ADMINISTRATOR) {
            throw new BadRequestException("Can't remove ADMINISTRATOR role!");
        }
        UserEntity userEntity = userRepository.findByEmail(user);
        if (userEntity == null) {
            throw new NotFoundException("User not found!");
        }
        if (userEntity.getUserGroups().stream().noneMatch(g -> g.getCode().equalsIgnoreCase(role.name()))) {
            throw new BadRequestException("The user does not have a role!");
        }
        if (userEntity.getUserGroups().size() < 2) {
            throw new BadRequestException("The user must have at least one role!");
        }
        userEntity.getUserGroups().removeIf(g -> g.getCode().equalsIgnoreCase(role.name()));

        return UserUtils.convertToDto(userEntity);
    }

    public List<UserDTO> findAll() {
        List<UserDTO> res = new ArrayList<>();
        userRepository.findAll().forEach(u -> res.add(UserUtils.convertToDto(u)));
        return res;
    }

    public void lock(String email, LockOperation operation) {
        UserEntity user = userRepository.findByEmail(email);
        if (user.isAdmin()) {
            throw new BadRequestException("Can't lock the ADMINISTRATOR!");
        }
        boolean locked = operation == LockOperation.LOCK;
        user.setLocked(locked);
        if (!locked) {
            user.setAttempts(0);
        }
        userRepository.save(user);
    }

    @Transactional
    public void resetAttempts(String email) {
        userRepository.resetAttempts(email);
    }
}
