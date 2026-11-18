package serverDataBase.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import logic.enums.ExerciseType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "Exercise")
public class Exercise {

    @Id
    @GeneratedValue
    @Column(name = "exerciseId")
    int exerciseId;

    @Column(name = "exerciseStartTime",nullable = false)
    LocalDateTime exerciseStartTime;

    @Column(name = "exerciseEndTime",nullable = false)
    LocalDateTime exerciseEndTime;

    @Column(name = "exerciseDeadline",nullable = false)
    LocalDateTime exerciseDeadline;

    @Column(name = "exerciseType",nullable = false)
    @Enumerated(EnumType.ORDINAL)
    ExerciseType exerciseType;

    @Column(name = "exerciseContext",length = 4000)
    String exerciseContext="";

    @Column(name = "explanation",length = 4000)
    String explanation="";

    @Lob
    @Column(name = "exerciseFile")
    byte[] exerciseFile;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "exerciseId")
    private List<Answer> exerciseAnswers=new ArrayList<>();

    @Column(name = "exerciseCourseId",nullable = false)
    int exerciseCourseId;

    @Column(name = "exerciseName",nullable = false)
    String exerciseName;

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public LocalDateTime getExerciseStartTime() {
        return exerciseStartTime;
    }

    public void setExerciseStartTime(LocalDateTime exerciseStartTime) {
        this.exerciseStartTime = exerciseStartTime;
    }

    public LocalDateTime getExerciseEndTime() {
        return exerciseEndTime;
    }

    public void setExerciseEndTime(LocalDateTime exerciseEndTime) {
        this.exerciseEndTime = exerciseEndTime;
    }

    public LocalDateTime getExerciseDeadline() {
        return exerciseDeadline;
    }

    public void setExerciseDeadline(LocalDateTime exerciseDeadline) {
        this.exerciseDeadline = exerciseDeadline;
    }

    public ExerciseType getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(ExerciseType exerciseType) {
        this.exerciseType = exerciseType;
    }

    public String getExerciseContext() {
        return exerciseContext;
    }

    public void setExerciseContext(String exerciseContext) {
        this.exerciseContext = exerciseContext;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public byte[] getExerciseFile() {
        return exerciseFile;
    }

    public void setExerciseFile(byte[] exerciseFile) {
        this.exerciseFile = exerciseFile;
    }

    public List<Answer> getExerciseAnswers() {
        return exerciseAnswers;
    }

    public void setExerciseAnswers(List<Answer> exerciseAnswers) {
        this.exerciseAnswers = exerciseAnswers;
    }

    public int getExerciseCourseId() {
        return exerciseCourseId;
    }

    public void setExerciseCourseId(int exerciseCourseId) {
        this.exerciseCourseId = exerciseCourseId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }
}
