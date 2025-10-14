package uz.coder.davomatbackend.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Attendance {
    private Long id;
    private Long studentId;
    private LocalDate date;
    private String status;

    public Attendance(Long studentId, LocalDate date, String status) {
        this.studentId = studentId;
        this.date = date;
        this.status = status;
    }
}