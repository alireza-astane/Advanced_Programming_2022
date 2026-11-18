package database.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Uploaded bytes, kept apart so that lists of messages and items stay light. */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "stored_file")
public class StoredFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Lob
    @Column(nullable = false)
    private byte[] bytes;

    public StoredFile(byte[] bytes) {
        this.bytes = bytes;
    }
}
