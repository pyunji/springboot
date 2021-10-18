package com.mycompany.webapp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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

	@GetMapping("/boardWriteForm")
	public String boardWriteForm() {
		log.info("실행");
		return "dao/boardWriteForm";
	}

	@PostMapping("/boardWrite")
	public String boardWrite(Board board) throws Exception {
		log.info("실행");

		if (board.getBattach() != null && !board.getBattach().isEmpty()) {
			MultipartFile mf = board.getBattach();
			board.setBattachoname(mf.getOriginalFilename());
			board.setBattachsname(new Date().getTime() + "-" + mf.getOriginalFilename());
			board.setBattachtype(mf.getContentType());
			File file = new File("C:/uploadfiles/" + board.getBattachsname());
			mf.transferTo(file);
		}

		boardService.writeBoard(board);
		return "redirect:/dao/boardList";
	}

	@GetMapping("/boardDetail")
	public String boardDetail(int bno, Model model) {
		log.info("실행");
		Board board = boardService.getBoard(bno);
		model.addAttribute("board", board);
		return "dao/boardDetail";
	}

	@GetMapping("/battachDownload")
	// responsebody를 쓰는 것 보다 void를 쓰는 것이 더 효율적이다.
	public void battachDownload(int bno, HttpServletResponse response) throws Exception {
		Board board = boardService.getBoard(bno);
		String battachoname = board.getBattachoname();
		if (battachoname == null) return;

		battachoname = new String(battachoname.getBytes("UTF-8"), "ISO-8859-1");
		String battachsname = board.getBattachsname();
		String battachspath = "C:/uploadfiles/" + battachsname;
		String battachtype = board.getBattachtype();

		// 무조건 다운로드 받게 한다
		response.setHeader("Content-Disposition", "attachment; filename=\"" + battachoname + "\";");
		response.setContentType(battachtype);

		InputStream is = new FileInputStream(battachspath);
		OutputStream os = response.getOutputStream();
		FileCopyUtils.copy(is, os);
		is.close();
		os.flush();
		os.close();
	}
}
