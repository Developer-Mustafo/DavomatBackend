package uz.coder.davomatbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.coder.davomatbackend.model.Attendance;
import uz.coder.davomatbackend.model.Response;
import uz.coder.davomatbackend.service.AttendanceService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/add")
    public ResponseEntity<Response<Attendance>> save(@RequestBody Attendance attendance) {
        Attendance saved = attendanceService.save(attendance);
        if (saved == null)
            return ResponseEntity.badRequest().body(new Response<>(500, "Already exists or invalid"));
        return ResponseEntity.ok(new Response<>(200, saved));
    }

    @PutMapping("/edit")
    public ResponseEntity<Response<Attendance>> update(@RequestBody Attendance attendance) {
        Attendance updated = attendanceService.update(attendance);
        if (updated == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new Response<>(200, updated));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable long id) {
        boolean deleted = attendanceService.delete(id);
        if (deleted)
            return ResponseEntity.ok(new Response<>(200, "Deleted successfully"));
        return ResponseEntity.status(404).body(new Response<>(404, "Not found"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Response<Attendance>> findById(@PathVariable long id) {
        try {
            Attendance attendance = attendanceService.findById(id);
            return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new Response<>(200, attendance));
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new  Response<>(500, e.getMessage()));
        }
    }

    @PostMapping("/excel")
    public ResponseEntity<Response<String>> importExcel(@RequestParam MultipartFile file) {
        boolean result = attendanceService.saveAllByExcel(file);
        if (result)
            return ResponseEntity.ok(new Response<>(200, "Imported successfully", null));
        return ResponseEntity.badRequest().body(new Response<>(500, null, "Import failed"));
    }

    @GetMapping("/export/{userId}/{year}/{month}")
    public ResponseEntity<byte[]> exportAttendance(
            @PathVariable("userId") long userId,
            @PathVariable("year") int year,
            @PathVariable("month") int month,
            @RequestParam(name = "courseId", required = false) Long courseId,
            @RequestParam(name = "groupId", required = false) Long groupId
    ) {
        try {
            // Excel faylni yaratish va byte[] olish
            byte[] data = attendanceService.exportToExcelByMonth(userId, courseId, groupId, year, month);

            // Response yuborish, fayl nomi va media type to‘g‘ri belgilandi
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"attendance.xlsx\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(data);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null); // server xatosi bo‘lsa
        }
    }
    @GetMapping("/student/{studentId}")
    public ResponseEntity<Response<List<Attendance>>> getByStudent(@PathVariable long studentId) {
        try {
            List<Attendance> attendanceList = attendanceService.getAllByStudentId(studentId);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, attendanceList));
        }catch (Exception e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }
}