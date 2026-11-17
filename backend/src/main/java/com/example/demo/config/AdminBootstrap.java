package com.example.demo.config;

import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Locale;

@Component
public class AdminBootstrap implements ApplicationRunner {
    private final UserRepository users; private final PasswordEncoder encoder;
    @Value("${app.bootstrap.admin-email:}") private String email;
    @Value("${app.bootstrap.admin-password:}") private String password;
    @Value("${app.bootstrap.admin-name:System Administrator}") private String name;
    public AdminBootstrap(UserRepository users,PasswordEncoder encoder){this.users=users;this.encoder=encoder;}
    public void run(ApplicationArguments args){
        if(email.isBlank()||password.isBlank()) return;
        String normalized=email.trim().toLowerCase(Locale.ROOT);
        if(users.existsByEmailIgnoreCase(normalized)) return;
        users.save(new User(name.trim(),normalized,encoder.encode(password),User.Role.ADMIN,true));
    }
}
