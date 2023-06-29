package com.digitalmoney.msaccounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsAccountsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsAccountsApplication.class, args);
	}

}
