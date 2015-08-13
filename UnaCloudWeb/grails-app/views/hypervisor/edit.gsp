<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
   <body>
   	<div class="hero-unit span9" >
   		<g:form name="hypEdit" class="form-horizontal" controller="hypervisor" action="setValues" >
   			<div class="control-group">
   			<label class="control-label" >Hypervisor Name</label>
	    		<div class="controls">
	    			<input name="name" type="text" value="${hypervisor.name}">
	    			<input name= "id" type="hidden" value="${hypervisor.getId()}">
	    		</div>
    		</div>
    		<div class="controls">
  			<g:submitButton name="hypEdit" class="btn" value="Finish" />
   			</div>
   			
   		</g:form>
   	</div>
   </body>
</html>