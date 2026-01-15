package uz.coder.davomatbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import uz.coder.davomatbackend.jwt.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import uz.coder.davomatbackend.model.LoginResponse;
import uz.coder.davomatbackend.model.Response;
import uz.coder.davomatbackend.model.User;
import uz.coder.davomatbackend.service.UserService;

import java.time.LocalDate;

import static uz.coder.davomatbackend.todo.Strings.ROLE_STUDENT;
import static uz.coder.davomatbackend.todo.Strings.THIS_PHONE_NUMBER_TAKEN;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService service;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService, UserService service,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.service = service;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestParam String email,
            @RequestParam String password
    ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        String token = jwtService.generateToken(email);

        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new LoginResponse(token, HttpStatus.OK.value(), null));
        }catch (Exception e){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new LoginResponse(null, HttpStatus.NO_CONTENT.value(), e.getMessage()));
        }
    }
    @PostMapping("/register")
    public ResponseEntity<Response<User>> register(@RequestBody User user) {
    try {
        User userByNumberOfPhone = service.findByPhoneNumber(user.getPhoneNumber());
        if (userByNumberOfPhone != null) {
            if(userByNumberOfPhone.getEmail() == null){
                userByNumberOfPhone.setLastName(user.getLastName());
                userByNumberOfPhone.setFirstName(user.getFirstName());
                userByNumberOfPhone.setEmail(user.getEmail());
                userByNumberOfPhone.setRole(ROLE_STUDENT);
                if(user.getPassword() != null){
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                }
                userByNumberOfPhone.setPayedDate(LocalDate.now().plusWeeks(1));
                User edit = service.edit(userByNumberOfPhone);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new Response<>(200, edit));
            }else {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new Response<>(500, THIS_PHONE_NUMBER_TAKEN));
            }
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


}
