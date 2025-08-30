package uz.coder.davomatbackend.db.model;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "telegram_users")
public class TelegramUserDbModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;
    @Column(name = "userId")
    private long userId;
    @Column(name = "telegramUserId",  nullable = false, unique = true)
    private long telegramUserId;
    @Column(name = "firstName", nullable = false)
    private String firstName;
    @Column(name = "lastName", nullable = false)
    private String lastName;
    @Column(name = "phoneNumber", nullable = false)
    private String phoneNumber;

    public TelegramUserDbModel(long userId, long telegramUserId, String firstName, String lastName, String phoneNumber) {
        this.userId = userId;
        this.telegramUserId = telegramUserId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public TelegramUserDbModel(long telegramUserId, String firstName, String lastName, String phoneNumber) {
        this.telegramUserId = telegramUserId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }
}