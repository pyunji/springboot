package com.mycompany.webapp.security;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import lombok.extern.slf4j.Slf4j;

@EnableWebSecurity
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		log.info("configure(HttpSecurity http) 실행");
		// 로그인 방식 설정
		http.formLogin()
		.loginPage("/security/loginForm")	// default: /login(GET)
		.usernameParameter("mid")	// default: username
		.passwordParameter("mpassword") // default: password
		.loginProcessingUrl("/login") // default: /login(POST) .디폴트 로그인 요청 경로. 요청 매핑 메서드 만들 필요 없음. 시큐리티가 정해놓은 경로. POST 방식으로 login 경로를 요청해야한다
		.defaultSuccessUrl("/security/content") // 로그인 성공 시 이동할 경로(리다이렉트)
		.failureUrl("/security/loginError") // default: /login?error . 로그인 실패 시 이동할 경로
		;
		
		// 로그아웃 설정
		http.logout()
			.logoutUrl("/logout")	// default: /logout
			.logoutSuccessUrl("/security/content");
		
		// URL 권한 설정
		http.authorizeRequests()
			.antMatchers("/security/admin/**").hasAuthority("ROLE_ADMIN")
			.antMatchers("/security/manager/**").hasAuthority("ROLE_MANAGER")
			.antMatchers("/security/user/**").authenticated()
			.antMatchers("/**").permitAll();
		
		// 권한 없음(403)일 경우 이동할 경로 설정
		http.exceptionHandling().accessDeniedPage("/security/accessDenied");
		
		// CSRF 비활성화
		http.csrf().disable();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		log.info("configure(AuthenticationManagerBuilder auth) 실행");
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		log.info("configure(WebSecurity web) 실행");
	}
}
