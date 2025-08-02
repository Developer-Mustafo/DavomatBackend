package uz.coder.davomatbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.coder.davomatbackend.model.Response;
import uz.coder.davomatbackend.model.Course;
import uz.coder.davomatbackend.model.User;
import uz.coder.davomatbackend.service.CourseService;
import uz.coder.davomatbackend.service.UserService;

import java.util.List;

import static uz.coder.davomatbackend.todo.Strings.*;

@RequestMapping("/api/course")
@RestController
public class CourseController {
    private final CourseService service;
    private final UserService userService;

    @Autowired
    public CourseController(CourseService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }
    @DeleteMapping("/delete/{userId}/{id}")
    public ResponseEntity<Response<Integer>> deleteById(@PathVariable("id") long id, @PathVariable("userId")  long userId) {
        try {
            User user = userService.findById(userId);
            if (user.getRole().equals(ROLE_ADMIN) ||  user.getRole().equals(ROLE_TEACHER)) {
                int data = service.deleteById(id);
                return  ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new Response<>(200, data));
            } else {
                return  ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new Response<>(500, YOU_ARE_A_STUDENT));
            }
        }catch (Exception e){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Response<Course>> findById(@PathVariable("id") long id) {
        try {
            Course course = service.findById(id);
            return  ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, course));
        }catch (Exception e){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Response<Course>> register(@RequestBody Course course) {
        try {
            long userId = course.getUserId();
            User user = userService.findById(userId);
            if (user.getRole().equals(ROLE_ADMIN) || user.getRole().equals(ROLE_TEACHER)) {
                Course save = service.save(course);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new Response<>(200, save));
            }
            else {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new Response<>(500, YOU_ARE_A_STUDENT));
            }
        }catch (Exception e){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Response<Course>> update(@RequestBody Course course) {
        try {
            long userId = course.getUserId();
            User user = userService.findById(userId);
            if (user.getRole().equals(ROLE_ADMIN) || user.getRole().equals(ROLE_TEACHER)) {
                Course edit = service.edit(course);
                return  ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new Response<>(200, edit));
            }else {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new Response<>(500, YOU_ARE_A_STUDENT));
            }
        }catch (Exception e){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }
    @GetMapping("/getAllCourses/{userId}")
    public ResponseEntity<Response<List<Course>>> getAllCourses(@PathVariable("userId") long userId) {
        try {
            List<Course> all = service.findAll(userId);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new  Response<>(200, all));
        }catch (Exception e){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }
}