package com.example.jsoup.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
public class Course {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	private String thumbnail;
	
	@Column
	private String title;
	
	@Column
	private String content;
	
	@Column
	private Integer realprice;    // 참고) DB에는 Integer값도 null로 들어가는데 자바에서는 Int는 null값을 허용하지 않는다. SO, Int를 쓰지 말고 Integer를 써라
	
	@Column
	private Integer saleprice;
	
	@Column
	private String instructor;
	
	@Column
	private String link;
	
	@Column
	private String skill;

	@Column
	private Double rating;
     
    
    
    
    
    
    
}
