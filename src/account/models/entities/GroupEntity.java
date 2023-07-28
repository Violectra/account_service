package account.models.entities;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "principle_groups")
public class GroupEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;
    private String name;

    @ManyToMany(mappedBy = "userGroups")
    private Set<UserEntity> users;

    public GroupEntity(String code, String name, Set<UserEntity> users) {
        this.code = code;
        this.name = name;
        this.users = users;
    }

    public GroupEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Set<UserEntity> getUsers() {
        return users;
    }
}