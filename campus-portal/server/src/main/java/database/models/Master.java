package database.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import logic.enums.MasterGrade;
import logic.enums.MasterType;

@Entity
@Getter
@Setter
@Table(name = "master")
public class Master {
    @Id
    private int id;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @ManyToOne(optional = false)
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MasterType masterType = MasterType.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MasterGrade grade = MasterGrade.ASSISTANT_PROFESSOR;

    @Column(nullable = false, unique = true)
    private String masterNumber;

    private String roomNumber = "";
}
