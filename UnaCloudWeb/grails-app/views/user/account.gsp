<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
   <body>
   	<div class="hero-unit span9" >
   		<g:if test="${flash.message && flash.message!=""}">
   			<div class="alert alert-error"><i class="icon-exclamation-sign"></i>&nbsp;&nbsp;&nbsp;${flash.message }</div>
   		</g:if>
   		<g:form name="userCreate" class="form-horizontal" controller="user">
   			<div class="control-group">
   			<label class="control-label">Username</label>
	    		<div class="controls">
	    			<input name= "usernameTF" type="text" value="${user.username}" disabled>
	    			<input name= "username" type="hidden" value="${user.username}">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Password</label>
	    		<div class="controls">
	    			<input name= "oldPassword" type="password" >
	    		</div>
    		</div>
    		<div class="control-group">
    		<label class="control-label">New Password</label>
	    		<div class="controls">
	    			<input name= "newPassword" type="password">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Confirm Password</label>
	    		<div class="controls">
	    			<input name= "confirmPassword" type="password" >
	    		</div>
    		</div>
    		<div class="control-group">
    		<label class="control-label">API Key</label>
	    		<div class="controls">
	    			<input name= "api-key" type="text" value="${user.apiKey}">
	    		</div>
    		</div>
    		
    		<div class="control-group">
    		<label class="control-label">KeyPair</label>
	    		<div class="controls">
	    			<g:actionSubmit class="btn" action="downloadKey" value="Download Private Key"/>
	    		</div>
    		</div>
    		<div class="control-group">
    			
    			<div class="controls">
  					<g:actionSubmit class="btn" action="changePass" value="Save Password" />
  					<g:actionSubmit value="Refresh Api Key" action="refreshAPIKey" class="btn"/>
   				</div>
   			</div>
   			
   		</g:form>
   		
   	</div>
   </body>
</html>