package com.example.jwt.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.jwt.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor // final로 되어있는 것만 생성자 만들어줌
@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	private final UserRepository userRepository;   // final 생성자 생성 및 초기화 동시에 함

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		return userRepository.findById(username).get();   // pk로 사용할 id(username)를 넘겨주는 것
	}

}
