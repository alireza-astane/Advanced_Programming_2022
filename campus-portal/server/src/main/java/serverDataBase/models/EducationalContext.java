package serverDataBase.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import logic.enums.EducationalContextType;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "EducationalContext")
public class EducationalContext {

    @Id
    @GeneratedValue
    @Column(name = "educationalContextId")
    int educationalContextId;

    @Column(name = "educationalContextName")
    String educationalContextName="";

    @Column(name = "educationalContextType",nullable = false)
    @Enumerated(EnumType.ORDINAL)
    EducationalContextType educationalContextType;

    @Column(name = "educationalContextTime",nullable = false)
    LocalDateTime educationalContextTime;

    @Column(name = "educationalContextCourseId",nullable = false)
    int educationalContextCourseId;

    @Lob
    @Column(name = "educationalContextFile")
    byte[] educationalContextFile;

    @Column(name ="educationalContextText",length = 4000)
    String educationalContextText="";

    public int getEducationalContextId() {
        return educationalContextId;
    }

    public void setEducationalContextId(int educationalContextId) {
        this.educationalContextId = educationalContextId;
    }

    public String getEducationalContextName() {
        return educationalContextName;
    }

    public void setEducationalContextName(String educationalContextName) {
        this.educationalContextName = educationalContextName;
    }

    public EducationalContextType getEducationalContextType() {
        return educationalContextType;
    }

    public void setEducationalContextType(EducationalContextType educationalContextType) {
        this.educationalContextType = educationalContextType;
    }

    public LocalDateTime getEducationalContextTime() {
        return educationalContextTime;
    }

    public void setEducationalContextTime(LocalDateTime educationalContextTime) {
        this.educationalContextTime = educationalContextTime;
    }

    public int getEducationalContextCourseId() {
        return educationalContextCourseId;
    }

    public void setEducationalContextCourseId(int educationalContextCourseId) {
        this.educationalContextCourseId = educationalContextCourseId;
    }

    public byte[] getEducationalContextFile() {
        return educationalContextFile;
    }

    public void setEducationalContextFile(byte[] educationalContextFile) {
        this.educationalContextFile = educationalContextFile;
    }

    public String getEducationalContextText() {
        return educationalContextText;
    }

    public void setEducationalContextText(String educationalContextText) {
        this.educationalContextText = educationalContextText;
    }


}
