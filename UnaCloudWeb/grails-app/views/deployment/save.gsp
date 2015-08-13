<html>
   <head>
      <meta name="layout" content="main"/>    
      <r:require modules="bootstrap"/>
   </head>
	<body>
		<div class="hero-unit span9">
		<g:form class="form-horizontal" controller="deployment" action="saveImage" enctype="multipart/form-data">
			<!-- Content -->
			<h4>Description of Image that will be saved</h4>
			<input name="name" type="text" value="${imageName}">
			<input name="image" type="hidden" value="${imageId}">
			<input name="machine" type="hidden" value="${machineId}">
			<g:submitButton name="saveImage" class="btn" value="Save" />
		</g:form>
		</div>
	</body>
</html>