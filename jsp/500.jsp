<%@page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<%@page import="java.io.StringWriter"%>
<%@page import="java.io.PrintWriter"%>
<%
// @see http://java.sun.com/developer/technicalArticles/Servlets/servletapi2.3/
Throwable e = (Throwable)request.getAttribute("javax.servlet.error.exception");
// drop ServletException wrapping
if(e.getCause()!=null){
	e = e.getCause();
}
StringWriter sw = new StringWriter();
PrintWriter st = new PrintWriter(sw);
e.printStackTrace(st);
String message = sw.getBuffer().toString().replace("\n","<br>").replace("\t","&nbsp;&nbsp;&nbsp;&nbsp;");
response.setStatus(500);
%>
<html>
<jsp:include page="./head.jsp" />
<body>
<div id="header">
  <h1>HTTP Status:500</h1>
</div>
<div id="contents">
<br>

<%= message %>
<br>
</div>

<jsp:include page="./footer.jsp" />

</body>
</html>
