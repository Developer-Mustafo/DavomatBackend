package uz.coder.davomatbackend.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AddStudent {
    private String fullName;
    private String phoneNumber;
    private long groupId;
}
