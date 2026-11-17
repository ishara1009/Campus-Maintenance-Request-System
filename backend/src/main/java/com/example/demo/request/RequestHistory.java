package com.example.demo.request;
import com.example.demo.user.User;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="request_history")
public class RequestHistory {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="request_id") private MaintenanceRequest request;
 @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="actor_id") private User actor;
 @Column(nullable=false,length=60) private String action;
 @Column(nullable=false,length=1000) private String details;
 @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now();
 protected RequestHistory(){} public RequestHistory(MaintenanceRequest r,User a,String action,String details){request=r;actor=a;this.action=action;this.details=details;}
 public Long getId(){return id;} public User getActor(){return actor;} public String getAction(){return action;} public String getDetails(){return details;} public Instant getCreatedAt(){return createdAt;}
}
