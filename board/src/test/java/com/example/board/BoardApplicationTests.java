package com.example.board;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.board.entity.Board;
import com.example.board.repository.BoardRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class BoardApplicationTests {
	
	@Autowired
	BoardRepository boardRepository;
	@Test
	void contextLoads() {
		PageRequest pageRequest = PageRequest.of(0, 2);   // 현재페이지, 하나의 페이지 로우수
		Page<Board> pages = boardRepository.findAll(pageRequest);
		List<Board> boards = pages.getContent();
		for(Board board : boards) {
			System.out.println(board);
		}
	}

}
