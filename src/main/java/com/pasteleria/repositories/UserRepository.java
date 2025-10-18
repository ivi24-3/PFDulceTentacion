package com.pasteleria.repositories;

import com.pasteleria.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByDni(String dni);
    Optional<User> findByToken(String token);
    
    @Query("SELECT u FROM User u WHERE u.nombre LIKE %?1% OR u.email LIKE %?1%")
    java.util.List<User> searchUsers(String keyword);
}
