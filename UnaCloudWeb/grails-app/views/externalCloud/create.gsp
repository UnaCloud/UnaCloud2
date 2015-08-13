<%@page import="unacloud2.ExternalCloudTypeEnum"%>
<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
   <body>
   	<div class="hero-unit span9" >
   		<g:form name="extCloudAccountCreate" class="form-horizontal" controller="externalCloud" action="add" >
   			<div class="control-group">
   			<label class="control-label">Name</label>
	    		<div class="controls">
	    			<input name="name" type="text">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Endpoint</label>
	    		<div class="controls">
	    			<input name="endpoint" type="text">	
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Type of provided service</label>
	    		<div class="controls">
	    		<select name="type">
	    		<g:each in="${ExternalCloudTypeEnum.values()}" status="i" var="type">
	  					<option value="${type}">${type.getName() }</option>
	  			</g:each>
	  			</select>
	  			</div>
    		</div>
    		<div class="controls">
  			<g:submitButton name="createUser" class="btn" value="Create" />
   			</div>
   			
   		</g:form>
   	</div>
   </body>
</html>