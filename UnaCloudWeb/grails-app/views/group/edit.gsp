<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
   <body>
   	<div class="hero-unit span9" >
   		<g:form name="groupCreate" class="form-horizontal" controller="group" action="setValues" >
   			<div class="control-group">
   			<label class="control-label">Group Name</label>
	    		<div class="controls">
	    			<input name="name" type="text" value="${group.name}">
	    			<input name="oldName" type="hidden" value="${group.name}">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Users</label>
	    		<div class="controls">
	    			<g:select name="users"
          			from="${users}"
          			optionKey="username"
          			value="${group.users}"
          			optionValue="username" 
          			multiple="multiple" />
	  			</div>
    		</div>
    		<div class="controls">
  			<g:submitButton name="editGroup" class="btn" value="Finish" />
   			</div>
   		</g:form>
   	</div>
   </body>
</html>