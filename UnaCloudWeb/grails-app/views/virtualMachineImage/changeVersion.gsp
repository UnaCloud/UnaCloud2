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
   		<form id="form-change" class="form-horizontal" action="../updateFiles" enctype="multipart/form-data" >
   			<div class="control-group">
   			<label class="control-label">New Image Files</label>
	    		<div class="controls">
	    			<input id="files" name="files" type="file" multiple>
	    			<input id="id" name="id" type="hidden" value="${id}">
	    		</div>
    		</div>
    		<div class="controls">  		
  			<a id="button-submit" class="btn" style="cursor:pointer">Submit</a>	 
   			</div>
   			
   		</form>
   	</div>
   <g:javascript src="images.js" />
   <script>$(document).ready(function()	{changeUploadImage();});</script>
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
