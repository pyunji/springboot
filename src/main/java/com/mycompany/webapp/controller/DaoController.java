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
	// pageNo를 쿼리 파라미터로 받아온다
	public String boardList(@RequestParam(defaultValue = "1") int pageNo, Model model) {
		log.info("실행");

		// DB에 저장되어있는 게시물의 개수를 가져온다
		int totalRows = boardService.getTotalBoardNum();
		// DB에 저장되어있는 게시물의 개수가 1000개가 안되면, 1000개의 게시물을 추가한다
		if (totalRows < 1000) {
			for (int i = 1; i <= 1000; i++) {
				Board board = new Board();
				board.setBtitle("제목" + i);
				board.setBcontent("내용" + i);
				board.setMid("user");
				boardService.writeBoard(board);
			}
		}

		// DB에 저장되어있는 게시물의 수를 다시 센다
		totalRows = boardService.getTotalBoardNum();
		// Pager(int rowsPerPage, int pagesPerGroup, int totalRows, int pageNo)
		// Pager 객체를 만들 때 4가지의 값을 세팅함으로써 Pager DTO에서 다음 값을 얻는 연산이 일어난다.
		Pager pager = new Pager(5, 5, totalRows, pageNo);
		// totalPageNo, totalGroupNo, groupNo, startPageNo, endPageNo, startRowNo, endRowNo
		model.addAttribute("pager", pager);

		// startRowNo 부터 endRowNo 까지 해당하는 게시물을 가져온다
		List<Board> boards = boardService.getBoards(pager);
		model.addAttribute("boards", boards);
		return "dao/boardList";
	}

	@GetMapping("/boardWriteForm")
	// 새글쓰기 누르면 화면 폼 보여주기
	public String boardWriteForm() {
		log.info("실행");
		return "dao/boardWriteForm";
	}

	@PostMapping("/boardWrite")
	// 글 작성 후 글쓰기 버튼을 눌러서 데이터 제출하는 곳
	public String boardWrite(Board board) throws Exception {
		log.info("실행");
		// 첨부파일이 있는 경우
		if (board.getBattach() != null && !board.getBattach().isEmpty()) {
			// 업로드된 파일
			MultipartFile mf = board.getBattach();
			// 업로드된 파일의 원래 이름을 가져옴
			board.setBattachoname(mf.getOriginalFilename());
			// 로컬에 저장될 파일의 이름을 설정함
			board.setBattachsname(new Date().getTime() + "-" + mf.getOriginalFilename());
			// 업로드된 파일의 content type을 가져옴
			board.setBattachtype(mf.getContentType());
			// 파일을 다운로드할 경로 + 파일명 지정
			File file = new File("C:/uploadfiles/" + board.getBattachsname());
			// 설정된 경로에 파일을 다운로드
			mf.transferTo(file);
		}
		// 게시물의 내용을 DB에 저장
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
	// 첨부파일의 링크를 눌렀을 때 요청하는 경로 매핑 메서드
	// responsebody를 쓰는 것 보다 void를 쓰는 것이 더 효율적이다.
	public void battachDownload(int bno, HttpServletResponse response) throws Exception {
		Board board = boardService.getBoard(bno);
		String battachoname = board.getBattachoname();
		if (battachoname == null)
			return;
		// 한글 파일명일 경우 변환해주기 위해 인코딩
		battachoname = new String(battachoname.getBytes("UTF-8"), "ISO-8859-1");
		
		String battachsname = board.getBattachsname();
		String battachtype = board.getBattachtype();

		// 브라우저에서 보여주는 것이 아니라 파일을 로컬에 다운로드 받도록 설정
		response.setHeader("Content-Disposition", "attachment; filename=\"" + battachoname + "\";");
		response.setContentType(battachtype);
		
		// 파일로부터 데이터를 읽는 입력 스트림 생성
		// 원래는 디비에 해당 파일의 데이터가 들어있어야 한다
		// battachspath는 사용자가 다운로드를 요청한 파일이 저장되어있는 저장소의 경로라고 생각하면 된다
		String battachspath = "C:/uploadfiles/" + battachsname;
		InputStream is = new FileInputStream(battachspath);
		
		// 응답 바디에 출력하는 출력 스트림 얻기
		OutputStream os = response.getOutputStream();
		// 입력스트립 -> 출력스트림 으로 데이터 복사하기
		FileCopyUtils.copy(is, os);
		is.close();
		os.flush();
		os.close();
	}

	@GetMapping("/boardUpdateForm")
	public String boardUpdateForm(int bno, Model model) {
		log.info("실행");
		Board board = boardService.getBoard(bno);
		model.addAttribute("board", board);
		return "dao/boardUpdateForm";
	}

	@PostMapping("/boardUpdate")
	public String boardUpdate(Board board) throws Exception {
		log.info("실행");

		if (board.getBattach() != null && !board.getBattach().isEmpty()) {
			MultipartFile mf = board.getBattach();
			board.setBattachoname(mf.getOriginalFilename());
			board.setBattachsname(new Date().getTime() + "-" + mf.getOriginalFilename());
			board.setBattachtype(mf.getContentType());
			File file = new File("C:/uploadfiles/" + board.getBattachsname());
			mf.transferTo(file);
		}

		boardService.updateBoard(board);
		return "redirect:/dao/boardDetail?bno=" + board.getBno();
	}

	@GetMapping("/boardDelete")
	public String boardDelete(int bno) {
		log.info("실행");
		boardService.removeBoard(bno);
		return "redirect:/dao/boardList";
	}
}
