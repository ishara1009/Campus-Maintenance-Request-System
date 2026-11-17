package com.example.demo.request;

import com.example.demo.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;

@Entity
@Table(name = "maintenance_requests")
public class MaintenanceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 150)
    private String location;

    @Column(nullable = false, length = 1200)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.PENDING;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @Column(length = 2000) private String repairNote;
    private Instant completedAt;
    @Version private long version;
    @Column(length=64) private String attachmentId;
    private String attachmentName;
    @Column(length=50) private String attachmentType;
    private Long attachmentSize;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected MaintenanceRequest() {
    }

    public MaintenanceRequest(User user, String title, String category, String location,
                              String description, Priority priority) {
        this.user = user;
        this.title = title;
        this.category = category;
        this.location = location;
        this.description = description;
        this.priority = priority;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public Priority getPriority() { return priority; }
    public Status getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public User getUser() { return user; }
    public User getAssignee() { return assignee; }
    public String getRepairNote() { return repairNote; }
    public Instant getCompletedAt() { return completedAt; }
    public long getVersion() { return version; }
    public String getAttachmentId() { return attachmentId; }
    public String getAttachmentName() { return attachmentName; }
    public String getAttachmentType() { return attachmentType; }
    public Long getAttachmentSize() { return attachmentSize; }
    public void update(String title,String category,String location,String description,Priority priority){this.title=title;this.category=category;this.location=location;this.description=description;this.priority=priority;}
    public void setPriority(Priority value){priority=value;} public void setAssignee(User value){assignee=value;}
    public void start(){status=Status.IN_PROGRESS;} public void complete(String note){status=Status.COMPLETED;repairNote=note;completedAt=Instant.now();}
    public void attach(String id,String name,String type,long size){attachmentId=id;attachmentName=name;attachmentType=type;attachmentSize=size;}
    public void removeAttachment(){attachmentId=null;attachmentName=null;attachmentType=null;attachmentSize=null;}

    public enum Priority { LOW, MEDIUM, HIGH }
    public enum Status { PENDING, IN_PROGRESS, COMPLETED }
}
