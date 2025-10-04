package uz.coder.davomatbackend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.coder.davomatbackend.service.EmailService;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final EmailService emailService;

    // application.properties dan oladi
    @Value("${spring.mail.username}")
    private String toEmail;

    public ContactController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/contact")
    public ResponseEntity<String> sendMail(@RequestBody Map<String, String> data) {
        try {
            String emailText = "Ism: " + data.get("name") +
                    "\nEmail: " + data.get("email") +
                    "\nXabar: " + data.get("message");

            emailService.sendEmail(
                    toEmail,  // propertiesdan olingan
                    "Davomat App Contact Form",
                    emailText
            );

            return ResponseEntity.ok("Xabaringiz yuborildi!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Serverda xatolik yuz berdi");
        }
    }
}
