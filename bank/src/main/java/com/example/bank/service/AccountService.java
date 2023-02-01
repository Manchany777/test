package com.example.bank.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bank.entity.Account;
import com.example.bank.repository.AccountRepository;

@Service
public class AccountService {
	@Autowired
	AccountRepository accountRepository;

	//getter, setter 문제는 무조건 lombok문제, 우리가 직접 만든게 아니라 롬복에서 import 한 것임 (인텔리제이는 롬복 2개 그래들에 깔아야함)
	public void makeAccount(Account acc) throws Exception {
		acc.setBalance(0);
		accountRepository.save(acc);
	}
	
	public Account accountInfo(String id) throws Exception {
		Optional<Account> oacc = accountRepository.findById(id);
		if(!oacc.isPresent()) throw new Exception("계좌번호 오류");
		return oacc.get();
	}
	
	public Boolean checkDoubleId(String id) throws Exception {
		//계좌번호가 존재할 때 : "true", 존재하지 않을 때 : "false"
		Optional<Account> oacc = accountRepository.findById(id);
		if(oacc.isPresent()) return true; // 계좌번호 이미 존재
		return false;
	}
	
	public Integer deposit(String id, Integer money) throws Exception {
		Optional<Account> oacc = accountRepository.findById(id);
		if(!oacc.isPresent()) throw new Exception("계좌번호 오류");
		Account acc = oacc.get();
		acc.deposit(money);
		accountRepository.save(acc);
		return acc.getBalance();
	}
	
	public Integer withdraw(String id, Integer money) throws Exception {
		Optional<Account> oacc = accountRepository.findById(id);
		if(!oacc.isPresent()) throw new Exception("계좌번호 오류");
		Account acc = oacc.get();
		acc.withdraw(money);
		accountRepository.save(acc);
		return acc.getBalance();
	}
	
	public Integer trasnfor(String sid, String rid, Integer money) throws Exception {
		Optional<Account> osacc = accountRepository.findById(sid);   // id가 아닌 name 등 중복 가능성이 있는건 리턴 타입을 배열로 해줘야함
		if(!osacc.isPresent()) throw new Exception("송신계좌번호 오류");
		Account sacc = osacc.get();
		sacc.withdraw(money);   // 잔액 부족시 오류 발생 예외 처리됨
		Optional<Account> rsacc = accountRepository.findById(rid);
		if(!rsacc.isPresent()) throw new Exception("수신계좌번호 오류");
		Account racc = rsacc.get();
		racc.deposit(money);
		accountRepository.save(sacc);
		accountRepository.save(racc);     // save update할때 쓰는 키
		return sacc.getBalance();
	}
	
	public List<Account> allAccount() throws Exception {
		List<Account> accs = accountRepository.findAll();
		return accs;
	}
}
