package uz.coder.davomatbackend.db.model;

import jakarta.persistence.*;
import lombok.*;

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

    public StudentDbModel(String phoneNumber, long userId,  long groupId) {
        this.phoneNumber = phoneNumber;
        this.userId = userId;
        this.groupId = groupId;
    }
}
