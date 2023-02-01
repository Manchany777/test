package com.example.board.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.board.entity.Board;
import com.example.board.service.BoardService;
import com.example.board.vo.PageInfo;

@RestController
public class BoardController {
	
	@Autowired
	BoardService boardService;
	
	@PostMapping("/writeboard")         // 하나씩 가져오는 방법
	public ResponseEntity<String> writeboard(@RequestParam("writer") String writer,
			@RequestParam("password") String password,
			@RequestParam("subject") String subject,
			@RequestParam("content") String content,
			@RequestParam(name="file", required=false) MultipartFile file) {
				
		ResponseEntity<String> res = null;
		try {
			String filename = null;
			if(file!=null && !file.isEmpty()) {    // file upload
				String path = "C:/Users/CHOI/uploadBoard/";
				filename = file.getOriginalFilename();
				File dFile = new File(path+filename);
				file.transferTo(dFile);
			}
			
			boardService.writeBoard(
					new Board(null,writer,password,subject,content,filename));
					
			res = new ResponseEntity<String>("게시글 저장 성공", HttpStatus.OK);
		} catch(Exception e) {
			e.printStackTrace();  // 에러가 나왔을 때 볼 수 있게 해주는 소중한 함수
			res = new ResponseEntity<String>("게시글 저장 실패", HttpStatus.BAD_REQUEST);
		}
			
		return res;		
	}
	
	@PostMapping("/writeboard2")         // 한꺼번에 가져오는 방법
	public ResponseEntity<String> writeboard2(@ModelAttribute Board board,
			@RequestParam(name="file", required = false) MultipartFile file) {
		ResponseEntity<String> res = null;
		try {
			boardService.writeBoard2(board, file);
			res = new ResponseEntity<String>("게시글 저장 성공", HttpStatus.OK);
		} catch(Exception e) {
			e.printStackTrace();
			res = new ResponseEntity<String>("게시글 저장 실패", HttpStatus.BAD_REQUEST);
		}
			
		return res;		
	}
	
	@GetMapping("/boarddetail/{id}")         // 한꺼번에 가져오는 방법
	public ResponseEntity<Board> boarddetail(@PathVariable Integer id) {
		ResponseEntity<Board> res = null;
		try {
			Board board = boardService.detailBoard(id);
			res = new ResponseEntity<Board>(board, HttpStatus.OK);
		} catch(Exception e) {
			e.printStackTrace();
			res = new ResponseEntity<Board>(HttpStatus.BAD_REQUEST);
		}
			
		return res;		
	}
	
	// src는 get방식
	@GetMapping("/img/{filename}") 
	public void imageView(@PathVariable String filename, HttpServletResponse response) {
		ResponseEntity<String> res = null;
		try {
			String path = "C:/Users/CHOI/uploadBoard/";
			FileInputStream fis = new FileInputStream(path+filename);
			OutputStream out = response.getOutputStream();
			FileCopyUtils.copy(fis, out);
			out.flush();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@GetMapping("/boardlist")
	public ResponseEntity<List<Board>> boardlist() {
		ResponseEntity<List<Board>> res = null;
		try {
			List<Board> boards = boardService.boardList();
			res = new ResponseEntity<List<Board>>(boards, HttpStatus.OK);
		} catch(Exception e) {
			e.printStackTrace();
			res = new ResponseEntity<List<Board>>(HttpStatus.BAD_REQUEST);
		}
		return res;
	}
	
	@PutMapping("/modify/{id}")
	public ResponseEntity<String> modifyBoard(@PathVariable Integer id,
			@RequestParam("subject") String subject,
			@RequestParam("content") String content) {
		ResponseEntity<String> res = null;
		try {
			boardService.modifyBoard(id,subject,content);
			res = new ResponseEntity<String>("게시글 수정 성공", HttpStatus.OK);
		} catch(Exception e) {
			e.printStackTrace();
			res = new ResponseEntity<String>("게시글 저장 실패", HttpStatus.BAD_REQUEST);
		}
			
		return res;		
	}
	
	@GetMapping(value={"/boardpage/{page}", "/boardpage"})      // 여러개 묶어줄 땐 value 써줘야 함
	public ResponseEntity<Map<String, Object>> boardpage(@PathVariable(required=false) Integer page) {
		if(page==null) page=1;
		System.out.println(page);
		ResponseEntity<Map<String, Object>> res = null;
		try {
			PageInfo pageInfo = new PageInfo();
			pageInfo.setCurPage(page);
			List<Board> boards = boardService.boardPage(pageInfo);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("pageInfo", pageInfo);
			map.put("boards", boards);
			res = new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
		} catch(Exception e) {
			e.printStackTrace();
			res = new ResponseEntity<Map<String, Object>>(HttpStatus.BAD_REQUEST);
		}
			
		return res;		
	}
	
	@PutMapping("/delete/{id}")
	public ResponseEntity<Integer> delete(@PathVariable Integer id, 
				@RequestParam("password") String password) {
		ResponseEntity<Integer> res = null;
		System.out.println(password);
		try {
			Integer msgno = boardService.deleteBoard(id, password);
			res = new ResponseEntity<Integer>(msgno, HttpStatus.OK);
		} catch(Exception e) {
			e.printStackTrace();
			res = new ResponseEntity<Integer>(HttpStatus.BAD_REQUEST);
		}
		return res;		
	}
}
