package com.digitalmoney.msusers.service;

import com.digitalmoney.msusers.application.dto.*;
import com.digitalmoney.msusers.application.exception.UserBadRequestException;
import com.digitalmoney.msusers.application.exception.UserNotFoundException;
import com.digitalmoney.msusers.application.exception.UserInternalServerException;
import com.digitalmoney.msusers.application.exception.UserUnauthorizedException;
import com.digitalmoney.msusers.application.dto.AccountCreationDTO;
import com.digitalmoney.msusers.application.dto.UserAccountDTO;
import com.digitalmoney.msusers.application.dto.UserRegisterDTO;
import com.digitalmoney.msusers.application.dto.UserRegisterResponseDTO;
import com.digitalmoney.msusers.persistency.entity.User;
import com.digitalmoney.msusers.persistency.repository.UserRepository;
import com.digitalmoney.msusers.service.feign.AccountFeignService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Service @AllArgsConstructor @Log4j2
public class UserService {
    private final UserRepository userRepository;
    private final ObjectMapper mapper;
    private final AccountFeignService accountFeignService;
    private final KeycloakService keycloakService;

    public UserRegisterResponseDTO createUser(UserRegisterDTO user) throws UserInternalServerException {
         User userToStore = mapper.convertValue(user, User.class);
         BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
         userToStore.setPassword(passwordEncoder.encode(userToStore.getPassword()));
         User userStored = userRepository.save(userToStore);

         UserAccountDTO accountToCreate = new UserAccountDTO(userStored.getId(), userStored.getDni());

         AccountCreationDTO account;
         try {
             account = accountFeignService.createAccount(accountToCreate).getBody();
         } catch (Exception e) {
             log.error("Error connecting with account service, {} threw {}", accountToCreate, e);
             userRepository.delete(userStored);
             throw new UserInternalServerException(e.getMessage());
         }

         return new UserRegisterResponseDTO(
                 userStored.getFirstName(),
                 userStored.getLastName(),
                 userStored.getDni(),
                 userStored.getEmail(),
                 userStored.getPhone(),
                 account.getCvu(),
                 account.getAlias());
    }

    public User findUserByID(String id) throws UserNotFoundException, UserUnauthorizedException, UserBadRequestException {
        validateID(id);

        Optional<User> userFound = userRepository.findById(id);

        if (!userFound.isPresent()) {
            throw new UserNotFoundException("the user with id " + id + " was not found");
        }

        //check if the user who did the request is the correct one
        String emailReq = ((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        log.error(emailReq);
        log.error(userFound.get().getEmail());
        if (!userFound.get().getEmail().equals(emailReq)) {
            throw new UserUnauthorizedException("you can only request your user details");
        }

        return userFound.get();
    }

    public User updateUser(String id, UserUpdateDTO userUpdates) throws UserNotFoundException, UserUnauthorizedException, UserBadRequestException, UserInternalServerException {
        User userFound = findUserByID(id);
        String keycloakEmail = userFound.getEmail();

        if (userUpdates.firstName() != null && !userUpdates.firstName().isEmpty()){
            userFound.setFirstName(userUpdates.firstName());
        }
        if (userUpdates.lastName() != null && !userUpdates.lastName().isEmpty()){
            userFound.setLastName(userUpdates.lastName());
        }
        if (userUpdates.email() != null && !userUpdates.email().isEmpty()){
            Optional<User> userEmail = userRepository.findByEmail(userUpdates.email());
            if (userEmail.isPresent()) {
                throw new UserBadRequestException("the email is already used by another user");
            }

            if (userUpdates.password() == null || userUpdates.password().isEmpty()){
                throw new UserBadRequestException("if you want to change your email, you need to send a password");
            }

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            userFound.setPassword(passwordEncoder.encode(userUpdates.password()));
            userFound.setEmail(userUpdates.email());
        }
        if (userUpdates.dni() != null && !userUpdates.dni().isEmpty()){
            Optional<User> userDNI = userRepository.findByDni(userUpdates.dni());
            if (userDNI.isPresent()) {
                throw new UserBadRequestException("the dni is already used by another user");
            }
            userFound.setDni(userUpdates.dni());
        }
        if (userUpdates.password() != null && !userUpdates.password().isEmpty()){
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            userFound.setPassword(passwordEncoder.encode(userUpdates.password()));
        }
        if (userUpdates.phone() != null && !userUpdates.phone().isEmpty()){
            userFound.setPhone(userUpdates.phone());
        }

        try {
            userRepository.save(userFound);
        } catch (Exception e) {
            throw new UserInternalServerException(e.getMessage());
        }

        Response response = keycloakService.updateInKeycloak(keycloakEmail, userUpdates);
        if (response == null){
            throw new UserNotFoundException("the user was not found on keycloak");
        } else if (response.getStatus() == HttpStatus.NOT_FOUND.value()){
            throw new UserNotFoundException(response.getStatusInfo().toString());
        } else if (response.getStatus() != HttpStatus.OK.value()) {
            throw new UserInternalServerException(response.getStatusInfo().toString());
        }

        return userFound;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void validateID(String id) throws UserBadRequestException {
        try {
            Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new UserBadRequestException("the id must be numeric");
        }
    }
}
