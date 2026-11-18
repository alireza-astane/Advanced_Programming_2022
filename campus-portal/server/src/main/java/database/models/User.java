package database.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import logic.enums.UserType;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "portal_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    @Column(nullable = false)
    private LocalDateTime lastLogin;

    @Lob
    private byte[] photo;

    private String email = "";
    private String phone = "";
    private String nationalNumber = "";
    private boolean canLogin = true;

    @OneToOne(mappedBy = "user")
    private Student student;

    @OneToOne(mappedBy = "user")
    private Master master;
}
