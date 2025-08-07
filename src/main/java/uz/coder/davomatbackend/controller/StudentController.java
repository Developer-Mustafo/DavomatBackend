package uz.coder.davomatbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.coder.davomatbackend.model.Student;
import uz.coder.davomatbackend.model.Response;
import uz.coder.davomatbackend.service.StudentService;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    private final StudentService service;

    @Autowired
    public StudentController(StudentService service) {
        this.service = service;
    }

    @PostMapping("/addStudent")
    public ResponseEntity<Response<Student>> addStudent(@RequestBody Student student) {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, service.save(student)));
        }catch (Exception ex){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, ex.getMessage()));
        }
    }
    @PutMapping("/editStudent")
    public ResponseEntity<Response<Student>> editStudent(@RequestBody Student student) {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, service.edit(student)));
        }catch (Exception ex){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, ex.getMessage()));
        }
    }
    @DeleteMapping("/deleteStudent/{id}")
    public ResponseEntity<Response<Integer>> deleteStudent(@PathVariable("id") long id) {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, service.deleteById(id)));
        } catch (Exception e) {
            return  ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Response<Student>> findById(@PathVariable("id") long id) {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, service.findById(id)));
        }catch (Exception ex){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, ex.getMessage()));
        }
    }
    @GetMapping("/findByGroupId/{groupId}")
    public ResponseEntity<Response<List<Student>>> findByGroupId(@PathVariable("groupId") long groupId) {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, service.findAllStudentByGroupId(groupId)));
        }catch (Exception ex){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, ex.getMessage()));
        }
    }

    @PostMapping(value = "/upload-excel/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<String>> uploadExcel(@RequestParam MultipartFile file, @PathVariable("userId") long userId) {
        try {
            // Faqat .xls fayllar uchun ruxsat beriladi
            if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx")) {
                return ResponseEntity.badRequest()
                        .body(new Response<>(400, "Faqat .xls formatdagi fayl yuklash mumkin"));
            }

            boolean result = service.saveAllByEXEL(file, userId);
            if (result) {
                return ResponseEntity.ok(new Response<>(200, "Fayl muvaffaqiyatli saqlandi"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new Response<>(500, "Xatolik yuz berdi"));
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Response<>(500, "Xatolik: " + ex.getMessage()));
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportXlsx(@RequestParam("userId") Long userId) throws IOException {
        List<Student> students = service.getStudentsByUserId(userId);
        byte[] fileBytes = service.exportStudentsToXlsx(students);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("students_user_" + userId + ".xlsx").build());

        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }
}
