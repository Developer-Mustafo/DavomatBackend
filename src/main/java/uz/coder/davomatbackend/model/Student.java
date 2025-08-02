package uz.coder.davomatbackend.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Student {
    private long id;
    private String fullName;
    private String phoneNumber;
    private long userId;
    private long groupId;

    public Student(String fullName, String phoneNumber, long userId, long groupId) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
        this.groupId = groupId;
    }
}
