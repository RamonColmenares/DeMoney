package com.digitalmoney.msusers.service;

import com.digitalmoney.msusers.application.dto.AccountCreationDTO;
import com.digitalmoney.msusers.application.dto.UserAccountDTO;
import com.digitalmoney.msusers.application.dto.UserRegisterDTO;
import com.digitalmoney.msusers.application.dto.UserRegisterResponseDTO;
import com.digitalmoney.msusers.application.exception.UserRegisterException;
import com.digitalmoney.msusers.persistency.entity.Account;
import com.digitalmoney.msusers.persistency.entity.User;
import com.digitalmoney.msusers.persistency.repository.UserRepository;
import com.digitalmoney.msusers.service.feign.AccountFeignService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service @AllArgsConstructor @Log4j2
public class UserService {
    private final UserRepository userRepository;
    private final ObjectMapper mapper;
    private final AccountFeignService accountFeignService;

    public UserRegisterResponseDTO createUser(UserRegisterDTO user) throws UserRegisterException {
         User userToStore = mapper.convertValue(user, User.class);
         BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
         userToStore.setPassword(passwordEncoder.encode(userToStore.getPassword()));
         User userStored = userRepository.save(userToStore);

         UserAccountDTO accountToCreate = new UserAccountDTO(userStored.getId(), userStored.getDni());

        // Could not write request: no suitable HttpMessageConverter found for request type [com.digitalmoney.msusers.application.dto.UserAccountDTO]

         AccountCreationDTO account = accountFeignService.createAccount(accountToCreate).getBody();

         return new UserRegisterResponseDTO(
                 userStored.getFirstName(),
                 userStored.getLastName(),
                 userStored.getDni(),
                 userStored.getEmail(),
                 userStored.getPhone(),
                 account.cvu(),
                 account.alias());
    }


    public List<User> findAll() {
        return userRepository.findAll();
    }
}
