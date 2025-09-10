// src/test/java/cz/solutia/acme/core/service/PasswordServiceTest.java
package cz.solutia.acme.core.service;

import cz.solutia.acme.core.model.User;
import cz.solutia.acme.core.repository.UserRepository;
import cz.solutia.acme.exception.InvalidCurrentPasswordException;
import cz.solutia.acme.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordServiceTest {

    private UserRepository repo;
    private PasswordEncoder encoder;
    private PasswordService service;

    @BeforeEach
    void setUp() {
        repo = mock(UserRepository.class);
        encoder = new BCryptPasswordEncoder();
        service = new PasswordService(encoder, repo);
    }

    @Test
    void changePassword_success() {
        User u = new User();
        u.setUsername("alice");
        u.setPassword(encoder.encode("OldPass1!"));

        when(repo.findByUsername("alice")).thenReturn(Optional.of(u));
        when(repo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        service.changePassword("alice", "OldPass1!", "NewPass1!");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repo).save(captor.capture());
        assertTrue(encoder.matches("NewPass1!", captor.getValue().getPassword()), "New hash must match new raw password");
        assertFalse(encoder.matches("OldPass1!", captor.getValue().getPassword()), "Old password must not match after change");
    }

    @Test
    void changePassword_failure_userNotFound() {
        when(repo.findByUsername("ghost")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.changePassword("ghost", "x", "y"));
        verify(repo, never()).save(any());
    }

    @Test
    void changePassword_failure_currentPasswordInvalid() {
        User u = new User();
        u.setUsername("alice");
        u.setPassword(encoder.encode("OldPass1!"));
        when(repo.findByUsername("alice")).thenReturn(Optional.of(u));

        assertThrows(InvalidCurrentPasswordException.class,
                () -> service.changePassword("alice", "WRONG", "NewPass1!"));
        verify(repo, never()).save(any());
    }

    @Test
    void  changePassword_failure_newPasswordSameAsOld(){
        User u = new User();
        u.setUsername("alice");
        u.setPassword(encoder.encode("OldPass1!"));
        when(repo.findByUsername("alice")).thenReturn(Optional.of(u));

        assertThrows(cz.solutia.acme.exception.SamePasswordException.class,
                () -> service.changePassword("alice", "OldPass1!", "OldPass1!"));
        verify(repo, never()).save(any());
    }
}
