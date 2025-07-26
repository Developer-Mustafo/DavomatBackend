package uz.coder.davomatbackend.db.model;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "groups")
@Entity
public class GroupDbModel {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "courseId", nullable = false)
    private long courseId;

    public GroupDbModel(String title, long courseId) {
        this.title = title;
        this.courseId = courseId;
    }
}
