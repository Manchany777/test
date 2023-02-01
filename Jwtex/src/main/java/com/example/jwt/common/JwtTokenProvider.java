package com.example.jwt.common;

import java.util.Base64;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
	private String secretKey = "myprojectmyprojectmyprojectmyprojectmyprojectmyprojectmyprojectmyprojectmyproject";
	              // 아무거나 써도 되지만 너무 짧으면 에러남
	private long tokenValidTime =  60*60*1000L;
			// 토큰 유효시간 설정 : 1시간 // L은 뭘까? -> 결과값이 int에서는 수치를 넘어가면 오버플로우 되서 마이너스값이 출력되는 데 그걸 막기위해 Long으로 설정
	private final UserDetailsService userDetailsService;
	
	@PostConstruct       // 생성된 후 다른 객체에 주입되기 전에 실행
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
	}
	
	// JWT 토큰 생성
	public String createToken(String userPk) {
		// JWT payload에 저장되는 정보단뒤, 보통 user를 식별하는 값을 넣는다.
		Claims claims = Jwts.claims().setSubject(userPk);
		//claims.put("","");  // 다른 정보도 넣을 수 있다.(넣고 싶은거 넣으면 됨)
		Date now = new Date();
		return Jwts.builder()
				.setClaims(claims)
				.setId(userPk)
				.setIssuedAt(now)                                          // 만들어진 시간
//				.setExpiration(new Date(now.getTime())) // 만료시간(즉시)  ==> 얘는 UserController에서 가져가고
				.setExpiration(new Date(now.getTime()+tokenValidTime*5)) // 만료시간 (*5 = 5시간)
				.signWith(SignatureAlgorithm.HS256, secretKey)        // 알고리즘 이용해서 키로 서명
				.compact();
	}
	
//	// JWT 토큰 생성
//	public String createToken2(String userPk) {          // 2가 정상적인 것
//		// JWT payload에 저장되는 정보단뒤, 보통 user를 식별하는 값을 넣는다.
//		Claims claims = Jwts.claims().setSubject(userPk);
//		//claims.put("","");  // 다른 정보도 넣을 수 있다.(넣고 싶은거 넣으면 됨)
//		Date now = new Date();
//		return Jwts.builder()
//				.setClaims(claims)
//				.setId(userPk)
//				.setIssuedAt(now)                                          // 만들어진 시간
//				.setExpiration(new Date(now.getTime()+tokenValidTime*5)) // 만료시간 (*5 = 5시간)  => 애는 JwtAuthenticationIntercepter에서 가져감
//				.signWith(SignatureAlgorithm.HS256, secretKey)        // 알고리즘 이용해서 키로 서명
//				.compact();
//	}
	
	public String refreshToken(String userPk) {
		// JWT payload에 저장되는 정보단뒤, 보통 user를 식별하는 값을 넣는다.
		Claims claims = Jwts.claims().setSubject(userPk);
		//claims.put("","");  // 다른 정보도 넣을 수 있다.(넣고 싶은거 넣으면 됨)
		Date now = new Date();
		return Jwts.builder()
				.setClaims(claims)
				.setId(userPk)
				.setIssuedAt(now)                                          // 만들어진 시간
				.setExpiration(new Date(now.getTime()+tokenValidTime*5)) // 만료시간 (*5 = 5시간)
				.signWith(SignatureAlgorithm.HS256, secretKey)        // 알고리즘 이용해서 키로 서명
				.compact();
	}
	
	// 토큰에서 회원정보 추출
	public String getUserPk(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
		
	}
	
	// JWT 토큰에서 인증 정보 조회 (DB와 대조)
	public Authentication getAuthentication(String token) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
		return new UsernamePasswordAuthenticationToken (userDetails, "", userDetails.getAuthorities());
	}
	
	// Request의 Header에서 AccessToken 값을 가져온다. "Authorization : token"  <-- 이런식으로 정해져 있음
	public String resolveToken(HttpServletRequest request) {
		return request.getHeader("Authorization");             // 토큰만 걸러옴
	}
	
	// 토큰의 유효성+만료시간 확인
	public boolean validateToken(String jwtToken) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
			return !claims.getBody().getExpiration().before(new Date());
		} catch(Exception e) {
			return false;
		}
	}
}
