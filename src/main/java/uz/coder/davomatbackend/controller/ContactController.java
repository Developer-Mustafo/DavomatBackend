package uz.coder.davomatbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ContactController {

    private final JavaMailSender mailSender;

    public ContactController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostMapping("/contact")
    public ResponseEntity<String> sendMail(@RequestBody Map<String, String> data) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("mustaforahimov30@gmail.com");
            message.setSubject("Davomat App Contact Form");
            message.setText(
                "Ism: " + data.get("name") +
                "\nEmail: " + data.get("email") +
                "\nXabar: " + data.get("message")
            );
            mailSender.send(message);
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error");
        }
    }
}
