<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
   <body>
   	<div class="hero-unit span9" >
   		<g:form name="osCreate" class="form-horizontal" controller="operatingSystem" action="add" >
   			<div class="control-group">
   			<label class="control-label">Name</label>
	    		<div class="controls">
	    			<input name="name" type="text">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Configurer Class</label>
	    		<div class="controls">
	    			<input name="configurer" type="text">	
	    		</div>
    		</div>
    		
    		<div class="controls">
  			<g:submitButton name="createOS" class="btn" value="Create" />
   			</div>
   			
   		</g:form>
   	</div>
   </body>
</html>