<html>
   <head>
      <meta name="layout" content="loggedout"/>
      <r:require modules="bootstrap"/>
   </head>
   <body>
   	<g:if test="${flash.message && flash.message!=""}">
   	<div class="alert alert-error"><i class="icon-exclamation-sign"></i>&nbsp;&nbsp;&nbsp;${"Error: "+flash.message }</div>
   	</g:if>
   </body>