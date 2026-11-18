package database.models;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    private Chat chat;

    @ManyToOne(optional = false)
    private User sender;

    @Column(nullable = false)
    private LocalDateTime sentAt = LocalDateTime.now();

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
