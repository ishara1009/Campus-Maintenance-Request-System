package com.example.demo.auth;

import com.example.demo.user.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.server.ResponseStatusException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class AuthActionService {
  private final ActionTokenRepository tokens; private final TokenService hashing; private final JavaMailSender mail;
  private final SecureRandom random=new SecureRandom();
  @Value("${app.frontend-origin:http://localhost:5173}") private String frontend;
  @Value("${app.mail.enabled:false}") private boolean mailEnabled;
  public AuthActionService(ActionTokenRepository tokens,TokenService hashing,ObjectProvider<JavaMailSender> mail){this.tokens=tokens;this.hashing=hashing;this.mail=mail.getIfAvailable();}
  public void sendVerification(User user){send(user,ActionToken.Purpose.VERIFY_EMAIL,"Verify your CampusCare email","/verify-email?token=",24);}
  public void sendReset(User user){send(user,ActionToken.Purpose.RESET_PASSWORD,"Reset your CampusCare password","/reset-password?token=",1);}
  private void send(User user,ActionToken.Purpose purpose,String subject,String path,long hours){
    byte[] bytes=new byte[32];random.nextBytes(bytes);String raw=Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    tokens.save(new ActionToken(user,hashing.hash(raw),purpose,Instant.now().plus(hours,ChronoUnit.HOURS)));
    if(mailEnabled){if(mail==null)throw new IllegalStateException("MAIL_ENABLED requires SMTP configuration.");SimpleMailMessage message=new SimpleMailMessage();message.setTo(user.getEmail());message.setSubject(subject);message.setText("Open this single-use link:\n"+frontend+path+raw);mail.send(message);}
  }
  @Transactional public User consume(String raw,ActionToken.Purpose purpose){
    ActionToken token=tokens.findByTokenHashAndPurpose(hashing.hash(raw),purpose).orElseThrow(()->bad("This link is invalid or has expired."));
    if(!token.usable()) throw bad("This link is invalid or has expired."); token.use(); return token.getUser();
  }
  private ResponseStatusException bad(String message){return new ResponseStatusException(BAD_REQUEST,message);}
}
