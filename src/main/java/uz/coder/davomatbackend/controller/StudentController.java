package uz.coder.davomatbackend.controller;

import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.coder.davomatbackend.model.*;
import uz.coder.davomatbackend.service.StudentService;
import uz.coder.davomatbackend.service.TelegramUserService;
import uz.coder.davomatbackend.service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService service;
    private final UserService userService;
    private final TelegramUserService telegramUserService;

    public StudentController(StudentService service, UserService userService, TelegramUserService telegramUserService) {
        this.service = service;
        this.userService = userService;
        this.telegramUserService = telegramUserService;
    }

    // 🔑 Token orqali current userni olish
    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return userService.findByEmail(userDetails.getUsername());
    }

    @PostMapping("/addStudent")
    public ResponseEntity<Response<Student>> addStudent(@RequestBody AddStudent addStudent) {
        try {
            Student student = new Student();
            student.setFullName(addStudent.getFullName());
            student.setPhoneNumber(addStudent.getPhoneNumber());
            student.setGroupId(addStudent.getGroupId());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, service.save(student)));
        } catch (Exception ex) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, ex.getMessage()));
        }
    }

    @PutMapping("/editStudent")
    public ResponseEntity<Response<Student>> editStudent(@RequestBody UpdateStudent updateStudent) {
        try {
            Student student = new Student();
            student.setFullName(updateStudent.getFullName());
            student.setPhoneNumber(updateStudent.getPhoneNumber());
            student.setGroupId(updateStudent.getGroupId());
            student.setId(updateStudent.getId());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, service.edit(student)));
        } catch (Exception ex) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, ex.getMessage()));
        }
    }

    @DeleteMapping("/deleteStudent/{id}")
    public ResponseEntity<Response<Integer>> deleteStudent(@PathVariable long id) {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, service.deleteById(id)));
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<Student>> findById(@PathVariable long id) {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, service.findById(id)));
        } catch (Exception ex) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, ex.getMessage()));
        }
    }

    @GetMapping("/findByGroupId/{groupId}")
    public ResponseEntity<Response<List<Student>>> findByGroupId(@PathVariable long groupId) {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, service.findAllStudentByGroupId(groupId)));
        } catch (Exception ex) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, ex.getMessage()));
        }
    }

    @PostMapping(value = "/upload-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<String>> uploadExcel(@RequestParam MultipartFile file) {
        try {
            User user = getCurrentUser(); // token orqali userId
            if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx")) {
                return ResponseEntity.badRequest()
                        .body(new Response<>(500, null, "Faqat .xlsx formatdagi fayl yuklash mumkin"));
            }

            boolean result = service.saveAllByExcel(file, user.getId());
            if (result) {
                return ResponseEntity.ok(new Response<>(200, "Fayl muvaffaqiyatli saqlandi", null));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new Response<>(500, null, "Xatolik yuz berdi"));
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(500, null, "Xatolik: " + ex.getMessage()));
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportXlsx() throws IOException {
        User user = getCurrentUser(); // token orqali userId
        List<Student> students = service.getStudentsByUserId(user.getId());
        byte[] fileBytes = service.exportStudentsToXlsx(students);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("students_user_" + user.getId() + ".xlsx").build());

        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/seeCourses")
    public ResponseEntity<Response<List<StudentCourseGroup>>> findAllCourses() {
        try {
            User user = getCurrentUser(); // token orqali userId
            List<StudentCourseGroup> result = service.getCourseAndGroupByUserId(user.getId());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, result));
        } catch (Exception ex) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, ex.getMessage()));
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<Response<Balance>> getUserBalanceByTelegram() {
        try {
            User user = getCurrentUser(); // token orqali userId
            TelegramUser telegramUserServiceByUserId = telegramUserService.findByUserId(user.getId());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, service.getUserBalanceByTelegramUserId(telegramUserServiceByUserId.getTelegramUserId())));
        } catch (Exception ex) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, ex.getMessage()));
        }
    }

    @GetMapping("/findByGroupIdAndUserId")
    public ResponseEntity<Response<Student>> findByGroupIdAndUserId(@RequestParam long groupId) {
        try {
            User user = getCurrentUser(); // token orqali userId
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, service.findByGroupIdAndUserId(user.getId(), groupId)));
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }
}