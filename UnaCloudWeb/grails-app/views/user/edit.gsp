<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
   <body>
   	<div class="hero-unit span9" >
   		<g:form name="userCreate" class="form-horizontal" controller="user" action="setValues" >
   			<div class="control-group">
   			<label class="control-label" >First Name</label>
	    		<div class="controls">
	    			<input name="name" type="text" value="${user.name.split(" ")[0]}">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label" >Last Name</label>
	    		<div class="controls">
	    			<input name="lastname" type="text" value="${((user.name.split(" ").length>1))? (user.name.split(" ")[1]):""}">	
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Username</label>
	    		<div class="controls">
	    			<input name= "username" type="text" value="${user.username}">
	    			<input name= "oldUsername" type="hidden" value="${user.username}">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Password</label>
	    		<div class="controls">
	    			<input name= "password" type="password" value="${user.password}">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">User Role</label>
	    		<div class="controls">
	    			<select name= "userType">
	  				<option ${(user.userType.equals("Administrator"))?"selected":"" }>Administrator</option>
	  				<option	${(user.userType.equals("User"))?"selected":"" }>User</option>
	  				</select>
	  			</div>
    		</div>
    		<div class="controls">
  			<g:submitButton name="editUser" class="btn" value="Finish" />
   			</div>
   			
   		</g:form>
   	</div>
   </body>
</html>