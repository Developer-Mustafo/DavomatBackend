package uz.coder.davomatbackend.model;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AddAttendance {
    private long studentId;
    private LocalDate date;
    private String status;
}
