package com.example.demo.auth;
import com.example.demo.user.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="action_tokens")
public class ActionToken {
  public enum Purpose { VERIFY_EMAIL, RESET_PASSWORD }
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id") private User user;
  @Column(nullable=false,unique=true,length=64) private String tokenHash;
  @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private Purpose purpose;
  @Column(nullable=false) private Instant expiresAt;
  private Instant usedAt;
  @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now();
  protected ActionToken(){}
  public ActionToken(User user,String hash,Purpose purpose,Instant expires){this.user=user;this.tokenHash=hash;this.purpose=purpose;this.expiresAt=expires;}
  public User getUser(){return user;} public boolean usable(){return usedAt==null&&expiresAt.isAfter(Instant.now());}
  public void use(){usedAt=Instant.now();}
}
