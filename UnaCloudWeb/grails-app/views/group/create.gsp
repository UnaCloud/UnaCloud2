<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
   <body>
   	<div class="hero-unit span9" >
   		<g:form name="groupCreate" class="form-horizontal" controller="group" action="add" >
   			<div class="control-group">
   			<label class="control-label">Group Name</label>
	    		<div class="controls">
	    			<input name="name" type="text">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Users</label>
	    		<div class="controls">
	    			<g:select name="users"
          			from="${users}"
          			optionKey="username"
          			optionValue="username" 
          			multiple="multiple" />
	  			</div>
    		</div>
    		<div class="controls">
  			<g:submitButton name="createUser" class="btn" value="Create" />
   			</div>
   			
   		</g:form>
   	</div>
   </body>
</html>