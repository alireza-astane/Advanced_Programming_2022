package database.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import logic.enums.DepartmentType;

@Entity
@Getter
@Setter
@Table(name = "department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private DepartmentType departmentType;

    @ManyToOne
    private Master head;

    @ManyToOne
    private Master deputy;
}
