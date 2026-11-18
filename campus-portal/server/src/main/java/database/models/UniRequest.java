package database.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import logic.enums.UniRequestResult;
import logic.enums.UniRequestType;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "uni_request")
public class UniRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UniRequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UniRequestResult result = UniRequestResult.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(length = 4000)
    private String text = "";

    private Integer courseId;
    private Integer otherCourseId;

    @ManyToOne
    private User requester;

    @ManyToOne
    private User responsible;

    @ManyToOne
    private User secondResponsible;

    @Enumerated(EnumType.STRING)
    private UniRequestResult firstAnswer;

    @Enumerated(EnumType.STRING)
    private UniRequestResult secondAnswer;
}
