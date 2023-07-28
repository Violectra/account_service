package account.controllers;


import account.models.dto.ChangePassDTO;
import account.models.dto.CreateUserDTO;
import account.models.dto.EmailStatusResponseDTO;
import account.models.dto.UserDTO;
import account.models.entities.enums.Action;
import account.models.entities.GroupEntity;
import account.models.entities.enums.Role;
import account.models.entities.UserEntity;
import account.models.exceptions.BadRequestException;
import account.repositories.GroupRepository;
import account.services.UserService;
import account.services.EventService;
import account.utils.UserUtils;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService service;
    private final PasswordEncoder passwordEncoder;
    private final GroupRepository groupRepository;
    private final EventService eventService;

    public AuthController(UserService service, PasswordEncoder passwordEncoder, GroupRepository groupRepository, EventService eventService) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
        this.groupRepository = groupRepository;
        this.eventService = eventService;
    }

    @PostMapping("/signup")
    public UserDTO signup(@Valid @RequestBody CreateUserDTO userDto) {
        UserEntity u;
        try {
            u = service.signup(userDto);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("User exist!");
        }
        UserDTO response = UserUtils.convertToDto(u);
        eventService.createEvent(Action.CREATE_USER, "Anonymous", response.getEmail(), "/api/auth/signup");
        return response;
    }

    @PostMapping("/changepass")
    public EmailStatusResponseDTO changepass(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody ChangePassDTO changePassDTO) {
        service.updatePassword(userDetails, changePassDTO.getNewPassword());
        String username = userDetails.getUsername();
        eventService.createEvent(Action.CHANGE_PASSWORD, username, username, "/api/auth/changepass");
        return new EmailStatusResponseDTO(username, "The password has been updated successfully");
    }
}
