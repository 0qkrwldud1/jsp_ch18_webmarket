package mvc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import mvc.model.BoardDAO;
import mvc.model.BoardDTO;
import mvc.model.FimageDTO;

public class BoardController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// 해당게시판의 페이징 처리하기 위한 상수값 -> 목록에 보여주는 갯수.
	static final int LISTCOUNT = 5; 
	
	//get으로 전송되어도 포스트 방식을 다 처리하는 것으로 예제 구성이 되어있음.
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		//requestURI주소 부분에서
		//contextPath 프로젝트 부분을 자르기를 하고
		//command : /BoardListAction.do 이런 형식으로 가져오기 위해서.
		String RequestURI = request.getRequestURI();
		System.out.println("RequestURI : " +RequestURI);
		
		String contextPath = request.getContextPath();
		System.out.println("ContextPath : " +contextPath);
		
		String command = RequestURI.substring(contextPath.length());
		System.out.println("command : " +command);
		
		response.setContentType("text/html; charset=utf-8");
		request.setCharacterEncoding("utf-8");
		
		
		//게시판을 클릭시 첫번째 조건문에서 처리하는 과정을 보자.
		if (command.equals("/BoardListAction.do")) {//등록된 글 목록 페이지 출력하기
			// 게시판의 목록에 관련된 
			requestBoardList(request);
			RequestDispatcher rd = request.getRequestDispatcher("./board/list.jsp");
			rd.forward(request, response);
		} else if (command.equals("/BoardWriteForm.do")) { // 글 등록 페이지 출력하기
				requestLoginName(request);
				RequestDispatcher rd = request.getRequestDispatcher("./board/writeForm.jsp");
				rd.forward(request, response);				
		} else if (command.equals("/BoardWriteAction.do")) {// 새로운 글 등록하기
				requestBoardWrite(request);
				RequestDispatcher rd = request.getRequestDispatcher("/BoardListAction.do");
				rd.forward(request, response);						
		} else if (command.equals("/BoardViewAction.do")) {//선택된 글 상세 페이지 가져오기
				requestBoardView(request);
				RequestDispatcher rd = request.getRequestDispatcher("/BoardView.do");
				rd.forward(request, response);						
		} else if (command.equals("/BoardView.do")) { //글 상세 페이지 출력하기
				RequestDispatcher rd = request.getRequestDispatcher("./board/view.jsp");
				rd.forward(request, response);	
		} else if (command.equals("/BoardUpdateAction.do")) { //선택된 글의 조회수 증가하기
				requestBoardUpdate(request);
				RequestDispatcher rd = request.getRequestDispatcher("/BoardListAction.do");
				rd.forward(request, response);
		}else if (command.equals("/BoardDeleteAction.do")) { //선택된 글 삭제하기
				requestBoardDelete(request);
				RequestDispatcher rd = request.getRequestDispatcher("/BoardListAction.do");
				rd.forward(request, response);				
		} 
	}
	//등록된 글 목록 가져오기	
	public void requestBoardList(HttpServletRequest request){
		
		// 임시로 게시판에 정보를 출력하기 위해서.
		String RequestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String command = RequestURI.substring(contextPath.length());
		int LISTCOUNT = BoardController.LISTCOUNT;
		
		// 게시판에서 해당 디비에 접근을 하기위한 sql 문장이 모아져있다.
		// 싱글톤 패턴으로 하나의 객체를 이용하고 있음.
		// 게시판에 글쓰기 수정하기 삭제하기 등 여러 메서드들이 dao라는 객체에 담겨져있다.
		// 컬렉션에는 게시판의 글을 담아두는 역할
		// 게시판의 하나의 글들은 board dto 타입으 객체들
		// boardlist -> 게시판의 각각의 게시글을 담아둠
		// 디비에 연결.
		BoardDAO dao = BoardDAO.getInstance();
		List<BoardDTO> boardlist = new ArrayList<BoardDTO>();
		
	  	int pageNum=1;
		int limit=LISTCOUNT;
		
		if(request.getParameter("pageNum")!=null)
			pageNum=Integer.parseInt(request.getParameter("pageNum"));
				// 만약 request에 담겨진 페이지 정보가 널이 아니면
				// parseInt -> 해당문자열을 정수 값으로 변환해서 담는다.
		String items = request.getParameter("items");
				// items 검색할 때 선택할 조건 제목, 글쓴이, 글
		String text = request.getParameter("text");
				// 검색할 때 텍스트 사용.
		
		// 해당 게시글의 모든 갯수를 가져오는 역할.
		int total_record=dao.getListCount(items, text);
		boardlist = dao.getBoardList(pageNum,limit, items, text); 
		
		int total_page;
		
		// 11/5 -> 2.2
		// Math.floor -> 2
		// total_page는 3
		if (total_record % limit == 0){     
	     	total_page =total_record/limit;
	     	Math.floor(total_page);  
	     	
		}
		else{
		   total_page =total_record/limit;
		   double total_page_test =  Math.floor(total_page);
		   System.out.println("total_page_test의 값:"+ total_page_test);
		   Math.floor(total_page); 
		   total_page =  total_page + 1; 
		}		
		
		request.setAttribute("RequestURI", RequestURI);
		request.setAttribute("contextPath", contextPath);
		request.setAttribute("command", command);
		
		request.setAttribute("LISTCOUNT", LISTCOUNT);
   		request.setAttribute("pageNum", pageNum);		  
   		request.setAttribute("total_page", total_page);   
		request.setAttribute("total_record",total_record); 
		request.setAttribute("boardlist", boardlist);								
	}
	
	//인증된 사용자명 가져오기
	public void requestLoginName(HttpServletRequest request){
					
		String id = request.getParameter("id");
		
		BoardDAO  dao = BoardDAO.getInstance();
		
		String name = dao.getLoginNameById(id);		
		
		request.setAttribute("name", name);									
	}
	// 새로운 글 등록하기
	// 추가로 이미지 등록하는 메서드를 따로 분리해서 작업 
	// 여기안에 해당메서드를 호출
	//멀티파트 인코딘 타입은 -> 받는 타입이 바뀌게된다.
	public void requestBoardWrite(HttpServletRequest request){
		
	
		String filename = "";
		String realFolder = "C:\\jsp_workspace\\ch_18webmarket2\\src\\main\\webapp\\resources\\board_images"; //웹 어플리케이션상의 절대 경로
		String encType = "utf-8"; //인코딩 타입
		int maxSize = 10 * 1024 * 1024; //최대 업로드될 파일의 크기5Mb

		MultipartRequest multi;
		
		try {
			multi = new MultipartRequest
					(request, realFolder, maxSize, encType, new DefaultFileRenamePolicy());
			String productId = multi.getParameter("productId");
			
			// dao 게시판에 관련된 crud 메서드들이 있다.
			BoardDAO dao = BoardDAO.getInstance();		
			
			// 사용자가 입력한 글들을 받아서 임시로 저장할 객체가 필요.
			// 임시객체는 해당 디비에 전다할 형식 dto.
			BoardDTO board = new BoardDTO();
			FimageDTO fileDTO = new FimageDTO();
			
			// 해당 넘을 받아오는 작업x
			// 사용자로부터 입력받은 내용을 임시객체에 담아두는 작업.
			// request.getParameter에 담긴 내용을 dto의 객체에 담아준다.
			board.setId(multi.getParameter("id"));
			board.setName(multi.getParameter("name"));
			board.setSubject(multi.getParameter("subject"));
			board.setContent(multi.getParameter("content"));	
			
			// 콘솔상에 출력해보기 -> 해당값을 잘 받아오고 있는지 확인.
			System.out.println(multi.getParameter("name"));
			System.out.println(multi.getParameter("subject"));
			System.out.println(multi.getParameter("content"));
			java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy/MM/dd(HH:mm:ss)");
			String regist_day = formatter.format(new java.util.Date()); 
			
			//조회수
			board.setHit(0);
			//날짜
			board.setRegist_day(regist_day);
			//Ip
			board.setIp(request.getRemoteAddr());			
			
			//board dao의 매서드를 사용하기위해 dao객체 생성.
			//board dto타입의 board라는 객체 생성 후 사용자가 입력한 내용을 담는다.
			//dao에 insertBoard라는 메서드 사용 -> 매개변수 board
			dao.insertBoard(board);
			
			//해당이미지를 저장하는 메서드만들기
			//dao.insertimages(num)를 만들기
			//게시글 번호 필요 -> 매개변수
			//해당매개변수에서 게시글 번호로 불러올수 있음.
			//하나의 게시글에 첨부된 이미지들의 목록도 있음.
			
			//보드에서 이미지를 넣는 경우
			// 한개만 넣거나, 2개이상이 들어갈 수도 있고, 또는 파일이미지가 없는 경우가 있을 수있음.
			//if 문으로 경우를 설정해줌.
			if(board.getFileList() != null) {
			dao.insertImages(board, fileDTO);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

										
	}
	//선택된 글 상세 페이지 가져오기
	public void requestBoardView(HttpServletRequest request){
					
		BoardDAO dao = BoardDAO.getInstance();
		int num = Integer.parseInt(request.getParameter("num"));
		int pageNum = Integer.parseInt(request.getParameter("pageNum"));	
		
		// boarddto 타입의 board생성
		// board에 dao의 getBoardByNum(num, pageNum)를 담는다.
		BoardDTO board = new BoardDTO();
		board = dao.getBoardByNum(num, pageNum);
		//getBoardByNum(num, pageNum) -> board테이블에서 해당 num의 칼럼을 가져오는 쿼리문.
		
		//request에 setAttribute로 각 내용을 담는다.
		request.setAttribute("num", num);		 
   		request.setAttribute("page", pageNum); 
   		request.setAttribute("board", board);   									
	}
	//선택된 글 내용 수정하기
	public void requestBoardUpdate(HttpServletRequest request){
					
		int num = Integer.parseInt(request.getParameter("num"));
		int pageNum = Integer.parseInt(request.getParameter("pageNum"));	
		
		BoardDAO dao = BoardDAO.getInstance();		
		
		BoardDTO board = new BoardDTO();		
		board.setNum(num);
		board.setName(request.getParameter("name"));
		board.setSubject(request.getParameter("subject"));
		board.setContent(request.getParameter("content"));		
		
		 java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy/MM/dd(HH:mm:ss)");
		 String regist_day = formatter.format(new java.util.Date()); 
		 
		 board.setHit(0);
		 board.setRegist_day(regist_day);
		 board.setIp(request.getRemoteAddr());			
		
		 // dao에서 게시글을 수정하는 쿼리문을 수행하는 메서드
		 dao.updateBoard(board);								
	}
	//선택된 글 삭제하기
	public void requestBoardDelete(HttpServletRequest request){
					
		int num = Integer.parseInt(request.getParameter("num"));
		int pageNum = Integer.parseInt(request.getParameter("pageNum"));	
		
		BoardDAO dao = BoardDAO.getInstance();
		
		//dao에서 게시글을 삭제하는 쿼리문을 수행하는 메서드
		dao.deleteBoard(num);							
	}	
}
