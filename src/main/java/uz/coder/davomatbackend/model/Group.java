package uz.coder.davomatbackend.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Group {
    private long id;
    private String title;
    private long courseId;

    public Group(String title, long courseId) {
        this.title = title;
        this.courseId = courseId;
    }
}
