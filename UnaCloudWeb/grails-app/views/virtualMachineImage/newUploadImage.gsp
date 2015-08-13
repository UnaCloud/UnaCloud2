<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
      <link href="https://rawgithub.com/hayageek/jquery-upload-file/master/css/uploadfile.css" rel="stylesheet">
	  <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
	  <script src="https://rawgithub.com/hayageek/jquery-upload-file/master/js/jquery.uploadfile.min.js"></script>
   </head>
   <body>
   	<div class="hero-unit span9" >
   		<g:link controller="virtualMachineImage" action="index" style="display: -webkit-box;"><i class="icon-chevron-left" title="Back"></i><h5 style="margin: 3px;">Back to Image list</h5></g:link><br>
   	
   		<div id="label-message"></div>
   		<form id="form-new" class="form-horizontal" action="../virtualMachineImage/upload" enctype="multipart/form-data" >
   			<div class="control-group">
   			<label class="control-label">Image Name</label>
	    		<div class="controls">
	    			<input name="name" type="text">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Image Location</label>
	    		<div class="controls">
	    			<input id="files" name="files" type="file" multiple>
	    		</div>
    		</div>
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
    		<div class="control-group">
   			<label class="control-label">User</label>
	    		<div class="controls">
	    			<input name="user" type="text">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Password</label>
	    		<div class="controls">
	    			<input name="password" type="password">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Access Protocol</label>
	    		<div class="controls">
	    			<input name="accessProtocol" type="text">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Public</label>
	    		<div class="controls">
	    			<g:checkBox name="isPublic" value="${false}"/>
	    		</div>
    		</div>
    		<div class="controls">
    		<a id="button-submit" class="btn" style="cursor:pointer">Submit</a>	  			
   			</div>   			
   		</form>
   	</div>
   	<g:javascript src="images.js" />
   <script>    
	$(document).ready(function()
	{
		newUploadImage();
	//	$("#fileuploader").uploadFile({
		//	fileName:"myfile"
			//maxChunkSize: 1000000
//		});
	});
	</script>
   </body>   
  
</html>
