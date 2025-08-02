package uz.coder.davomatbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.coder.davomatbackend.db.CourseDatabase;
import uz.coder.davomatbackend.db.model.CourseDbModel;
import uz.coder.davomatbackend.model.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CourseService {
    private final CourseDatabase database;

    @Autowired
    public CourseService(CourseDatabase database) {
        this.database = database;
    }
    public Course save(Course course) {
        CourseDbModel save = database.save(new CourseDbModel(course.getTitle(), course.getDescription(), course.getUserId()));
        return new Course(save.getId(), save.getTitle(), save.getDescription(), save.getUserId());
    }
    public Course edit(Course course) {
        database.update(course.getId(), course.getTitle(), course.getDescription(), course.getUserId());
        CourseDbModel save = database.findById(course.getId()).orElse(null);
        assert save != null;
        return new Course(save.getId(), save.getTitle(), save.getDescription(), save.getUserId());
    }
    public Course findById(long id) {
        CourseDbModel course = database.findById(id).orElse(null);
        assert course != null;
        return new Course(course.getId(), course.getTitle(), course.getDescription(), course.getUserId());
    }
    public int deleteById(long id) {
        CourseDbModel course = database.findById(id).orElse(null);
        assert course != null;
        database.delete(course);
        return 1;
    }
    public List<Course> findAll(long userId) {
        List<CourseDbModel> allByUserId = database.findAllByUserId(userId);
        return allByUserId.stream().map(item -> new Course(item.getId(), item.getTitle(), item.getDescription(), item.getUserId())).collect(Collectors.toList());
    }
}