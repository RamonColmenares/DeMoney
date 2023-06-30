package com.digitalmoney.msaccounts.service.feign;

import com.digitalmoney.msaccounts.application.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@Service
@FeignClient(name = "ms-users")
public interface UserFeignService {

    @GetMapping("/users/{id}/findUserName")
    ResponseEntity<UserDTO> findUserById(@PathVariable String id);

}
