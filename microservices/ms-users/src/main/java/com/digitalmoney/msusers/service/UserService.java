package com.digitalmoney.msusers.service;

import com.digitalmoney.msusers.application.dto.UserRegisterDTO;
import com.digitalmoney.msusers.application.dto.UserRegisterResponseDTO;
import com.digitalmoney.msusers.application.exception.UserRegisterException;
import com.digitalmoney.msusers.persistency.entity.User;
import com.digitalmoney.msusers.persistency.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service @AllArgsConstructor @Log4j2
public class UserService {
    private final UserRepository userRepository;
    private final ObjectMapper mapper;

    public UserRegisterResponseDTO createUser(UserRegisterDTO user) throws UserRegisterException {
         User userToStore = mapper.convertValue(user, User.class);
         BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
         userToStore.setPassword(passwordEncoder.encode(userToStore.getPassword()));
         userToStore.setCvu(generateCVU(user.getDni()));
         userToStore.setAlias(generateAlias(user.getDni()));
         return mapper.convertValue(userRepository.save(userToStore), UserRegisterResponseDTO.class);
    }

    private String generateAlias(String dni) throws UserRegisterException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, List<String>> data = mapper.readValue(new File("words.json"), Map.class);

            List<String> words = data.get("words");
            Random rand = new Random(Long.parseLong(dni));

            String word1 = words.get(rand.nextInt(words.size()));
            String word2 = words.get(rand.nextInt(words.size()));
            String word3 = words.get(rand.nextInt(words.size()));

            return word1 + "." + word2 + "." + word3;
        } catch (IOException e) {
            log.error("Error reading words file", e);
        }
        throw new UserRegisterException("Could not generate alias. Try again later.");
    }

    private String generateCVU(String DNI) {
        CVUService cvuService = new CVUService(Long.parseLong(DNI));
        return cvuService.generateCVU();
    }
    public List<User> findAll() {
        return userRepository.findAll();
    }
}
