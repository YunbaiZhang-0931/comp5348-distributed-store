package edu.usyd.comp5348.store_api.web;

import edu.usyd.comp5348.store_api.domain.Customer;
import edu.usyd.comp5348.store_api.service.AuthService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostConstruct
    public void init() {
        // 确保演示账号存在
        this.auth.ensureDemoUser();
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResp login(@RequestBody LoginReq req) {
        Customer c = auth.login(req.getUsername(), req.getPassword());
        return new LoginResp(c.getId(), c.getUsername(), c.getEmail());
    }

    @Data
    public static class LoginReq {
        private String username;
        private String password;
    }
    public record LoginResp(String userId, String username, String email){}
}
