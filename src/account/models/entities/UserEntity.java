package account.models.entities;

import account.models.entities.enums.Role;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "USER_ID_SEQ", allocationSize = 100)
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private String password;

    private boolean locked;
    private int attempts;

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    @ManyToMany(fetch = FetchType.EAGER, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "user_groups",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"
            ))
    private Set<GroupEntity> userGroups = new HashSet<>();

    public UserEntity() {
    }

    public UserEntity(String name, String lastname, String email, String password, Set<GroupEntity> userGroups) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.userGroups = userGroups;
    }

    public boolean isAdmin() {
        return userGroups.stream().anyMatch(g -> g.getCode().toUpperCase().equals(Role.ADMINISTRATOR.name()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userGroups.stream().map(GroupEntity::getName).sorted().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public Set<GroupEntity> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(Set<GroupEntity> userGroups) {
        this.userGroups = userGroups;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
