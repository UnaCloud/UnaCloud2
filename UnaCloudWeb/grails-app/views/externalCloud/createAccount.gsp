<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
   <body>
   	<div class="hero-unit span9" >
   		<g:form name="extCloudAccountCreate" class="form-horizontal" controller="externalCloud" action="addAccount" >
   			<div class="control-group">
   			<label class="control-label">Name</label>
	    		<div class="controls">
	    			<input name="name" type="text">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Provider</label>
	    		<div class="controls">
	    			<select name= "provider">
	  				<g:each in="${providers}" status="i" var="p">
	  					<option value="${p.id}">${p.name }</option>
	  				</g:each>
	  				</select>
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Account Access ID</label>
	    		<div class="controls">
	    			<input name="account_id" type="password">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Account Access Key</label>
	    		<div class="controls">
	    			<input name="account_key" type="password">
	    		</div>
    		</div>
    		<div class="controls">
  			<g:submitButton name="createAccount" class="btn" value="Create" />
   			</div>
   			
   		</g:form>
   	</div>
   </body>
</html>