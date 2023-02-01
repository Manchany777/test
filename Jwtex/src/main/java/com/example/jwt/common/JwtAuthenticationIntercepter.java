package com.example.jwt.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationIntercepter implements HandlerInterceptor {
	
	private final JwtTokenProvider jwtTokenProvider;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// TODO Auto-generated method stub
		String token = jwtTokenProvider.resolveToken(request); // request header로부터 토크 가져오기
		System.out.println("token:"+token);
		if(token==null) return true;    // 1. 토큰이 비어있을 때 : 로그인시 토큰 생성 전이기 떄문에 로그인 처리로 보낸다.  // 즉, 로그인 해야 토큰을 줄 거니까 기본값은 null
		
		String[] tokens = token.split(",");
		if(jwtTokenProvider.validateToken(tokens[0])) {    // 2. acccess token이 유효한 경우, 정상처리
			System.out.println("2. acccess token이 유효");
			// 토큰이 유효하면 토큰으로부터 유저 정보를 받아온다.
			Authentication authentication = jwtTokenProvider.getAuthentication(token);
			// Security Context이 Authentication 객체를 저장한다.
			SecurityContextHolder.getContext().setAuthentication(authentication);
			return true;
		} else if(tokens.length==1) {                // 3. accessToken만 가져왔는데 만료되었을 경우. refreshToken 요청
			System.out.println("3. refreshToken 요청");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);    // accessToken이 만료되었을 때
			response.getWriter().write("{\"rescode\":100}");         // refresh토큰이 타당하지 않다고 할 때 (JSON 방식을 강제로 집어넣음)
			response.getWriter().flush();
		} else if(jwtTokenProvider.validateToken(tokens[1])) {       // 4. refreshToken 유효함. 새로운 두 개의 토큰 재발급
			System.out.println("4. refreshToken 유효함");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			String userPK = jwtTokenProvider.getUserPk(tokens[1]);
			String accessToken = jwtTokenProvider.createToken(userPK);
			String refreshToken = jwtTokenProvider.refreshToken(userPK);
			response.getWriter().write(
					"{\"rescode\":101,\"accessToken\":\""+accessToken+"\",\"refreshToken\":\""+refreshToken+"\"}");  // response에 담아서 보냄
			response.getWriter().flush();
		} else {              // 5. refresh 토큰 만료됨. 재로그인 요청
			System.out.println("5. refresh 토큰 만료됨. 재로그인 요청");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("{\"rescode\":102}");
			response.getWriter().flush();
		}
		return false;
		// return HandlerInterceptor.super.preHandle(request, response, handler);      ==> 이건 초반에 쓴 건데 이제 안쓰는 건가???
	}

}
