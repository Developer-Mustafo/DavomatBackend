package uz.coder.davomatbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import uz.coder.davomatbackend.model.Balance;
import uz.coder.davomatbackend.model.Response;
import uz.coder.davomatbackend.model.TelegramUser;
import uz.coder.davomatbackend.model.User;
import uz.coder.davomatbackend.service.TelegramUserService;
import uz.coder.davomatbackend.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/telegram")
public class TelegramUserController {

    private final TelegramUserService service;
    private final UserService userService;

    @Autowired
    public TelegramUserController(TelegramUserService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    // Token orqali telegramUser olish
    private TelegramUser getCurrentTelegramUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // token ichidagi username/email
        User user = userService.findByEmail(username);
        return service.findByUserId(user.getId());
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
            TelegramUser currentUser = getCurrentTelegramUser();
            balance.setTelegramUserId(currentUser.getId());
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
}