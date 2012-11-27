<!DOCTYPE html>
<%@page import="java.util.Enumeration"%>
<%@page contentType="text/html; charset=UTF-8" %>
<%
/*
javax.servlet.error.message

javax.servlet.forward.request_uri
javax.servlet.forward.servlet_path
javax.servlet.error.request_uri

javax.servlet.forward.context_path:/relay
javax.servlet.forward.path_info:/error/404.jsp
javax.servlet.error.status_code:404
javax.servlet.error.servlet_name:default
org.apache.catalina.ASYNC_SUPPORTED:false
*/
response.setStatus(404);
%>
<html>
<jsp:include page="./head.jsp" />
<body>
<div id="header">
  <h1>HTTP Status: 404 Not Found</h1>
</div>
<div id="contents">
<br><h2>
"<%= request.getAttribute("javax.servlet.error.message") %>" is not available.
</h2>
<br>
<strong>Request Attributes:</strong><br>
<%
Enumeration<String> e = request.getAttributeNames();
while(e.hasMoreElements()){
	String key = e.nextElement();
	out.write(key +":"+request.getAttribute(key)+"<br>");
}
%>
</div>

<jsp:include page="./footer.jsp" />

</body>
</html>
