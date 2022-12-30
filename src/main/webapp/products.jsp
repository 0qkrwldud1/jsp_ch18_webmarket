<%@ page contentType="text/html; charset=utf-8"%>
<%@ page import="java.sql.*"%>
<html>
<head>
<link rel="stylesheet" href="./resources/css/bootstrap.min.css" />
<title>상품 목록</title>
</head>
<body>
	<jsp:include page="menu.jsp" />
	<div class="jumbotron">
		<div class="container">
			<h1 class="display-3">상품 목록</h1>
		</div>
	</div>
	<div class="container">
		<div class="row" align="center">
			<%@ include file="dbconn.jsp" %> <!-- 디비연결 -->
			<%
				PreparedStatement pstmt = null; 
				/* 동적 쿼리 , 해당 sql문장을 전달할 때 이용할 객체*/
				ResultSet rs = null;
				/* 디비에서 조회한 정보들을 담을 객체 */
				String sql = "select * from product"; // 쿼리문
				pstmt = conn.prepareStatement(sql);
				rs = pstmt.executeQuery();
				while (rs.next()) {
			%>
			<div class="col-md-4"> <!-- c:/upload/ 원래 이미지 경로 -->
				<img src="./resources/images/<%=rs.getString("p_fileName")%>" style="width: 100%">
				<h3><%=rs.getString("p_name")%></h3>
				<p><%=rs.getString("p_description")%>
				<p><%=rs.getString("p_UnitPrice")%>원
				<p><a href="./product.jsp?id=<%=rs.getString("p_id")%>"class="btn btn-secondary" role="button">상세 정보 &raquo;></a>
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
