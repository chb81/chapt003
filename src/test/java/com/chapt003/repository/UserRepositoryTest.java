package com.chapt003.repository;

import com.chapt003.entity.User;
import com.chapt003.entity.enums.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_WithExistingEmail_ShouldReturnUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setMobile("13800138000");
        user.setPassword("encodedPassword");
        user.setStatus(UserStatus.UNVERIFIED);
        entityManager.persistAndFlush(user);

        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void findByEmail_WithNonExistingEmail_ShouldReturnEmpty() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(found.isPresent());
    }

    @Test
    void findByMobile_WithExistingMobile_ShouldReturnUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setMobile("13800138000");
        user.setPassword("encodedPassword");
        user.setStatus(UserStatus.UNVERIFIED);
        entityManager.persistAndFlush(user);

        Optional<User> found = userRepository.findByMobile("13800138000");

        assertTrue(found.isPresent());
        assertEquals("13800138000", found.get().getMobile());
    }

    @Test
    void existsByEmail_WithExistingEmail_ShouldReturnTrue() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setMobile("13800138000");
        user.setPassword("encodedPassword");
        user.setStatus(UserStatus.UNVERIFIED);
        entityManager.persistAndFlush(user);

        assertTrue(userRepository.existsByEmail("test@example.com"));
    }

    @Test
    void existsByEmail_WithNonExistingEmail_ShouldReturnFalse() {
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    void existsByMobile_WithExistingMobile_ShouldReturnTrue() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setMobile("13800138000");
        user.setPassword("encodedPassword");
        user.setStatus(UserStatus.UNVERIFIED);
        entityManager.persistAndFlush(user);

        assertTrue(userRepository.existsByMobile("13800138000"));
    }

    @Test
    void existsByMobile_WithNonExistingMobile_ShouldReturnFalse() {
        assertFalse(userRepository.existsByMobile("13900139000"));
    }
}
