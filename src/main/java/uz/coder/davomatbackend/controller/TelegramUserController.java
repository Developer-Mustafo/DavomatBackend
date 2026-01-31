package uz.coder.davomatbackend.controller;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import uz.coder.davomatbackend.model.Balance;
import uz.coder.davomatbackend.model.Response;
import uz.coder.davomatbackend.model.TelegramUser;
import uz.coder.davomatbackend.model.User;
import uz.coder.davomatbackend.service.StudentService;
import uz.coder.davomatbackend.service.TelegramUserService;
import uz.coder.davomatbackend.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/telegram")
public class TelegramUserController {

    private final TelegramUserService service;
    private final UserService userService;
    private final StudentService studentService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TelegramUserController(TelegramUserService service, UserService userService, StudentService studentService, PasswordEncoder passwordEncoder) {
        this.service = service;
        this.userService = userService;
        this.studentService = studentService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Response<TelegramUser>> register(@RequestBody TelegramUser telegramUser) {
        try {
            TelegramUser result = service.save(telegramUser);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, result));
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }

    @NonNull
    static ResponseEntity<Response<User>> getResponseResponseEntity(@PathVariable String phoneNumber, UserService userService) {
        try {
            User result = userService.findByPhoneNumber(phoneNumber);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, result));
        }catch (Exception e){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }


    @GetMapping("/get_all_users")
    public ResponseEntity<Response<List<TelegramUser>>> getAllUsers() {
        try {
            List<TelegramUser> result = service.findAll();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, result));
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }

    // ===================== TOKEN asosida to'lov endpointi =====================
    @PutMapping("/pay")
    public ResponseEntity<Response<Boolean>> payMe(@RequestBody Balance balance) {
        try {
            boolean success = userService.updateBalanceUser(balance);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, success));
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }
    @GetMapping("/balance")
    public ResponseEntity<Response<Balance>> getUserBalance(@RequestParam long telegramUserId) {
        try {
            Balance balance = studentService.getUserBalanceByTelegramUserId(telegramUserId);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, balance));
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }
    @PutMapping("/update/user")
    public ResponseEntity<Response<User>> updateUserViaPhoneNumber(@RequestParam String phoneNumber, @RequestBody String password) {
        try {
            User user = userService.findByPhoneNumber(phoneNumber);
            user.setPassword(passwordEncoder.encode(password));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, userService.edit(user)));
        }catch (Exception e){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }
}