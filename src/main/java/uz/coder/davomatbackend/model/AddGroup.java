package uz.coder.davomatbackend.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AddGroup {
    private String title;
    private long courseId;
}
