<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
   <body>
   	<div class="hero-unit span9" >
   		<g:form name="osEdit" class="form-horizontal" controller="operatingSystem" action="setValues" >
   			<div class="control-group">
   			<label class="control-label" >OS Name</label>
	    		<div class="controls">
	    			<input name="name" type="text" value="${os.name}">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label" >Configurer Class</label>
	    		<div class="controls">
	    			<input name="configurer" type="text" value="${os.configurer}">	
	    			<input name= "id" type="hidden" value="${os.getId()}">
	    		</div>
    		</div>
    		<div class="controls">
  			<g:submitButton name="osEdit" class="btn" value="Finish" />
   			</div>
   			
   		</g:form>
   	</div>
   </body>
</html>