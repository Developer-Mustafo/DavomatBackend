package uz.coder.davomatbackend.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.coder.davomatbackend.db.CourseDatabase;
import uz.coder.davomatbackend.db.GroupDatabase;
import uz.coder.davomatbackend.db.StudentDatabase;
import uz.coder.davomatbackend.db.UserDatabase;
import uz.coder.davomatbackend.db.model.CourseDbModel;
import uz.coder.davomatbackend.db.model.GroupDbModel;
import uz.coder.davomatbackend.db.model.StudentDbModel;
import uz.coder.davomatbackend.db.model.UserDbModel;
import uz.coder.davomatbackend.model.Student;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StudentService {
    private final StudentDatabase database;
    private final UserDatabase userDatabase;
    private final GroupDatabase groupDatabase;
    private final CourseDatabase courseDatabase;

    @Autowired
    public StudentService(StudentDatabase database, UserDatabase userDatabase, GroupDatabase groupDatabase, CourseDatabase courseDatabase) {
        this.database = database;
        this.userDatabase = userDatabase;
        this.groupDatabase = groupDatabase;
        this.courseDatabase = courseDatabase;
    }

    public Student save(Student student) {
        StudentDbModel save = database.save(new StudentDbModel(student.getPhoneNumber(), student.getUserId(), student.getGroupId()));
        String firstNameById = userDatabase.findFirstNameById(student.getUserId());
        String lastNameById = userDatabase.findLastNameById(student.getUserId());
        String fullName = firstNameById + " " + lastNameById;
        return new Student(save.getId(), fullName, save.getPhoneNumber(), save.getUserId(), save.getGroupId());
    }

    public Student edit(Student student) {
        database.update(student.getId(), student.getPhoneNumber(), student.getUserId(), student.getGroupId());
        StudentDbModel save = database.findById(student.getId()).orElse(null);
        assert save != null;
        String firstNameById = userDatabase.findFirstNameById(student.getUserId());
        String lastNameById = userDatabase.findLastNameById(student.getUserId());
        String fullName = firstNameById + " " + lastNameById;
        return new Student(save.getId(), fullName, save.getPhoneNumber(), save.getUserId(), save.getGroupId());
    }

    public Student findById(long id) {
        StudentDbModel student = database.findById(id).orElse(null);
        assert student != null;
        String firstNameById = userDatabase.findFirstNameById(student.getUserId());
        String lastNameById = userDatabase.findLastNameById(student.getUserId());
        String fullName = firstNameById + " " + lastNameById;
        return new Student(student.getId(), fullName, student.getPhoneNumber(), student.getUserId(), student.getGroupId());
    }

    public int deleteById(long id) {
        StudentDbModel student = database.findById(id).orElse(null);
        assert student != null;
        database.delete(student);
        return 1;
    }

    public List<Student> findAllStudentByGroupId(long groupId) {
        List<StudentDbModel> allByUserId = database.findAllByGroupId(groupId);
        return allByUserId.stream().map(item -> {
            String firstNameById = userDatabase.findFirstNameById(item.getUserId());
            String lastNameById = userDatabase.findLastNameById(item.getUserId());
            String fullName = firstNameById + " " + lastNameById;
            return new Student(item.getId(), fullName, item.getPhoneNumber(), item.getUserId(), item.getGroupId());
        }).collect(Collectors.toList());
    }

    // 🔄 TO‘G‘RILANGAN 1: saveAllByEXEL
    public boolean saveAllByEXEL(MultipartFile file, long userId) {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<Student> studentList = new ArrayList<>();

            List<GroupDbModel> allGroups = groupDatabase.findAll();
            List<CourseDbModel> allCourses = courseDatabase.findAll();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String fullName = getCellStringValue(row.getCell(1));
                String phoneNumber = getCellStringValue(row.getCell(2));
                String groupName = getCellStringValue(row.getCell(3));
                String courseName = getCellStringValue(row.getCell(4));

                // 1. USERNI TELEFON RAQAMI ORQALI TEKSHIRISH
                UserDbModel user = userDatabase.findByPhoneNumber(phoneNumber);
                if (user == null) {
                    String[] nameParts = fullName.trim().split(" ", 2);
                    String firstName = nameParts.length > 0 ? nameParts[0] : "";
                    String lastName = nameParts.length > 1 ? nameParts[1] : "";
                    user = new UserDbModel(firstName, lastName, phoneNumber, "STUDENT");
                    user = userDatabase.save(user);
                }

                // 2. COURSE BOR YOKI YO'QLIGINI TEKSHIRISH
                CourseDbModel matchedCourse = allCourses.stream()
                        .filter(c -> isSimilar(courseName, c.getTitle()))
                        .findFirst()
                        .orElseGet(() -> {
                            CourseDbModel newCourse = new CourseDbModel(courseName, "", userId);
                            return courseDatabase.save(newCourse);
                        });

                // 3. GROUP BOR YOKI YO'QLIGINI TEKSHIRISH
                GroupDbModel matchedGroup = allGroups.stream()
                        .filter(g -> isSimilar(groupName, g.getTitle()) && g.getCourseId() == matchedCourse.getId())
                        .findFirst()
                        .orElseGet(() -> {
                            GroupDbModel newGroup = new GroupDbModel(groupName, matchedCourse.getId());
                            return groupDatabase.save(newGroup);
                        });

                // 4. STUDENTNI TAYYORLASH
                Student student = new Student(fullName, phoneNumber, user.getId(), matchedGroup.getId());
                studentList.add(student);
            }

            // 5. BARCHASINI SAQLASH
            studentList.forEach(this::accept);

            return true;

        } catch (Exception e) {
            log.error("Faylni o'qishda xatolik yuz berdi: ", e);
            return false;
        }
    }

    // 🔄 TO‘G‘RILANGAN 2: exportStudentsToXlsx
    public byte[] exportStudentsToXlsx(List<Student> students) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("№");
            header.createCell(1).setCellValue("O‘quvchining F. I. Sh");
            header.createCell(2).setCellValue("Telefon raqami");
            header.createCell(3).setCellValue("Guruhi");
            header.createCell(4).setCellValue("Kasbi");

            int rowNum = 1;
            for (int i = 0; i < students.size(); i++) {
                Student s = students.get(i);
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(s.getFullName());
                row.createCell(2).setCellValue(s.getPhoneNumber());

                GroupDbModel group = groupDatabase.findById(s.getGroupId()).orElse(null);
                String groupName = group != null ? group.getTitle() : "Noma'lum";

                CourseDbModel course = (group != null) ? courseDatabase.findById(group.getCourseId()).orElse(null) : null;
                String courseName = (course != null) ? course.getTitle() : "Noma'lum";

                row.createCell(3).setCellValue(groupName);
                row.createCell(4).setCellValue(courseName);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    // Quyidagilar o‘zgartirilmagan, yordamchi metodlar
    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> "";
        };
    }

    private boolean isSimilar(String input, String target) {
        if (input == null || target == null) return false;
        String iNorm = normalize(input);
        String tNorm = normalize(target);
        return iNorm.contains(tNorm) || tNorm.contains(iNorm);
    }

    private String normalize(String text) {
        return text.toLowerCase()
                .replaceAll("[\\s\\-_/|]", "")
                .replace("’", "")
                .replace("'", "")
                .trim();
    }

    private void accept(Student student) {
        try {
            database.save(new StudentDbModel(student.getPhoneNumber(), student.getUserId(), student.getGroupId()));
        } catch (Exception e) {
            log.error("e: ", e);
        }
    }

    public List<Student> getStudentsByUserId(Long userId) {
        List<StudentDbModel> students = database.findAllStudentsByOwnerUserId(userId);
        return students.stream().map(item -> {
            String firstNameById = userDatabase.findFirstNameById(item.getUserId());
            String lastNameById = userDatabase.findLastNameById(item.getUserId());
            String fullName = firstNameById + " " + lastNameById;
            return new Student(item.getId(), fullName, item.getPhoneNumber(), item.getUserId(), item.getGroupId());
        }).collect(Collectors.toList());
    }
}