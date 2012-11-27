<!-- $Id: stats.jsp,v 1.11 2005/05/27 20:30:25 yuk Exp $ -->
<!doctype html public "-//w3c//dtd html 4.0 transitional//en"
   "http://www.w3.org/tr/rec-html40/loose.dtd">
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%-- <%@ page import="com.lavans.util.*" %> --%>
<%@ page import="com.lavans.lacoder.sql.stats.*" %>
<%!
	private static String METHOD_EXCLUDE_PACKAGE="com.lavans.util";

	private static String COMMAND ="cmd";
	private static String C_LIST  ="list";

	private static String SORT ="sort";
	private static String SORT_TOTAL="total";
	private static String SORT_AVERAGE="ave";
%>
<%
	Statistics stat = Statistics.getInstance();

	// 繧ｽ繝ｼ繝�
	String sort = request.getParameter(SORT);
	if(sort==null) sort=SORT_AVERAGE;

	// 繧ｳ繝槭Φ繝�
	String command = request.getParameter(COMMAND);
	if(command==null) command=C_LIST;

	NumberFormat nf  = NumberFormat.getInstance();
	DecimalFormat df = new DecimalFormat("#,###.00");
%>
<html lang="ja">
<head>
<title>邨ｱ險域ュ蝣ｱ</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style type="text/css">
  body{
    color: #000000;
    background-color: #fffafa;
    font-size: 12px;
  }

a:link {
	text-decoration: none;
}

a:visited {
	text-decoration: none;
}

a:hover {
	color: #FFFFFF;
	background-color: #000066;
}

  .txt{
    font-size: 12px;
  }

  div.center{
  	text-align: center
  }

  tr.title{
    color: #ffffff;
    background-color: #000066;
    font-size: 14px;
	font-weight: bold;
	text-align: middle;
  }

  td.sql{
    color: #000066;
    background-color: #f5f9ff;
    font-size: 12px;
  }

  td.method{
    color: #991000;
    background-color: #FDFDFF;
    font-size: 12px;
  }
  .hilight{
    color: #FF0000;
  }

  .white{
    color: #ffffff;
  }
  .gray{
    color: #999999;
  }
</style>
<script language="javascript">
function openSQL(sql){
  w = window.open("","_new","width=500,height=300,scrollbars=yes,resizable=yes,status=no,menbar=no");
  w.document.open("text/html","replace");
  w.document.write("<font face=\"�ｭ�ｳ 繧ｴ繧ｷ繝�け\">");
  w.document.write(sql);
  w.document.write("</font>");
  w.document.close();
  w.focus();
 }
</script>
</head>
<body>

<table border=1 cellspacing=0 cellpadding=3 width="100%">
<tr class="title">
  <th>蜻ｼ蜃ｺ蜈�/th>
  <th rowspan="2" width="30">螳溯｡�br>蝗樊焚</th>
<%
	if(sort.equals(SORT_TOTAL)){
%>
  <th rowspan="2">邱丞ｮ溯｡梧凾髢�br>(msec)<font class="gray">笆ｼ</font></th>
  <th rowspan="2">蟷ｳ蝮�ｮ溯｡梧凾髢�br>(msec)<a href="stats.jsp?<%= SORT +"="+ SORT_AVERAGE %>" class="white">笆ｼ</a></th>
<%
	}else{
%>
  <th rowspan="2">邱丞ｮ溯｡梧凾髢�br>(msec)<a href="stats.jsp?<%= SORT +"="+ SORT_TOTAL %>" class="white">笆ｼ</a></th>
  <th rowspan="2">蟷ｳ蝮�ｮ溯｡梧凾髢�br>(msec)<font class="gray">笆ｼ</font></th>
<%
	}
%>

</tr>
<tr class="title">
  <th>SQL</th>
</tr>
<%
	List list = null;
	if(sort.equals(SORT_TOTAL)){
		list = stat.getStatInfoByTotal();
	}else{
		list = stat.getStatInfoByAverage();
	}
	for(int i=0; i<list.size(); i++){
		StatsRecord rec = (StatsRecord)list.get(i);
		// 蜻ｼ蜃ｺ蜈�ｸ�ｦｧ
  		StringBuffer buf = new StringBuffer();
  		Object methods[] = rec.getMethodNames().toArray();
		for(int j=0; j<methods.length; j++){
			buf.append(methods[j] + "<br>");
		}
%>
<tr>
<!--  <td class='method'><%= buf.substring(METHOD_EXCLUDE_PACKAGE.length()) %></td>
-->
  <td class='method'><%= buf.toString() %></td>
  <td rowspan="2" align="right"><%= nf.format(rec.getCallCount()) %></td>
  <td rowspan="2" align="right"><%= nf.format(rec.getTotalCostTime()) %></td>
  <td rowspan="2" align="right"><%= df.format(rec.getAverage()) %></td>
</tr>
<tr>
  <td class='sql'>
<%
		if(rec.getSql().length()<200){
			out.print(rec.getSql());
		}else{
%>
<script> var sql<%= i %>="<%= rec.getSql().replaceAll("\n","<br>").replaceAll(" "," ") %>"; </script>
  <%= rec.getSql().substring(0,300) %><a href="javascript:openSQL(sql<%= i %>)">.....</a>
<%
		}
%>
</td>
</tr>
<%
	}
%>
</table>
<%= Statistics.getInstance().viewConnectionPool() %>
<%
/*
    Enumeration e = DriverManager.getDrivers();
    while(e.hasMoreElements()){
            Driver drivera = (Driver)e.nextElement();
            %><%= drivera.toString() %><br>\n<%
    }
*/
%>
</body>
</html>