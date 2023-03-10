<%@ page contentType="text/html; charset=utf-8"%>
<%@ page import="dto.Product"%>
<%@ page import="dao.ProductRepository"%>
<%@ page errorPage="exceptionNoProductId.jsp"%>
<%@ page import="java.sql.*"%>
<!--  sql 작업 위해서 해당 클래스들 전부 임포트 -->
<html>
<head>
<link rel="stylesheet" href="./resources/css/bootstrap.min.css" />
<title>상품 상세 정보</title>
<script type="text/javascript">
	function addToCart() {
		if (confirm("상품을 장바구니에 추가하시겠습니까?")) {
			document.addForm.submit();
		} else {		
			document.addForm.reset();
		}
	}
</script>
</head>
<body>
	<jsp:include page="menu.jsp" />
	<div class="jumbotron">
		<div class="container">
			<h1 class="display-3">상품 정보</h1>
		</div>
	</div>
	<%
		String id = request.getParameter("id"); //id는 상품의 아이디
		//클래스에 등록된 샘플 상품을 사용하는 대신 디비 사용하기위해서. 주석. 
	/*	ProductRepository dao = ProductRepository.getInstance();
		Product product = dao.getProductById(id);  		*/
	%>
	
	<%@ include file="dbconn.jsp" %> 
	<div class="container">
	<div class="row">
	<!-- 화면이 변할수도 ㅇ -> 이 경우 파일 위치 변경 -->
			<%
				PreparedStatement pstmt = null; 
				/* 동적 쿼리 , 해당 sql문장을 전달할 때 이용할 객체*/
				ResultSet rs = null;
				/* 디비에서 조회한 정보들을 담을 객체 */
				String sql = "select * from product where p_id= ?"; // 쿼리문
				//? 의 위치값은 1부터 시작
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, id);
				// 동적인 파라미터가 들어갈 위치 parameterIndex
				// 조회를 할때, executeQuery()를 호출.
				rs = pstmt.executeQuery();
				// rs resultset이라는 형식의 객체에 테이블 형식으로 값을 저장
				while (rs.next()) {
			%>
		<div class="col-md-5"> <!-- c:/upload/ 원래 이미지 경로 -->
			<img src="./resources/images/<%=rs.getString("p_fileName")%>" style="width: 100%">
		</div>
			
			<div class="col-md-6">
			<h3><%=rs.getString("p_name")%></h3>
				<p><%=rs.getString("p_description")%>
				<p><b>상품 코드 : </b><span class="badge badge-danger"> <%=rs.getString("p_id")%></span>
				<p><b>제조사</b> : <%=rs.getString("p_manufacturer")%>
				<p><b>분류</b> : <%=rs.getString("p_category")%>
				<p><b>재고 수</b> : <%=rs.getString("p_unitsInStock")%>
				<h4><%=rs.getString("p_unitPrice")%>원</h4> 
				
				 <p><form name="addForm" action="./addCart.jsp?id=<%=rs.getString("p_id")%>" method="post">
					<a href="#" class="btn btn-info" onclick="addToCart()"> 상품 주문 &raquo;</a>
					<a href="./cart.jsp" class="btn btn-warning"> 장바구니 &raquo;</a> 
					<a href="./products.jsp" class="btn btn-secondary"> 상품 목록 &raquo;</a>
				</form> 
			</div>
			<%
				}
				
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			%>
		</div>
		<hr>
	</div>
	<jsp:include page="footer.jsp" />
</body>
</html>