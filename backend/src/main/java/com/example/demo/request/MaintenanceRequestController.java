package com.example.demo.request;

import com.example.demo.user.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class MaintenanceRequestController {
    private final MaintenanceRequestRepository requestRepository;

    public MaintenanceRequestController(MaintenanceRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @GetMapping
    public List<RequestResponse> list(@AuthenticationPrincipal User user) {
        return requestRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(RequestResponse::from).toList();
    }

    @GetMapping("/summary")
    public SummaryResponse summary(@AuthenticationPrincipal User user) {
        long total = requestRepository.countByUserId(user.getId());
        long pending = requestRepository.countByUserIdAndStatus(user.getId(), MaintenanceRequest.Status.PENDING);
        long inProgress = requestRepository.countByUserIdAndStatus(user.getId(), MaintenanceRequest.Status.IN_PROGRESS);
        long completed = requestRepository.countByUserIdAndStatus(user.getId(), MaintenanceRequest.Status.COMPLETED);
        return new SummaryResponse(total, pending, inProgress, completed);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestResponse create(@AuthenticationPrincipal User user,
                                  @Valid @RequestBody CreateRequest request) {
        var saved = requestRepository.save(new MaintenanceRequest(user, request.title().trim(),
                request.category().trim(), request.location().trim(), request.description().trim(),
                request.priority()));
        return RequestResponse.from(saved);
    }

    public record CreateRequest(
            @NotBlank @Size(max = 120) String title,
            @NotBlank @Size(max = 50) String category,
            @NotBlank @Size(max = 150) String location,
            @NotBlank @Size(max = 1200) String description,
            @NotNull MaintenanceRequest.Priority priority) {}

    public record RequestResponse(Long id, String title, String category, String location,
                                  String description, MaintenanceRequest.Priority priority,
                                  MaintenanceRequest.Status status, Instant createdAt) {
        static RequestResponse from(MaintenanceRequest request) {
            return new RequestResponse(request.getId(), request.getTitle(), request.getCategory(),
                    request.getLocation(), request.getDescription(), request.getPriority(),
                    request.getStatus(), request.getCreatedAt());
        }
    }

    public record SummaryResponse(long total, long pending, long inProgress, long completed) {}
}
