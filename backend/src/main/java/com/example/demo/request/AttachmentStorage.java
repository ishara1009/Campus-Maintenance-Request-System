package com.example.demo.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.*;import java.nio.file.*;import java.util.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class AttachmentStorage {
 private final Path root; public AttachmentStorage(@Value("${app.upload.directory:./uploads}") String dir){try{root=Paths.get(dir).toAbsolutePath().normalize();Files.createDirectories(root);}catch(IOException e){throw new IllegalStateException("Cannot create upload directory",e);}}
 public Stored save(MultipartFile file){
  if(file==null||file.isEmpty())return null;if(file.getSize()>5L*1024*1024)throw bad("Image must be 5 MB or smaller.");
  try{byte[] header=file.getInputStream().readNBytes(12);String type=detect(header);if(type==null)throw bad("Only genuine JPEG or PNG images are allowed.");
   String declared=file.getContentType();if(declared!=null&&!declared.equalsIgnoreCase(type))throw bad("The image content does not match its file type.");
   String id=UUID.randomUUID().toString();Path target=path(id);try(InputStream in=file.getInputStream()){Files.copy(in,target,StandardCopyOption.REPLACE_EXISTING);}return new Stored(id,safeName(file.getOriginalFilename()),type,file.getSize());
  }catch(IOException e){throw new ResponseStatusException(BAD_REQUEST,"The image could not be saved.",e);}
 }
 public Path path(String id){if(id==null||!id.matches("[0-9a-fA-F-]{36}"))throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND);Path p=root.resolve(id).normalize();if(!p.startsWith(root))throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND);return p;}
 public void delete(String id){if(id==null)return;try{Files.deleteIfExists(path(id));}catch(IOException ignored){}}
 private String detect(byte[] b){if(b.length>=8&&(b[0]&255)==137&&b[1]==80&&b[2]==78&&b[3]==71&&b[4]==13&&b[5]==10&&b[6]==26&&b[7]==10)return "image/png";if(b.length>=3&&(b[0]&255)==255&&(b[1]&255)==216&&(b[2]&255)==255)return "image/jpeg";return null;}
 private String safeName(String name){return name==null?"image":Paths.get(name).getFileName().toString().replaceAll("[^a-zA-Z0-9._ -]","_");}
 private ResponseStatusException bad(String m){return new ResponseStatusException(BAD_REQUEST,m);} public record Stored(String id,String name,String type,long size){}
}
