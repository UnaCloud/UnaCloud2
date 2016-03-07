<html>
   <head>
      <meta name="layout" content="loggedout"/>
   </head>
   <body>   
  	 <div class="main">	
   		<div class="login">	    
		    <i class="fa fa-cloud"></i>
		    <h1 class="title">UnaCloud</h1>
		    <h6 ><em>Desktop Cloud</em></h6>	
		    <g:if test="${flash.message && flash.message!=""}">
		   	<div class="alert alert-danger"><i class="icon-exclamation-sign"></i>&nbsp;&nbsp;&nbsp;${flash.message }</div>
		   	</g:if> 	   	
		    <form method="POST" action="${createLink(uri: '/user/login', absolute: true)}">	    
		    <div class="box">
		        <input class="text-una" name="username" type="text" placeholder="username">
			    <input class="text-una" name="password" type="password" placeholder="password">		    
			    <g:submitButton class="btn btn-default full-width" name="login" value="Sign in"/>
		    </div>
		    </form>	    
	    </div>
	    
	 </div>
   </body>
</html>