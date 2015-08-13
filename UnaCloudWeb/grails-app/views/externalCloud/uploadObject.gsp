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
   		<g:form class="form-horizontal" controller="externalCloud" action="upload" enctype="multipart/form-data">
    		<div class="control-group">
   			<label class="control-label">File Location</label>
	    		<div class="controls">
	    			<input id="file" name="file" type="file">
	    		</div>
    		</div>
    		<div class="controls">
  			<g:submitButton name="uploadFile" class="btn" value="Upload" />
   			</div>
   			
   		</g:form>
   	</div>
   	
   </body>   
   <script>
	$(document).ready(function()
	{
		$("#fileuploader").uploadFile({
			fileName:"myfile"
			maxChunkSize: 1000000
		});
	});
	</script>
</html>
