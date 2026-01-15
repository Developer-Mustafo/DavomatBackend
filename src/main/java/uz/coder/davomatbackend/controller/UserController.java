package uz.coder.davomatbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.coder.davomatbackend.model.Balance;
import uz.coder.davomatbackend.model.Response;
import uz.coder.davomatbackend.model.User;
import uz.coder.davomatbackend.service.UserService;
import static uz.coder.davomatbackend.todo.Strings.THIS_PHONE_NUMBER_TAKEN;
import java.time.LocalDate;
import static uz.coder.davomatbackend.todo.Strings.ROLE_STUDENT;

@RequestMapping("/api/user")
@RestController
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response<Integer>> deleteById(@PathVariable long id) {
        try {
            int data = service.deleteById(id);
            return  ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, data));
        }catch (Exception e){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Response<User>> findById(@PathVariable long id) {
        try {
            User user = service.findById(id);
            return  ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, user));
        }catch (Exception e){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<Response<User>> findByPhone(@PathVariable String phoneNumber) {
        try {
            User user = service.findByPhoneNumber(phoneNumber);
            return  ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, user));
        }catch (Exception e){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }
    @PutMapping("/update")
    public ResponseEntity<Response<User>> update(@RequestBody User user) {
        try {
            User edit = service.edit(user);
            return  ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, edit));
        }catch (Exception e){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }

    @PutMapping("/pay")
    public ResponseEntity<Response<Boolean>> pay(@RequestBody Balance balance) {
        try {
            boolean user = service.updateBalanceUser(balance);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, user));
        }catch (Exception e){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }
}