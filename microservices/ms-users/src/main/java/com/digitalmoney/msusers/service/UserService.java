package com.digitalmoney.msusers.service;

import com.digitalmoney.msusers.application.dto.UserRegisterDTO;
import com.digitalmoney.msusers.persistency.entity.User;
import com.digitalmoney.msusers.persistency.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ObjectMapper mapper;

    public User createUser(UserRegisterDTO user) {
         User userToStore = mapper.convertValue(user, User.class);
         return userRepository.save(userToStore);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
