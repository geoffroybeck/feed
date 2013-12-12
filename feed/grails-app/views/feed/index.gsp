<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta name="layout" content="main"/>
<title></title>
</head>
<body>
  <div class="body">
  <ul style="margin-left:30px;">
  	<g:each in = "${results}" var = "result" >
	  	<li>
	  		<div style="font-weight:bold;font-size:18px;">
	  		${result.key}
	  		</div> 
	  		${result.value.class }
	  		${(result.value.size()>40?result.value[0..39]:result.value)}
	  	</li>
  	</g:each>
  	</ul>
  </div>
</body>
</html>