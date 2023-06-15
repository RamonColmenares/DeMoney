package com.digitalmoney.msusers.service.feign;

import com.digitalmoney.msusers.application.dto.AccountDTO;
import com.digitalmoney.msusers.application.dto.UserAccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Service
@FeignClient(name = "ms-accounts")
public interface AccountFeignService {

    @PostMapping("/accounts/create")
    ResponseEntity<AccountDTO> createAccount(UserAccountDTO user);

    @GetMapping("/accounts/user/{id}")
    ResponseEntity<AccountDTO> findAccountByUserId(@PathVariable Long id);

}
