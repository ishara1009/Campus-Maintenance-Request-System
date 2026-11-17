package com.example.demo;

import com.example.demo.auth.*;
import com.example.demo.request.*;
import com.example.demo.user.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc
class WorkflowSecurityIntegrationTests {
 @Autowired MockMvc mvc; @Autowired UserRepository users; @Autowired PasswordEncoder encoder; @Autowired TokenService tokens; @Autowired JsonMapper json; @Autowired MaintenanceRequestRepository requests; @Autowired ActionTokenRepository actionTokens;
 private User user(String prefix,User.Role role){return users.save(new User(prefix,prefix+System.nanoTime()+"@campus.edu",encoder.encode("Password123"),role,true));}
 private String bearer(User u){return "Bearer "+tokens.createToken(u);} private String create(User student)throws Exception{return json.readTree(mvc.perform(post("/api/requests").header("Authorization",bearer(student)).contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Leak\",\"category\":\"Plumbing\",\"location\":\"B1\",\"description\":\"Water leak\",\"priority\":\"MEDIUM\"}")).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).get("id").asText();}

 @Test void studentCannotReadOrEditAnotherStudentsRequest()throws Exception{User owner=user("Owner",User.Role.STUDENT),other=user("Other",User.Role.STUDENT);String id=create(owner);mvc.perform(get("/api/requests/"+id).header("Authorization",bearer(other))).andExpect(status().isForbidden());mvc.perform(patch("/api/requests/"+id).header("Authorization",bearer(other)).contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Changed\",\"category\":\"Other\",\"location\":\"X\",\"description\":\"No\",\"priority\":\"LOW\"}")).andExpect(status().isForbidden());mvc.perform(get("/api/requests/"+id+"/image").header("Authorization",bearer(other))).andExpect(status().isForbidden());}

 @Test void publicSignupCannotCreatePrivilegedRole()throws Exception{String email="role"+System.nanoTime()+"@campus.edu";mvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content("{\"fullName\":\"Role Attack\",\"email\":\""+email+"\",\"password\":\"Password123\",\"role\":\"ADMIN\"}")).andExpect(status().isCreated());org.junit.jupiter.api.Assertions.assertEquals(User.Role.STUDENT,users.findByEmailIgnoreCase(email).orElseThrow().getRole());}

 @Test void assignmentControlsStaffTransitionsAndRepairNote()throws Exception{User student=user("Student",User.Role.STUDENT),staff=user("Staff",User.Role.STAFF),admin=user("Admin",User.Role.ADMIN);String id=create(student);mvc.perform(post("/api/requests/"+id+"/start").header("Authorization",bearer(staff))).andExpect(status().isForbidden());mvc.perform(patch("/api/requests/"+id+"/assignment").header("Authorization",bearer(admin)).contentType(MediaType.APPLICATION_JSON).content("{\"staffId\":"+staff.getId()+"}")).andExpect(status().isOk());mvc.perform(post("/api/requests/"+id+"/start").header("Authorization",bearer(staff))).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("IN_PROGRESS"));mvc.perform(post("/api/requests/"+id+"/complete").header("Authorization",bearer(staff)).contentType(MediaType.APPLICATION_JSON).content("{\"repairNote\":\"\"}")).andExpect(status().isConflict());mvc.perform(post("/api/requests/"+id+"/complete").header("Authorization",bearer(staff)).contentType(MediaType.APPLICATION_JSON).content("{\"repairNote\":\"Pipe replaced\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("COMPLETED"));mvc.perform(post("/api/requests/"+id+"/start").header("Authorization",bearer(staff))).andExpect(status().isConflict());}

 @Test void invalidImageAndUnauthenticatedImageAccessAreRejected()throws Exception{User student=user("Image",User.Role.STUDENT);MockMultipartFile data=new MockMultipartFile("request","","application/json","{\"title\":\"Photo\",\"category\":\"Other\",\"location\":\"X\",\"description\":\"Bad image\",\"priority\":\"LOW\"}".getBytes());MockMultipartFile image=new MockMultipartFile("image","fake.png","image/png","not an image".getBytes());mvc.perform(multipart("/api/requests/multipart").file(data).file(image).header("Authorization",bearer(student))).andExpect(status().isBadRequest());MaintenanceRequest r=requests.save(new MaintenanceRequest(student,"Valid","Other","X","Description",MaintenanceRequest.Priority.LOW));r.attach("11111111-1111-1111-1111-111111111111","x.png","image/png",1);requests.save(r);mvc.perform(get("/api/requests/"+r.getId()+"/image")).andExpect(status().isUnauthorized());}

 @Test void logoutAndPasswordResetRevokeLoginTokens()throws Exception{User u=user("Tokens",User.Role.STUDENT);String raw=tokens.createToken(u);mvc.perform(post("/api/auth/logout").header("Authorization","Bearer "+raw)).andExpect(status().isNoContent());mvc.perform(get("/api/auth/me").header("Authorization","Bearer "+raw)).andExpect(status().isUnauthorized());String active=tokens.createToken(u),reset="known-reset-token";actionTokens.save(new ActionToken(u,tokens.hash(reset),ActionToken.Purpose.RESET_PASSWORD,java.time.Instant.now().plusSeconds(300)));mvc.perform(post("/api/auth/reset-password").contentType(MediaType.APPLICATION_JSON).content("{\"token\":\""+reset+"\",\"password\":\"NewPassword123\"}")).andExpect(status().isOk());mvc.perform(get("/api/auth/me").header("Authorization","Bearer "+active)).andExpect(status().isUnauthorized());}
}
