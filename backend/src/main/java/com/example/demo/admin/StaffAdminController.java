package com.example.demo.admin;

import com.example.demo.auth.AuthTokenRepository;import com.example.demo.request.*;import com.example.demo.user.*;
import jakarta.transaction.Transactional;import jakarta.validation.Valid;import jakarta.validation.constraints.*;
import org.springframework.http.*;import org.springframework.security.core.annotation.AuthenticationPrincipal;import org.springframework.security.crypto.password.PasswordEncoder;import org.springframework.web.bind.annotation.*;import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController @RequestMapping("/api/admin/staff")
public class StaffAdminController {
 private final UserRepository users;private final PasswordEncoder encoder;private final AuthTokenRepository tokens;private final MaintenanceRequestRepository requests;private final RequestWorkflowService workflow;
 public StaffAdminController(UserRepository u,PasswordEncoder e,AuthTokenRepository t,MaintenanceRequestRepository r,RequestWorkflowService w){users=u;encoder=e;tokens=t;requests=r;workflow=w;}
 @GetMapping public List<StaffDto> list(@AuthenticationPrincipal User admin){check(admin);return users.findByRoleOrderByFullName(User.Role.STAFF).stream().map(StaffDto::from).toList();}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public StaffDto create(@AuthenticationPrincipal User admin,@Valid @RequestBody Create body){check(admin);String email=body.email().trim().toLowerCase(Locale.ROOT);if(users.existsByEmailIgnoreCase(email))throw new ResponseStatusException(HttpStatus.CONFLICT,"An account already exists for this email.");User staff=new User(body.fullName().trim(),email,encoder.encode(body.temporaryPassword()),User.Role.STAFF,true);staff.setMustChangePassword(true);return StaffDto.from(users.save(staff));}
 @PostMapping("/{id}/deactivate") @Transactional public StaffDto deactivate(@AuthenticationPrincipal User admin,@PathVariable Long id,@RequestBody(required=false) Deactivate body){check(admin);User staff=users.findById(id).filter(x->x.getRole()==User.Role.STAFF).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));List<MaintenanceRequest> open=requests.findByAssigneeIdAndStatusNot(id,MaintenanceRequest.Status.COMPLETED);User replacement=null;if(body!=null&&body.replacementStaffId()!=null)replacement=users.findById(body.replacementStaffId()).filter(x->x.getRole()==User.Role.STAFF&&x.isActive()&&!x.getId().equals(id)).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Replacement staff is invalid."));for(MaintenanceRequest r:open)workflow.assign(r.getId(),admin,replacement,null);staff.setActive(false);tokens.deleteByUserId(id);return StaffDto.from(staff);}
 private void check(User u){if(u.getRole()!=User.Role.ADMIN)throw new ResponseStatusException(HttpStatus.FORBIDDEN);}
 public record Create(@NotBlank @Size(max=100) String fullName,@NotBlank @Email String email,@Size(min=8,max=72) String temporaryPassword){}public record Deactivate(Long replacementStaffId){}
 public record StaffDto(Long id,String fullName,String email,boolean active,boolean mustChangePassword){static StaffDto from(User u){return new StaffDto(u.getId(),u.getFullName(),u.getEmail(),u.isActive(),u.isMustChangePassword());}}
}
