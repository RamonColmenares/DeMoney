package com.digitalmoney.msusers.service;

import com.digitalmoney.msusers.persistency.entity.User;
import lombok.AllArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.List;

import static java.util.Collections.singletonList;

@Service @AllArgsConstructor
public class KeycloakService {

    private final Keycloak connection;



    public Response createInKeycloak(User user) {
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


        return connection.realm("Master").users().create(userDB);
    }


    public List<UserRepresentation> test() {
        return connection.realm("Master").users().search("admin");
    }

    public Integer findAll() {
        return 2;

    }
}
