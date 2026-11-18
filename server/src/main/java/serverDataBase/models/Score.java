package serverDataBase.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import logic.enums.ScoreType;

@Entity
@Getter
@Setter
@Table(name = "Score")
public class Score {
    @Id
    @GeneratedValue
    @Column(name = "scoreId")
    int scoreId;

    @Column(name = "scoreCourseId",nullable = false)
    int scoreCourseId;

    @Column(name = "scoreType")
    @Enumerated(EnumType.ORDINAL)
    ScoreType scoreType=ScoreType.NA;


    @Column(name = "scoreValue")
    double scoreValue;

    @Column(name = "scoreStudentId",nullable = false)
    int scoreStudentId;


    @Column(name="Units",nullable = false)
    int units;

    @Column(name = "ScoreProtestation",length = 4000)
    String scoreProtestation="";

    @Column(name = "scoreProtestationAnswer",length = 4000)
    String scoreProtestationAnswer="";

    public int getScoreId() {
        return scoreId;
    }

    public void setScoreId(int scoreId) {
        this.scoreId = scoreId;
    }

    public int getScoreCourseId() {
        return scoreCourseId;
    }

    public void setScoreCourseId(int scoreCourseId) {
        this.scoreCourseId = scoreCourseId;
    }

    public ScoreType getScoreType() {
        return scoreType;
    }

    public void setScoreType(ScoreType scoreType) {
        this.scoreType = scoreType;
    }

    public double getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(double scoreValue) {
        this.scoreValue = scoreValue;
    }

    public int getScoreStudentId() {
        return scoreStudentId;
    }

    public void setScoreStudentId(int scoreStudentId) {
        this.scoreStudentId = scoreStudentId;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public String getScoreProtestation() {
        return scoreProtestation;
    }

    public void setScoreProtestation(String scoreProtestation) {
        this.scoreProtestation = scoreProtestation;
    }

    public String getScoreProtestationAnswer() {
        return scoreProtestationAnswer;
    }

    public void setScoreProtestationAnswer(String scoreProtestationAnswer) {
        this.scoreProtestationAnswer = scoreProtestationAnswer;
    }



}
