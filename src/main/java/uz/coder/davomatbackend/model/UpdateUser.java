package uz.coder.davomatbackend.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UpdateUser {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
}
