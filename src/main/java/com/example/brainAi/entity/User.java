package com.example.brainAi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(nullable = false, length = 45)
    private String firstName;
    @Column(nullable = false, length = 45)
    private String lastName;
    @Column(nullable = false, unique = true, length = 45)
    private String email;
    @Column(nullable = false, unique = true)
    private String password;
    @ToString.Exclude
    @JsonIgnore
    // Specifies a many-to-many relationship between User and Role entities.
    // FetchType.EAGER loads all roles of a user at once.
    // CascadeType.ALL specifies that all operations (persist, remove, refresh, merge, and detach) should be cascaded.
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    // Specifies the join table name and columns for the relationship.
    // The "joinColumns" element specifies the mapping for the owning side of the association.
    // The "inverseJoinColumns" element specifies the mapping for the non-owning side of the association.
    @JoinTable(
            name = "users_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private List<Role> roles = new ArrayList<>();
    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private List<Doctor> doctors;
    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private List<Patient> patients;


    public User(String firstName, String lastName, String email, String password, List<Role> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;

        this.roles = roles;
    }


}
