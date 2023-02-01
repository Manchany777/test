package com.example.jsoup.controller;

import java.util.Objects;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JsoupController {
	//썸네일 링크
	@GetMapping("/thumnail")
	public String thumnail() {
		final String inflearnUrl ="https://www.inflearn.com/courses/it-programming";
		Connection conn = Jsoup.connect(inflearnUrl);
		StringBuilder sb = new StringBuilder();    // 내부부적으로 StringBuffer는 동기화가 되어있고 StringBuilder는 동기화가 빠져있다.
		try {
			Document document = conn.get();
			Elements imgUrlElements = document.getElementsByClass("swiper-lazy");
			for(Element element : imgUrlElements) {       // 우리 실습에서는 import시 swing을 쓸 일이 없다. (swing : 자바가 가지고 있는 윈도우 컴퍼넌트 목록)
				sb.append(element.attr("abs:src")+"<br>");  // attr ; 특정 소스(src)만 가져오게 하는 법 - 실제 사용시에는 가져오길 원하는 속성만 골라서 설정
				// 필요한 값만 추출한 후에 DB에 넣으면 된다.
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	//강의 제목
	@GetMapping("/title")
	public String course_title() {
		final String inflearnUrl ="https://www.inflearn.com/courses/it-programming";
		Connection conn = Jsoup.connect(inflearnUrl);
		StringBuilder sb = new StringBuilder();    // 내부부적으로 StringBuffer는 동기화가 되어있고 StringBuilder는 동기화가 빠져있다.
		try {
			Document document = conn.get();
			Elements titleUrlElements = document.select("div.card-content>div.course_title");
			// 해당 클래스 값을 가지는 다른 페이지가 있는 경우 의도치 않은 자료들까지 크롤링 하게 되는 경우가 있다. 이를 방지하기 위해 특정 태그만 가져오게 만드는 설정
			// 이때는 getElementsByClass가 아니라 select로 해야한다.
			// _ : 후손, > : 자식 (후손과 자식은 다르다)
			for(Element element : titleUrlElements) {
				sb.append(element.text()+"<br>");           // 따로 가져올 속성이 없으니 attr 필요가 없다.  // .text 없이 하면 태그까지 포함해서 가져옴
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	//가격
	@GetMapping("/price")
	public String price() {
		final String inflearnUrl ="https://www.inflearn.com/courses/it-programming";
		Connection conn = Jsoup.connect(inflearnUrl);
		StringBuilder sb = new StringBuilder();
		try {
			Document document = conn.get();
			Elements priceUrlElements = document.getElementsByClass("price");
			for(Element element : priceUrlElements) {
				String price = element.text();                        // 아래의 두 메소드를 취합해서 뿌려줌
				String realPrice = getRealPrice(price);
				String salePrice = getSalePrice(price);
				
				int nrealPrice = toInt(realPrice);
				int nsalePrice = toInt(salePrice);
				sb.append("가격:" + nrealPrice);
				if(nrealPrice!=nsalePrice)
						sb.append("&nbsp;할인가격:"+nsalePrice);
				sb.append("<br>");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	private String getSalePrice(String price) {    // 가격에서 정상가와 할인가가 각각 있는 경우 따로 분리
		String[] prices = price.split(" ");         // split : 문자열을 끊어서 배열로 만들어주는 함수
		return prices.length==1? prices[0]:prices[1];
	}
	private String getRealPrice(String price) {            		   
		return price.split(" ")[0];
	}
	private int toInt(String str) {              // "₩"값과 ","값을 ""공백으로 변경하고 // string 타입을 int로 바꿔주는 것
		str = str.replaceAll("₩", "");
		str = str.replaceAll(",", "");
		return Integer.parseInt(str);
				
	}
	
	//강의 링크 & 평점
	@GetMapping("/link")        // 위의 방식들과는 달리 상세페이지 링크를 다 불러와서 그 중에서 뽑아내는 것이기때문에 속도가 느려야 정상
	public String link() {
		final String inflearnUrl ="https://www.inflearn.com/courses/it-programming";
		Connection conn = Jsoup.connect(inflearnUrl);
		StringBuilder sb = new StringBuilder();
		try {
			Document document = conn.get();
			Elements linkUrlElements = document.select("a.course_card_front");
			for(Element element : linkUrlElements) {
				String innerUrl = element.attr("abs:href");
				Connection innerConn = Jsoup.connect(innerUrl);
				Document innerDoc = innerConn.get();
				Element ratingElement = innerDoc.selectFirst("dashboard-star__num");
						// 클래스로 되어있는 애들은 리턴타입이 늘 배열이다.(여러개라는 뜻) 하지만 지금은 하나만 있기에 배열중 첫번째 것만 가져오게 하기 위해
				// (<-> 같은 내용) Element ratingElement = innerDoc.select("dashboard-star__num").get(0);
				double rating = Objects.isNull(ratingElement)? 0.0:Double.parseDouble(ratingElement.text());
				// Double.parseDouble : 문자열 double을 실제 숫자로 바꾸는 함수
				// double 데이터 타입 <-> Double 컬렉션  : 두 개는 다르다.
				rating = Math.round(rating*100)/100.0;      // Math.round : 반올림해서 정수로 만드는 함수        
				// 4.19001234... -> 419.01234... -> 4.19...
				sb.append(innerUrl +", 평점:" +rating+ "<br>");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	// 강의자, 강의 부가설명, 기술스택
	@GetMapping("/etc")
	public String etc() {
		final String inflearnUrl ="https://www.inflearn.com/courses/it-programming";
		Connection conn = Jsoup.connect(inflearnUrl);
		StringBuilder sb = new StringBuilder();
		try {
			Document document = conn.get();
			Elements instructorElements = document.getElementsByClass("instructor");
			Elements descriptionElements = document.select("p.course_description");
			Elements skillElements = document.select("div.course_skills>span");
			
			for(int i=0; i<instructorElements.size(); i++) {
				String instructor = instructorElements.get(i).text();
				String description = descriptionElements.get(i).text();
				String skills = skillElements.get(i).text().replace("\\s", "");    // \\s : 단일 공백 ​​문자와 일치
				
				sb.append("강의자:" + instructor + "<br>");
				sb.append("강의 부가설명:" + description + "<br>");
				sb.append("기술스택:" + skills + "<br><br>");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
