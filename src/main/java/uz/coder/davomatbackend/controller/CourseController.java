package uz.coder.davomatbackend.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import uz.coder.davomatbackend.model.*;
import uz.coder.davomatbackend.service.CourseService;
import uz.coder.davomatbackend.service.UserService;

import java.util.List;

import static uz.coder.davomatbackend.todo.Strings.*;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    private final CourseService service;
    private final UserService userService;

    public CourseController(CourseService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    // Token orqali userId olinadi
    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return userService.findByEmail(userDetails.getUsername());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response<Integer>> deleteById(@PathVariable long id) {
        try {
            User user = getCurrentUser();
            if (user.getRole().equals(ROLE_ADMIN) || user.getRole().equals(ROLE_TEACHER)) {
                int data = service.deleteById(id);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new Response<>(200, data));
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new Response<>(500, YOU_ARE_A_STUDENT));
            }
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<Course>> findById(@PathVariable long id) {
        try {
            Course course = service.findById(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, course));
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Response<Course>> create(@RequestBody AddCourse addCourse) {
        try {
            Course course = new Course();
            User user = getCurrentUser();
            course.setTitle(addCourse.getTitle());
            course.setDescription(addCourse.getDescription());
            course.setUserId(user.getId());
            if (user.getRole().equals(ROLE_ADMIN) || user.getRole().equals(ROLE_TEACHER)) {
                Course save = service.save(course);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new Response<>(200, save));
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new Response<>(500, YOU_ARE_A_STUDENT));
            }
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Response<Course>> update(@RequestBody UpdateCourse updateCourse) {
        try {
            Course course = new Course();
            course.setTitle(updateCourse.getTitle());
            course.setDescription(updateCourse.getDescription());
            course.setId(updateCourse.getId());
            User user = getCurrentUser();
            course.setUserId(user.getId());
            if (user.getRole().equals(ROLE_ADMIN) || user.getRole().equals(ROLE_TEACHER)) {
                Course edit = service.edit(course);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new Response<>(200, edit));
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new Response<>(500, YOU_ARE_A_STUDENT));
            }
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }

    @GetMapping("/getAllCourses")
    public ResponseEntity<Response<List<Course>>> getAllCourses() {
        try {
            User user = getCurrentUser(); // token asosida userId
            List<Course> all = service.findAll(user.getId());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, all));
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }
}