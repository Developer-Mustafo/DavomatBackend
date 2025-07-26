package uz.coder.davomatbackend.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Course {
    private long id;
    private String title;
    private String description;
    private long userId;

    public Course(String title, String description, long userId) {
        this.title = title;
        this.description = description;
        this.userId = userId;
    }
}
