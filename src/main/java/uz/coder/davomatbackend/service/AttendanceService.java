package uz.coder.davomatbackend.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.coder.davomatbackend.db.AttendanceDatabase;
import uz.coder.davomatbackend.db.GroupDatabase;
import uz.coder.davomatbackend.db.StudentDatabase;
import uz.coder.davomatbackend.db.UserDatabase;
import uz.coder.davomatbackend.db.model.AttendanceDbModel;
import uz.coder.davomatbackend.db.model.GroupDbModel;
import uz.coder.davomatbackend.db.model.StudentDbModel;
import uz.coder.davomatbackend.model.Attendance;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static uz.coder.davomatbackend.todo.Strings.*;

@Slf4j
@Service
public class AttendanceService {
    private final AttendanceDatabase attendanceDatabase;
    private final StudentDatabase studentDatabase;
    private final GroupDatabase groupDatabase;
    private final UserDatabase userDatabase;

    @Autowired
    public AttendanceService(AttendanceDatabase attendanceDatabase, StudentDatabase studentDatabase,
                             GroupDatabase groupDatabase, UserDatabase userDatabase) {
        this.attendanceDatabase = attendanceDatabase;
        this.studentDatabase = studentDatabase;
        this.groupDatabase = groupDatabase;
        this.userDatabase = userDatabase;
    }

    public Attendance save(Attendance attendance) {
        Optional<AttendanceDbModel> existingOpt = attendanceDatabase.findByStudentIdAndDate(attendance.getStudentId(), attendance.getDate());

        if (existingOpt.isPresent()) {
            AttendanceDbModel existing = existingOpt.get();
            existing.setStatus(attendance.getStatus());
            AttendanceDbModel updated = attendanceDatabase.save(existing);
            return mapToDto(updated);
        } else {
            AttendanceDbModel saved = attendanceDatabase.save(new AttendanceDbModel(
                    attendance.getStudentId(), attendance.getDate(), attendance.getStatus()
            ));
            return mapToDto(saved);
        }
    }

    public boolean saveAllByExcel(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(0);

            List<AttendanceDbModel> saveList = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String phone = getCellStringValue(row.getCell(2));
                StudentDbModel student = studentDatabase.findAll().stream()
                        .filter(s -> s.getPhoneNumber().equals(phone))
                        .findFirst().orElse(null);

                if (student == null) continue;

                for (int j = 5; j < header.getLastCellNum(); j++) {
                    String dateString = header.getCell(j).getStringCellValue();
                    LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    String value = getCellStringValue(row.getCell(j)).trim();

                    if (value.isEmpty()) continue;
                    if (attendanceDatabase.findByStudentIdAndDate(student.getId(), date).isEmpty()) {
                        saveList.add(new AttendanceDbModel(student.getId(), date, value));
                    }
                }
            }

            attendanceDatabase.saveAll(saveList);
            return true;

        } catch (IOException e) {
            log.error("Excel import error", e);
            return false;
        }
    }

    public byte[] exportToExcelByMonth(Long userId, int year, int month) throws IOException {
        List<StudentDbModel> students = studentDatabase.findAllStudentsByOwnerUserId(userId);

        Map<Long, String> studentNames = new HashMap<>();
        for (StudentDbModel student : students) {
            userDatabase.findById(student.getUserId()).ifPresent(user -> studentNames.put(student.getId(), user.getFirstName() + " " + user.getLastName()));
        }

        List<AttendanceDbModel> attendanceList = attendanceDatabase.findAll();

        Set<LocalDate> targetDates = attendanceList.stream()
                .map(AttendanceDbModel::getDate)
                .filter(date -> date.getYear() == year && date.getMonthValue() == month)
                .collect(Collectors.toCollection(TreeSet::new));

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Attendance");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue(NUMBER);
            header.createCell(1).setCellValue(FULL_NAME);
            header.createCell(2).setCellValue(PHONE);
            header.createCell(3).setCellValue(COURSE);
            header.createCell(4).setCellValue(GROUP);

            int cellIndex = 5;
            for (LocalDate date : targetDates) {
                header.createCell(cellIndex++).setCellValue(date.toString());
            }

            int rowNum = 1;
            for (StudentDbModel student : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rowNum - 1);
                row.createCell(1).setCellValue(studentNames.getOrDefault(student.getId(), ""));
                row.createCell(2).setCellValue(student.getPhoneNumber());

                GroupDbModel group = groupDatabase.findById(student.getGroupId()).orElse(null);
                row.createCell(3).setCellValue(group != null ? group.getCourseId() + "" : "");
                row.createCell(4).setCellValue(group != null ? group.getTitle() : "");

                Map<LocalDate, String> attMap = attendanceList.stream()
                        .filter(a -> a.getStudentId().equals(student.getId()))
                        .collect(Collectors.toMap(AttendanceDbModel::getDate, AttendanceDbModel::getStatus, (a, b) -> a));

                int colIndex = 5;
                for (LocalDate date : targetDates) {
                    String status = attMap.get(date);
                    row.createCell(colIndex++).setCellValue(status != null ? status : "");
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public List<Attendance> getAllByStudentId(Long studentId) {
        return attendanceDatabase.findAllByStudentId(studentId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public Attendance update(Attendance updated) {
        AttendanceDbModel model = attendanceDatabase.findById(updated.getId()).orElse(null);
        if (model == null) return null;

        model.setDate(updated.getDate());
        model.setStudentId(updated.getStudentId());
        model.setStatus(updated.getStatus());

        AttendanceDbModel saved = attendanceDatabase.save(model);
        return mapToDto(saved);
    }

    public boolean delete(Long id) {
        if (!attendanceDatabase.existsById(id)) return false;
        attendanceDatabase.deleteById(id);
        return true;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private Attendance mapToDto(AttendanceDbModel dbModel) {
        if (dbModel == null) return null;
        return new Attendance(dbModel.getId(), dbModel.getStudentId(), dbModel.getDate(), dbModel.getStatus());
    }

    public Attendance findById(Long id) {
        AttendanceDbModel attendanceDbModel = attendanceDatabase.findById(id).orElse(null);
        return mapToDto(attendanceDbModel);
    }
}