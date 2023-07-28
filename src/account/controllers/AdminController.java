package account.controllers;


import account.models.dto.*;
import account.models.entities.enums.Action;
import account.models.entities.enums.Operation;
import account.models.entities.enums.Role;
import account.models.exceptions.NotFoundException;
import account.services.UserService;
import account.services.EventService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService service;
    private final EventService eventService;

    public AdminController(UserService service, EventService eventService) {
        this.service = service;
        this.eventService = eventService;
    }

    @DeleteMapping("/user/{email}")
    public UserStatusResponseDTO delete(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String email) {
        email = email.toLowerCase();
        service.deleteByEmail(email);
        eventService.createEvent(Action.DELETE_USER, userDetails.getUsername(), email, "/api/admin/user/role");
        return new UserStatusResponseDTO(email, "Deleted successfully!");
    }

    @GetMapping({"/user", "/user/"})
    public List<UserDTO> getUsers() {
        return service.findAll();
    }

    @PutMapping("/user/access")
    public ResponseDTO lock(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody LockDTO request) {
        String email = request.getUser().toLowerCase();
        service.lock(email, request.getOperation());
        Action action;
        String object;
        if (request.getOperation() == LockOperation.LOCK) {
            action = Action.LOCK_USER;
            object = String.format("Lock user %s", email);
        } else {
            action = Action.UNLOCK_USER;
            object = String.format("Unlock user %s", email);
        }
        eventService.createEvent(action, userDetails.getUsername(), object, "/api/admin/user/access");
        return new ResponseDTO(String.format("User %s %s!", email, action == Action.LOCK_USER ? "locked" : "unlocked"));
    }

    @PutMapping("/user/role")
    public UserDTO changeRoles(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody RoleRequestDTO roleRequestDTO) {
        Role role;
        try {
            role = Role.valueOf(roleRequestDTO.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Role not found!");
        }
        Operation op;
        try {
            op = Operation.valueOf(roleRequestDTO.getOperation().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Operation not found!");
        }
        String email = roleRequestDTO.getUser().toLowerCase();
        if (op == Operation.GRANT) {
            UserDTO userDTO = service.addUserRole(email, role);
            String object = String.format("Grant role %s to %s", role, email);
            eventService.createEvent(Action.GRANT_ROLE, userDetails.getUsername(), object, "/api/admin/user/role");
            return userDTO;
        } else {
            UserDTO userDTO = service.removeUserRole(email, role);
            String object = String.format("Remove role %s from %s", role, email);
            eventService.createEvent(Action.REMOVE_ROLE, userDetails.getUsername(), object, "/api/admin/user/role");
            return userDTO;
        }
    }
}
