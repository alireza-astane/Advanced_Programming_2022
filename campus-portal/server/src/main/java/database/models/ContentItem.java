package database.models;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "content_item")
public class ContentItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    private CourseContent content;

    @Column(length = 4000)
    private String text = "";

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
}
