package com.mycompany.webapp.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mycompany.webapp.dto.Board;
import com.mycompany.webapp.dto.Pager;
import com.mycompany.webapp.service.BoardService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/dao")
@Slf4j
public class DaoController {
	@Resource private BoardService boardService;
	
	@RequestMapping("/content")
	public String content() {
		log.info("실행");
		return "dao/content";
	}
	
	@RequestMapping("/boardList")
	public String boardList(@RequestParam(defaultValue = "1") int pageNo, Model model) {
		log.info("실행");

		int totalRows = boardService.getTotalBoardNum();
		if (totalRows < 1000) {
			for (int i = 1; i <= 1000; i++) {
				Board board = new Board();
				board.setBtitle("제목" + i);
				board.setBcontent("내용" + i);
				board.setMid("user");
				boardService.writeBoard(board);
			}
		}

		totalRows = boardService.getTotalBoardNum();
		Pager pager = new Pager(5, 5, totalRows, pageNo);
		model.addAttribute("pager", pager);

		List<Board> boards = boardService.getBoards(pager);
		model.addAttribute("boards", boards);
		return "dao/boardList";
	}
}
