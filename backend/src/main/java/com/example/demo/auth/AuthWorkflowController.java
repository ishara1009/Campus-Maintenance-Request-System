package com.example.demo.auth;

import com.example.demo.user.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController @RequestMapping("/api/auth")
public class AuthWorkflowController {
  private final UserRepository users; private final AuthTokenRepository logins; private final AuthActionService actions; private final PasswordEncoder encoder;
  public AuthWorkflowController(UserRepository users,AuthTokenRepository logins,AuthActionService actions,PasswordEncoder encoder){this.users=users;this.logins=logins;this.actions=actions;this.encoder=encoder;}
  @PostMapping("/verify-email") @Transactional public Map<String,String> verify(@Valid @RequestBody TokenBody body){User u=actions.consume(body.token(),ActionToken.Purpose.VERIFY_EMAIL);u.setEmailVerified(true);return Map.of("message","Email verified. You can now sign in.");}
  @PostMapping("/forgot-password") public Map<String,String> forgot(@Valid @RequestBody EmailBody body){users.findByEmailIgnoreCase(body.email().trim()).filter(User::isActive).ifPresent(actions::sendReset);return Map.of("message","If that address is registered, a reset link has been sent.");}
  @PostMapping("/reset-password") @Transactional public Map<String,String> reset(@Valid @RequestBody ResetBody body){User u=actions.consume(body.token(),ActionToken.Purpose.RESET_PASSWORD);u.setPasswordHash(encoder.encode(body.password()));u.setMustChangePassword(false);logins.deleteByUserId(u.getId());return Map.of("message","Password updated. Please sign in again.");}
  @PostMapping("/change-password") @Transactional public Map<String,String> change(@AuthenticationPrincipal User u,@Valid @RequestBody ChangeBody body){if(!encoder.matches(body.currentPassword(),u.getPasswordHash()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Current password is incorrect.");u.setPasswordHash(encoder.encode(body.newPassword()));u.setMustChangePassword(false);logins.deleteByUserId(u.getId());return Map.of("message","Password changed. Please sign in again.");}
  public record TokenBody(@NotBlank String token){} public record EmailBody(@Email @NotBlank String email){}
  public record ResetBody(@NotBlank String token,@Size(min=8,max=72) String password){}
  public record ChangeBody(@NotBlank String currentPassword,@Size(min=8,max=72) String newPassword){}
}
