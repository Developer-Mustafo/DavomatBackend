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

import static uz.coder.davomatbackend.todo.Strings.NOT_FOUND;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/add")
    public ResponseEntity<Response<Attendance>> save(@RequestBody Attendance attendance) {
        Attendance saved = attendanceService.save(attendance);
        if (saved == null)
            return ResponseEntity.badRequest().body(new Response<>(400, "Already exists or invalid"));
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
    public ResponseEntity<Response<String>> delete(@PathVariable Long id) {
        boolean deleted = attendanceService.delete(id);
        if (deleted)
            return ResponseEntity.ok(new Response<>(200, "Deleted successfully"));
        return ResponseEntity.status(404).body(new Response<>(404, "Not found"));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Attendance>> findById(@PathVariable Long id) {
        try {
            Attendance attendance = attendanceService.findById(id);
            if (attendance == null)
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new  Response<>(400, NOT_FOUND));
            else
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new Response<>(200, attendance));
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new  Response<>(400, NOT_FOUND));
        }
    }

    @PostMapping("/excel")
    public ResponseEntity<Response<String>> importExcel(@RequestParam MultipartFile file) {
        boolean result = attendanceService.saveAllByExcel(file);
        if (result)
            return ResponseEntity.ok(new Response<>(200, "Imported successfully"));
        return ResponseEntity.badRequest().body(new Response<>(400, "Import failed"));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam Long userId,
                                         @RequestParam int year,
                                         @RequestParam int month) throws IOException {
        byte[] data = attendanceService.exportToExcelByMonth(userId, year, month);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<Response<List<Attendance>>> getByStudent(@PathVariable Long studentId) {
        List<Attendance> attendanceList = attendanceService.getAllByStudentId(studentId);
        return ResponseEntity.ok(new Response<>(200, attendanceList));
    }
}