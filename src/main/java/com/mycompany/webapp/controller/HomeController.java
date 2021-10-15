package com.mycompany.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.java.Log;

@Controller
@Log
public class HomeController {
	@RequestMapping("/")
	public String home() {
		log.info("실행");
		// home이라는 템플릿 엔진을 이용해서 응답을 보내라 ~
		return "home"; // html 파일을 찾게된다
	}
}