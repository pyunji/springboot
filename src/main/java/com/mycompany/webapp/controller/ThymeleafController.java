package com.mycompany.webapp.controller;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mycompany.webapp.dto.Board;

import lombok.extern.java.Log;

@Controller
@Log
@RequestMapping("/thymeleaf")
public class ThymeleafController {
	@RequestMapping("/content")
	public String content() {
		log.info("실행");
		return "thymeleaf/content";
	}

	@RequestMapping("/text")
	public String text(Model model) {
		log.info("실행");
		Board board = new Board();
		board.setBno(1);
		board.setBtitle("Spring Boot Template Engine");
		board.setBcontent(
				"<span style='color:red'>Thymeleaf</span> is a modern server-side <b>Java template engine</b>");
		board.setMid("thymeleaf");
		board.setBdate(new Date());
		
		model.addAttribute("board", board);
		return "thymeleaf/text";
	}
}
