package uz.coder.davomatbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import uz.coder.davomatbackend.model.Response;
import uz.coder.davomatbackend.model.User;
import uz.coder.davomatbackend.service.UserService;

@RequestMapping("/api/user")
@RestController
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    // Token orqali userId olish
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // username/email token ichida
        return service.findByEmail(username); // yoki username bilan topish
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response<Integer>> deleteMe() {
        try {
            User currentUser = getCurrentUser();
            int data = service.deleteById(currentUser.getId());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, data));
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Response<User>> getMe() {
        try {
            User currentUser = getCurrentUser();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, currentUser));
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Response<User>> updateMe(@RequestBody User userUpdate) {
        try {
            User currentUser = getCurrentUser();
            userUpdate.setId(currentUser.getId()); // tokendagi userId bilan override
            User updatedUser = service.edit(userUpdate);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, updatedUser));
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }
    @GetMapping("/find-by-phone-number/{phoneNumber}")
    public ResponseEntity<Response<User>> findByPhoneNumber(@PathVariable String phoneNumber){
        try {
            User result = service.findByPhoneNumber(phoneNumber);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, result));
        }catch (Exception e){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }
}
