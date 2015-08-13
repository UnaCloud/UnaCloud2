<html>
   <head>
      <meta name="layout" content="loggedout"/>
      <r:require modules="bootstrap"/>
   </head>
   <body>
   <div class="hero-unit" align="center">
   	<g:form controller="user" action="login">
   		<table>
   			<tr>
   			<td><p><small>Username:&nbsp;&nbsp;&nbsp;</small></p></td>   
	    	<td><input name="username" type="text"></td>	 
    		</tr>
   			<tr>
   			<td><p><small>Password:&nbsp;&nbsp;&nbsp;</small></p></td>
   			<td><input name="password" type="password"></td>
	    	</tr>
    		<tr>
    		<td></td>
  			<td><g:submitButton name="login" class="btn" value="Submit" />
   			</td>
   		</table>	
   	</g:form>
   	</div>
   	<g:if test="${flash.message && flash.message!=""}">
   	<div class="alert alert-error"><i class="icon-exclamation-sign"></i>&nbsp;&nbsp;&nbsp;${flash.message }</div>
   	</g:if>
   </body>