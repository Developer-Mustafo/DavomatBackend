package uz.coder.davomatbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import uz.coder.davomatbackend.db.model.CourseDbModel;
import uz.coder.davomatbackend.db.model.GroupDbModel;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class StudentCourseGroup {
    private CourseDbModel course;
    private List<GroupDbModel> group;
}
