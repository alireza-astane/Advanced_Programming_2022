package database.models;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import logic.enums.ExerciseType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "exercise")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    private Course course;

    @Column(nullable = false)
    private String name;

    @Column(length = 4000)
    private String description = "";

    @Column(nullable = false)
    private LocalDateTime opensAt;

    @Column(nullable = false)
    private LocalDateTime closesAt;

    @Column(nullable = false)
    private LocalDateTime freeUntil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExerciseType answerType = ExerciseType.TEXT;

    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private StoredFile storedFile;

    public byte[] getFile() {
        return storedFile == null ? null : storedFile.getBytes();
    }

    public void setFile(byte[] file) {
        storedFile = file == null ? null : new StoredFile(file);
    }

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();
}
