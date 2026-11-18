package database.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import logic.enums.ScoreType;

@Entity
@Getter
@Setter
@Table(name = "score")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    private Course course;

    @ManyToOne(optional = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScoreType scoreType = ScoreType.NOT_PUBLISHED;

    private boolean entered;
    private double scoreValue;

    @Column(length = 4000)
    private String protest = "";

    @Column(length = 4000)
    private String protestAnswer = "";
}
