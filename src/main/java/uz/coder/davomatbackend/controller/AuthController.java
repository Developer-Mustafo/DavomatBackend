package uz.coder.davomatbackend.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import uz.coder.davomatbackend.jwt.JwtService;
import uz.coder.davomatbackend.model.LoginRequest;
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
            JwtService jwtService,
            UserService service,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.service = service;
        this.passwordEncoder = passwordEncoder;
    }

    // 🔐 LOGIN
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginResponse(token, 200, null));
    }

    // 📝 REGISTER
    @PostMapping("/register")
    public ResponseEntity<Response<User>> register(@RequestBody User user) {

        try {
            User userByNumberOfPhone = service.findByPhoneNumber(user.getPhoneNumber());

            if (userByNumberOfPhone != null) {

                if (userByNumberOfPhone.getEmail() == null) {

                    userByNumberOfPhone.setLastName(user.getLastName());
                    userByNumberOfPhone.setFirstName(user.getFirstName());
                    userByNumberOfPhone.setEmail(user.getEmail());
                    userByNumberOfPhone.setRole(ROLE_STUDENT);

                    if (user.getPassword() != null) {
                        userByNumberOfPhone.setPassword(
                                passwordEncoder.encode(user.getPassword())
                        );
                    }

                    userByNumberOfPhone.setPayedDate(
                            LocalDate.now().plusWeeks(1)
                    );

                    User edit = service.edit(userByNumberOfPhone);

                    return ResponseEntity.ok(
                            new Response<>(200, edit)
                    );

                } else {
                    return ResponseEntity.ok(
                            new Response<>(500, THIS_PHONE_NUMBER_TAKEN)
                    );
                }

            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                User save = service.save(user);

                return ResponseEntity.ok(
                        new Response<>(200, save)
                );
            }

        } catch (Exception e) {
            return ResponseEntity.ok(
                    new Response<>(500, e.getMessage())
            );
        }
    }
}