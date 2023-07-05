package sobad.code.movies_diary.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sobad.code.movies_diary.dto.RegistrationUserDto;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class UserController {
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }
    @GetMapping("/secured")
    public String secured() {
        return "user";
    }
    @GetMapping("/unsecured")
    public String unsecured() {
        return "user";
    }
    @GetMapping("/info")
    public String info(Principal principal) {
        return principal.getName();
    }
}
