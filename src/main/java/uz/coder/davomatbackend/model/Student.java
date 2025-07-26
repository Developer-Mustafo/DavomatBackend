package uz.coder.davomatbackend.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Student {
    private long id;
    private String phoneNumber;
    private long userId;

    public Student(String phoneNumber, long userId) {
        this.phoneNumber = phoneNumber;
        this.userId = userId;
    }
}
