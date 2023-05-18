package com.digitalmoney.msusers.service;

import com.digitalmoney.msusers.application.dto.UserRegisterDTO;
import com.digitalmoney.msusers.config.beans.KeycloakConnectionManager;
import com.digitalmoney.msusers.persistency.entity.User;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.List;

import static java.util.Collections.singletonList;

@Service @AllArgsConstructor @Log4j2
public class KeycloakService {
    private final KeycloakConnectionManager keycloakConnectionManager;

    public Response createInKeycloak(UserRegisterDTO user) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(user.getPassword());
        credential.setTemporary(false);

        UserRepresentation userDB = new UserRepresentation();
        userDB.setEnabled(true);
        userDB.setUsername(user.getEmail());
        userDB.setFirstName(user.getFirstName());
        userDB.setLastName(user.getLastName());
        userDB.setEmail(user.getEmail());
        userDB.setCredentials(singletonList(credential));
        userDB.setRealmRoles(singletonList("user"));


        return keycloakConnectionManager.getConnectionAdmin().realm("users-bank").users().create(userDB);
    }


    public List<UserRepresentation> test(String username) {
        return keycloakConnectionManager.getConnectionAdmin().realm("users-bank").users().search(username, true);
    }

    public void removeFromKeycloak(UserRegisterDTO user) {
        try {
            String id = keycloakConnectionManager.getConnectionAdmin().realm("users-bank").users().search(user.getEmail(), true).get(0).getId();
            keycloakConnectionManager.getConnectionAdmin().realm("users-bank").users().delete(id);
        } catch (Exception e) {
            log.error("Error deleting faulty user: ", e);
        }
    }
}
