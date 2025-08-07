package uz.coder.davomatbackend.db.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "attendance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AttendanceDbModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String status; // "+" yoki "-" yoki "Bor" / "Yo'q"

    public AttendanceDbModel(Long studentId, LocalDate date, String status) {
        this.studentId = studentId;
        this.date = date;
        this.status = status;
    }
}