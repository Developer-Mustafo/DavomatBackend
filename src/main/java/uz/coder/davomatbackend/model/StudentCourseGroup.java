package uz.coder.davomatbackend.model;

import lombok.*;
import uz.coder.davomatbackend.db.model.CourseDbModel;
import uz.coder.davomatbackend.db.model.GroupDbModel;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class StudentCourseGroup {
    private CourseDbModel course;
    private List<GroupDbModel> group;
}
