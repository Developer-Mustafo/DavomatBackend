package uz.coder.davomatbackend.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AddCourse {
    private String title;
    private String description;
    public AddCourse(String title) {
        this.title = title;
    }
}
