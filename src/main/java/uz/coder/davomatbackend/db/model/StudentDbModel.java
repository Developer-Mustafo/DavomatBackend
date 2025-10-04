package uz.coder.davomatbackend.db.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "student")
@Entity
public class StudentDbModel {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private long id;
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    @Column(name = "userId", nullable = false)
    private long userId;
    @Column(name = "groupId",  nullable = false)
    private long groupId;
    @Column(name = "createdDate")
    private LocalDate createdDate;

    public StudentDbModel(String phoneNumber, long userId,  long groupId,  LocalDate createdDate) {
        this.phoneNumber = phoneNumber;
        this.userId = userId;
        this.groupId = groupId;
        this.createdDate = createdDate;
    }
}
