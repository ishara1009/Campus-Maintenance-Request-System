package com.example.demo.notification;
import com.example.demo.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;import java.util.*;
@RestController @RequestMapping("/api/notifications")
public class NotificationController{
 private final NotificationRepository repo; public NotificationController(NotificationRepository r){repo=r;}
 @GetMapping public Map<String,Object> list(@AuthenticationPrincipal User u){return Map.of("unread",repo.countByUserIdAndReadFalse(u.getId()),"items",repo.findByUserIdOrderByCreatedAtDesc(u.getId()).stream().map(Item::from).toList());}
 @PatchMapping("/{id}/read") public Item read(@AuthenticationPrincipal User u,@PathVariable Long id){Notification n=repo.findByIdAndUserId(id,u.getId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));n.markRead();return Item.from(repo.save(n));}
 public record Item(Long id,String message,Long requestId,boolean read,Instant createdAt){static Item from(Notification n){return new Item(n.getId(),n.getMessage(),n.getRequestId(),n.isRead(),n.getCreatedAt());}}
}
