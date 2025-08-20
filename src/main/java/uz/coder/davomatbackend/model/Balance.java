package uz.coder.davomatbackend.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Balance {
    private LocalDate date;
    private long id;
}
