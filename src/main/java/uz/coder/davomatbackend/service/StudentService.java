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
        return allByUserId.stream().map(item ->{
            String firstNameById = userDatabase.findFirstNameById(item.getUserId());
            String lastNameById = userDatabase.findLastNameById(item.getUserId());
            String fullName = firstNameById + " " + lastNameById;
            return new Student(item.getId(), fullName, item.getPhoneNumber(), item.getUserId(), item.getGroupId());
        }).collect(Collectors.toList());
    }

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

                Student student = new Student();
                student.setFullName(getCellStringValue(row.getCell(1)));
                student.setPhoneNumber(getCellStringValue(row.getCell(2)));

                String groupName = getCellStringValue(row.getCell(3));
                String courseName = getCellStringValue(row.getCell(4));

                CourseDbModel matchedCourse = allCourses.stream()
                        .filter(c -> isSimilar(courseName, c.getTitle()))
                        .findFirst()
                        .orElseGet(() -> {
                            CourseDbModel newCourse = new CourseDbModel(courseName, "", userId);
                            return courseDatabase.save(newCourse);
                        });

                GroupDbModel matchedGroup = allGroups.stream()
                        .filter(g -> isSimilar(groupName, g.getTitle()) && g.getCourseId()==matchedCourse.getId())
                        .findFirst()
                        .orElseGet(() -> {
                            GroupDbModel newGroup = new GroupDbModel(groupName, matchedCourse.getId());
                            return groupDatabase.save(newGroup);
                        });

                student.setGroupId(matchedGroup.getId());
                studentList.add(student);
            }

            // Saqlash (agar kerak bo‘lsa)
             studentList.forEach(this::accept);

            return true;

        } catch (Exception e) {
            System.out.println("Xatolik: " + e.getMessage());
            return false;
        }
    }


    // Yordamchi metod: katakcha qiymatini olish
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
                .replaceAll("[\\s\\-_/|]", "") // bo‘sh joy, -, _, /, | ni olib tashlaydi
                .replace("’", "") // apostrof olib tashlanadi
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
                row.createCell(0).setCellValue(i + 1); // Tartib raqam
                row.createCell(1).setCellValue(s.getFullName());
                row.createCell(2).setCellValue(s.getPhoneNumber());
                GroupDbModel groupDbModel = groupDatabase.findById(s.getGroupId()).orElse(null);
                assert groupDbModel != null;
                row.createCell(3).setCellValue(groupDbModel.getTitle());
                CourseDbModel courseDbModel = courseDatabase.findById(groupDbModel.getCourseId()).orElse(null);
                assert courseDbModel != null;
                row.createCell(4).setCellValue(courseDbModel.getTitle());
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public List<Student> getStudentsByUserId(Long userId) {
        return database.findUsersByUserId(userId).stream().map(
                item->{
                    long itemUserId = item.getUserId();
                    UserDbModel model = userDatabase.findById(itemUserId).orElse(null);
                    assert model != null;
                    String fullName = model.getFirstName()+" "+model.getLastName();
                    return new Student(item.getId(), fullName, item.getPhoneNumber(), item.getUserId(), item.getGroupId());
                }
        ).collect(Collectors.toList());
    }
}