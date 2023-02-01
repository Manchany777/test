package com.example.jwt.common;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;



//@EnableWebSecurity
//public class SecurityConfig extends WebSecurityConfigurerAdapter{           // 곧 서비스 종료할 인터페이스는 이렇게 가운데줄 그어놓음
//	//JWT와 관련되서 필요한 작업들   1.헤더(Header) 2.내용(Payload) 3.서명(Signature)  -> 이 작업은 JwtTokenProvider에서 따로 수행
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http.csrf().disable();
//		http.httpBasic().disable().authorizeRequests()
//			.antMatchers("/login**", "/web-respirces/**", "/actuator/**").permitAll()   // 이 경로로 들어오는건 체크하지 않고 허용하겠다.
//			.antMatchers("/admit/**").hasRole("ADMIN")
//			.antMatchers("/user/**").hasRole("USER")              // 이 두 개는 토큰 주고받는 형태
//			.anyRequest().authenticated();
//		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//		super.configure(http);
//	}
//
//}



// 위의 문장을 이 방식으로 해도 된다. 이 방식을 더 권장
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http)throws Exception {
		http.csrf().disable();
		http.httpBasic().disable().authorizeRequests()
			.antMatchers("/**/*").permitAll()                 // 아래 내용 날리고 이걸로 새로 교체
			.anyRequest().authenticated();
//			.antMatchers("/login**", "/web-respirces/**", "/actuator/**").permitAll()   // 이 경로로 들어오는건 체크하지 않고 허용하겠다.
//			.antMatchers("/admit/**").hasRole("ADMIN")
//			.antMatchers("/user/**").hasRole("USER")              // 이 두 개는 토큰 주고받는 형태
//			.anyRequest().authenticated();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		return http.build();
	}
}
