package uz.coder.davomatbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.coder.davomatbackend.model.Response;
import uz.coder.davomatbackend.model.User;
import uz.coder.davomatbackend.service.UserService;

import static uz.coder.davomatbackend.todo.Strings.ROLE_STUDENT;

@RequestMapping("/api/user")
@RestController
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }
    @PostMapping("/login")
    public ResponseEntity<Response<User>> login(@RequestParam("email") String email, @RequestParam("password") String password) {
        try {
            User user = service.login(email, password);
            return  ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, user));
        }catch (Exception e){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response<Integer>> deleteById(@PathVariable("id") long id) {
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
    public ResponseEntity<Response<User>> findById(@PathVariable("id") long id) {
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
    public ResponseEntity<Response<User>> findByPhone(@PathVariable("phoneNumber") String phoneNumber) {
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

    @PostMapping("/register")
    public ResponseEntity<Response<User>> register(@RequestBody User user) {
        try {
            User userByNumberOfPhone = service.findByPhoneNumber(user.getPhoneNumber());
            if (userByNumberOfPhone != null) {
                userByNumberOfPhone.setLastName(user.getLastName());
                userByNumberOfPhone.setFirstName(user.getFirstName());
                userByNumberOfPhone.setEmail(user.getEmail());
                userByNumberOfPhone.setRole(ROLE_STUDENT);
                userByNumberOfPhone.setPassword(user.getPassword());
                User edit = service.edit(userByNumberOfPhone);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new Response<>(200, edit));
            }else {
                User save = service.save(user);
                return  ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new Response<>(200, save));
            }
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
}