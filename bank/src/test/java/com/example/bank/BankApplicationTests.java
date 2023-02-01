package com.example.bank;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.bank.entity.Account;
import com.example.bank.repository.AccountRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class BankApplicationTests {

	@Autowired
	AccountRepository accountRepository;
	
	@Test
	void addAccount() {
		accountRepository.save(new Account("10002","kang","1234","Gold",0));
	}

}
