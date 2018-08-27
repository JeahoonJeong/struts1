<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%	//POST 방식에서 한글깨짐을 방지
	request.setCharacterEncoding("UTF-8");
	String cp = request.getContextPath();
%>
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body style="font-size: 12pt;">
	<table width="500" align="center">
		<tr height="30">
			<td align="right">
			<input type="button" value="파일 올리기" onclick="javascript:location.href='<%=cp%>/fileTest.do?method=write'">
			</td>
		</tr>
	</table>
	<table width="500" align="center" border="1" style="font-size: 12pt;">
		<tr height="30">
			<td width="50">번호</td>
			<td width="200">제목</td>
			<td width="200">파일</td>
			<td width="50">삭제</td>
		</tr>
		<c:forEach var="dto" items="${lists }">
		<tr onmouseover="this.style.backgroundColor='#e4e4e4'"
			onmouseout="this.style.backgroundColor=''" bgcolor="#ffffff">
			<td width="50">${dto.listNum }</td>
			<td width="200">${dto.subject }</td>
			<td width="200">
				<a href="${dto.urlFile }">${dto.originalFileName }</a>
			<!-- 
			<form action="<%=cp%>/fileTest.do?method=download" method="post">
				<input type="hidden" name="originalFileName" value="${dto.originalFileName }"/>
				<input type="hidden" name="saveFileName" value="${dto.saveFileName }"/>
				<input type="submit" value="${dto.originalFileName }">
			</form> 
			-->
			</td>
			<td width="50">
				<a href="<%=cp%>/fileTest.do?method=delete&num=${dto.num}&saveFileName=${dto.saveFileName}">삭제</a>
			</td>
		</tr>
		</c:forEach>
		
	
	<c:if test="${totalDataCount==0 }">
		<tr bgcolor="#ffffff">
			<td>등록된 자료가 없습니다.</td>
		</tr>
	</c:if>
	</table>
	
	<c:if test="${totalDataCount!=0 }">
		<table width="600" cellspacing="3" border="0" cellpadding="0" align="center">
		<tr  align="center">
			<td align="center" height="30">${pageIndexList }</td>
		</tr>
		</table>
	</c:if>
	
</body>
</html>