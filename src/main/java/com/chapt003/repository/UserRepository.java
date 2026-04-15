package com.chapt003.repository;

import com.chapt003.entity.User;
import com.chapt003.entity.enums.UserRole;
import com.chapt003.entity.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByMobile(String mobile);
    
    Optional<User> findByWechatOpenId(String wechatOpenId);
    
    @Query("SELECT u FROM User u WHERE u.email = :emailOrMobile OR u.mobile = :emailOrMobile")
    Optional<User> findByEmailOrMobile(@Param("emailOrMobile") String emailOrMobile);
    
    boolean existsByEmail(String email);
    
    boolean existsByMobile(String mobile);

    @Query("SELECT u FROM User u WHERE " +
           "(:search IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.mobile) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:status IS NULL OR u.status = :status)")
    Page<User> findAllAdmin(@Param("search") String search, 
                           @Param("role") UserRole role, 
                           @Param("status") UserStatus status, 
                           Pageable pageable);

    long countByRoleAndStatus(UserRole role, UserStatus status);
}
