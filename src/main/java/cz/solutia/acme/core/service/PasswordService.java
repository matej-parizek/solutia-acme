package cz.solutia.acme.core.service;

import cz.solutia.acme.core.repository.UserRepository;
import cz.solutia.acme.exception.InvalidCurrentPasswordException;
import cz.solutia.acme.exception.SamePasswordException;
import cz.solutia.acme.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordService implements IPasswordService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {
        userRepository.findByUsername(username).map(user -> {
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                throw new InvalidCurrentPasswordException();
            }
            if(passwordEncoder.matches(newPassword, user.getPassword())) {
                throw new SamePasswordException();
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            return userRepository.save(user);
        }).orElseThrow(() -> new UserNotFoundException(username));
    }
}
