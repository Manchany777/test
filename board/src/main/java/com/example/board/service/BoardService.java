package com.example.board.service;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.board.entity.Board;
import com.example.board.repository.BoardRepository;
import com.example.board.vo.PageInfo;

@Service
public class BoardService {
	
	@Autowired
	BoardRepository boardRepository;
	
	public void writeBoard(Board board) throws Exception {
		boardRepository.save(board);
	}
	
	public void writeBoard2(Board board, MultipartFile file) throws Exception {
		String filename = null;
		if(file!=null && !file.isEmpty()) { 	// file upload
			String path = "C:/Users/CHOI/uploadBoard/";
			filename = file.getOriginalFilename();
			File dFile = new File(path+filename);
			file.transferTo(dFile);
		}
		board.setFilename(filename);
		boardRepository.save(board);
	}
	
	public Board detailBoard(Integer id) throws Exception {
		Optional<Board> oboard = boardRepository.findById(id);
		if(oboard.isPresent()) return oboard.get();
		throw new Exception("글번호 오류");
	}
	
	public List<Board> boardList() throws Exception {
		return boardRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}
	
	public void modifyBoard(Integer id, String subject, String content) throws Exception {
		Optional<Board> oboard = boardRepository.findById(id);
		if(oboard.isEmpty()) throw new Exception("글 조회 오류");
		Board board = oboard.get();
		board.setSubject(subject);
		board.setContent(content);
		boardRepository.save(board);
	}
	
	public List<Board> boardPage(PageInfo pageInfo) throws Exception {
		PageRequest pageRequest = PageRequest.of(pageInfo.getCurPage()-1, 5, 
					Sort.by(Sort.Direction.DESC, "id"));
		Page<Board> pages = boardRepository.findAll(pageRequest);
		
		int maxPage =  pages.getTotalPages();
		int startPage = pageInfo.getCurPage()/10*10+1;         // 1,11,21,31...
		int endPage = startPage+10-1;                          // 10,20,30,40...
		if(endPage>maxPage) endPage=maxPage;
		
		pageInfo.setAllPage(maxPage);
		pageInfo.setStartPage(startPage);
		pageInfo.setEndPage(endPage);
		
		return pages.getContent();
	}
	
	public Integer deleteBoard(Integer id, String password) throws Exception {
		Optional<Board> oboard = boardRepository.findById(id);
		if(oboard.isEmpty()) return -1;
		Board board = oboard.get();
		if(board.getPassword().equals(password)==false) return -2;
		boardRepository.deleteById(id);
		return 0;
	}
	
}
