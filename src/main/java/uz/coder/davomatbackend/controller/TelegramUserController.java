package uz.coder.davomatbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.coder.davomatbackend.model.Response;
import uz.coder.davomatbackend.model.TelegramUser;
import uz.coder.davomatbackend.service.TelegramUserService;

import java.util.List;

@RestController
@RequestMapping("/api/telegram")
public class TelegramUserController {
    private final TelegramUserService service;

    @Autowired
    public TelegramUserController(TelegramUserService service) {
        this.service = service;
    }
    @PostMapping("/register")
    public ResponseEntity<Response<TelegramUser>> register(@RequestBody TelegramUser telegramUser) {
        try {
            TelegramUser result = service.save(telegramUser);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, result));
        }
        catch (Exception e) {
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
        }
        catch (Exception e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }
}
