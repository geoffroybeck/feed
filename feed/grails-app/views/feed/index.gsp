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
	  			${(result.value.ret.size()>100?result.value.ret[0..99]:result.value.ret)}<br>
	  		<ul>
		  		<g:each in="${result.value.environ }" var ="enventry">
		  			<li>${enventry.key } : ${enventry.value }</li>
		  		</g:each>
	  		</ul>
	  	</li>
  	</g:each>
  	</ul>
  </div>
</body>
</html>