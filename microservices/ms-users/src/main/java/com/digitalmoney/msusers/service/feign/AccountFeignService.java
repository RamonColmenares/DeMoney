package com.digitalmoney.msusers.service.feign;

import com.digitalmoney.msusers.application.dto.AccountCreationDTO;
import com.digitalmoney.msusers.application.dto.UserAccountDTO;
import com.digitalmoney.msusers.persistency.entity.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Service
@FeignClient(name = "ms-accounts", url = "http://localhost:8082")
public interface AccountFeignService {

    @RequestMapping(method = RequestMethod.POST, path = "/accounts/create")
    ResponseEntity<AccountCreationDTO> createAccount(@RequestBody UserAccountDTO user);

}
