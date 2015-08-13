<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
   <body>
   	<div class="hero-unit span9" >
   	<g:link controller="virtualMachineImage" action="index" style="display: -webkit-box;"><i class="icon-chevron-left" title="Back"></i><h5 style="margin: 3px;">Back to Image list</h5></g:link><br>
   	
   	<div id="label-message"></div>
   		
   		<form id="form-create" name="imageNewPublic" class="form-horizontal" controller="virtualMachineImage" action="newPublic" >
   			<div class="control-group">
   			<label class="control-label">Image Name</label>
	    		<div class="controls">
	    			<input name="name" type="text">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Public Image</label>
	    		<div class="controls">
	    			<select name= "pImage" onchange="refreshFields(this.value)">
	  				<option value="0">--Select a Public Image--</option>
	    			
	  				<g:each in="${pImages}" status="i" var="pImage">
	  					<option value="${pImage.id}">${pImage.name}</option>
	  				</g:each>
	  				</select>
	    		</div>
    		</div>
    		<div id="info"></div>	
    		<div class="controls">
  			<a id="button-submit" class="btn" style="cursor:pointer">Submit</a>	
   			</div>
   			
   		</form>
   	</div>
   		<script>
            function refreshFields(value) {
                <g:remoteFunction controller="virtualMachineImage" action="refreshInfo" update="info" params="'selectedValue='+value"/>
                
            }
        </script>
   <g:javascript src="images.js" />
   <script>$(document).ready(function()	{createPublicImage();});</script>
   </body>
</html>