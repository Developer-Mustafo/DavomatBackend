package uz.coder.davomatbackend.db.model;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "course")
public class CourseDbModel {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "userId", nullable = false)
    private long userId;

    public CourseDbModel(String title, String description, long userId) {
        this.title = title;
        this.description = description;
        this.userId = userId;
    }
}
