package com.digitalmoney.msusers.service;

import com.digitalmoney.msusers.persistency.entity.User;
import com.digitalmoney.msusers.persistency.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
