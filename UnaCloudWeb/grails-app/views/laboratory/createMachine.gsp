<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
   <body>   
   	<div class="hero-unit span9" >
   		<g:if test="${flash.message && flash.message!=""}">
	   		<div class="alert alert-error" ><i class="icon-exclamation-sign"></i><span style="font-size:14px">&nbsp;&nbsp;&nbsp;${flash.message }</span></div>
	   	</g:if>
   		<g:form name="machineCreate" class="form-horizontal" controller="laboratory" action="addMachine" >
   			<div class="control-group">
   			<label class="control-label">Physical Machine Name</label>
	    		<div class="controls">
	    			<input name="name" type="text">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Physical Machine IP</label>
	    		<div class="controls">
	    			<input name="ip" type="text">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Physical Machine MAC</label>
	    		<div class="controls">
	    			<input name="mac" type="text">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">CPU Cores</label>
	    		<div class="controls">
	    			<input name="cores" type="text">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">RAM Memory (MB)</label>
	    		<div class="controls">
	    			<input name="ram" type="text">
	    		</div>
    		</div>
    		<!--<div class="control-group">
   			<label class="control-label">Hard Disk Size</label>
	    		<div class="controls">
	    			<input name="disk" type="text">
	    		</div>
    		</div>-->
    		<div class="control-group">
   			<label class="control-label">Operating System</label>
	    		<div class="controls">
	    			<select name= "osId">
	  				<g:each in="${oss}" status="i" var="os">
	  					<option value="${os.id}">${os.name }</option>
	  				</g:each>
	  				</select>
	    		</div>
    		</div>
    		<input name="labId" type="hidden" value="${lab.id }">
    		<div class="controls">
  			<g:submitButton name="createUser" class="btn" value="Create" />
   			</div>
   			
   		</g:form>
   	</div>
   </body>
</html>