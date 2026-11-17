package com.example.demo.auth;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface ActionTokenRepository extends JpaRepository<ActionToken,Long>{
  Optional<ActionToken> findByTokenHashAndPurpose(String hash,ActionToken.Purpose purpose);
}
