package uz.coder.davomatbackend.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TelegramUser {
    private long id;
    private long userId;
    private long telegramUserId;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    public TelegramUser(long userId, long telegramUserId, String firstName, String lastName, String phoneNumber) {
        this.userId = userId;
        this.telegramUserId = telegramUserId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public TelegramUser(long telegramUserId, String firstName, String lastName, String phoneNumber) {
        this.telegramUserId = telegramUserId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }
}