<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
   <body>
   	<div class="hero-unit span9" >
   		<g:form name="hypCreate" class="form-horizontal" controller="hypervisor" action="add" >
   			<div class="control-group">
   			<label class="control-label">Hypervisor Name</label>
	    		<div class="controls">
	    			<input name="name" type="text">
	    		</div>
    		</div>
    		
    		<div class="control-group">
   			<label class="control-label">Hypervisor Version</label>
	    		<div class="controls">
	    			<input name="hyperVersion" type="text">
	    		</div>
    		</div>
    		
    		<div class="controls">
  			<g:submitButton name="createHyp" class="btn" value="Create" />
   			</div>
   			
   		</g:form>
   	</div>
   </body>
</html>