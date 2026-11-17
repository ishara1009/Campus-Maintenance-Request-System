package com.example.demo.notification;
import com.example.demo.user.User;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="notifications")
public class Notification {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id") private User user;
 @Column(nullable=false,length=500) private String message; private Long requestId;
 @Column(name="is_read",nullable=false) private boolean read;
 @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now();
 protected Notification(){} public Notification(User u,String m,Long requestId){user=u;message=m;this.requestId=requestId;}
 public Long getId(){return id;} public String getMessage(){return message;} public Long getRequestId(){return requestId;} public boolean isRead(){return read;} public Instant getCreatedAt(){return createdAt;} public void markRead(){read=true;}
}
