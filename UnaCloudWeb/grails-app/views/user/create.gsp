<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
   <body>
   	<div class="hero-unit span9" >
   		<g:form name="userCreate" class="form-horizontal" controller="user" action="add" >
   			<div class="control-group">
   			<label class="control-label">First Name</label>
	    		<div class="controls">
	    			<input name="name" type="text">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Last Name</label>
	    		<div class="controls">
	    			<input name="lastname" type="text">	
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Username</label>
	    		<div class="controls">
	    			<input name= "username" type="text">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Password</label>
	    		<div class="controls">
	    			<input name= "password" type="password">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">User Role</label>
	    		<div class="controls">
	    			<select name= "userType">
	  				<option>Administrator</option>
	  				<option>User</option>
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