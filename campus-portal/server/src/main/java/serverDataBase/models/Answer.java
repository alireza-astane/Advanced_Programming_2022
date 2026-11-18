package serverDataBase.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "answer")
public class Answer {


    @Id
    @GeneratedValue
    @Column(name="answerId")
    int answerId;


    @Column(name = "answerStudentId",nullable = false)
    int answerStudentID;

    @Column(name = "answerExerciseId",nullable = false)
    int answerExerciseId;

    @Column(name =  "answerScore")
    double answerScore;

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public int getAnswerStudentID() {
        return answerStudentID;
    }

    public void setAnswerStudentID(int answerStudentID) {
        this.answerStudentID = answerStudentID;
    }

    public int getAnswerExerciseId() {
        return answerExerciseId;
    }

    public void setAnswerExerciseId(int answerExerciseId) {
        this.answerExerciseId = answerExerciseId;
    }

    public double getAnswerScore() {
        return answerScore;
    }

    public void setAnswerScore(double answerScore) {
        this.answerScore = answerScore;
    }

    public LocalDateTime getAnswerUploadTime() {
        return answerUploadTime;
    }

    public void setAnswerUploadTime(LocalDateTime answerUploadTime) {
        this.answerUploadTime = answerUploadTime;
    }

    public byte[] getAnswerFile() {
        return answerFile;
    }

    public void setAnswerFile(byte[] answerFile) {
        this.answerFile = answerFile;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    @Column(name =  "answerUploadTime",nullable = false)
    LocalDateTime answerUploadTime;

    @Lob
    @Column(name =  "answerFile")
    byte[] answerFile;

    @Column(name = "answerText",length = 4000)
    String answerText;
}
