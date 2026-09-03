package com.example.blog.service;

import com.example.blog.dto.RegistrationForm;
import com.example.blog.model.Role;
import com.example.blog.model.User;
import com.example.blog.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 用户注册与查询。
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegistrationForm form) {
        if (userRepository.existsByUsername(form.getUsername())) {
            throw new IllegalArgumentException("用户名已被使用");
        }
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new IllegalArgumentException("邮箱已被注册");
        }
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new IllegalArgumentException("两次输入的密码不一致");
        }
        User user = new User(
                form.getUsername(),
                form.getEmail(),
                passwordEncoder.encode(form.getPassword()),
                Role.MEMBER);
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
