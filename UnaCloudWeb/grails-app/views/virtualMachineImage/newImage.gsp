<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
<body>
<div class="span9">
	<div class="hero-unit">
        <h3 class="text-center">New Image </h3>
        <br>
        <div class="row-fluid text-center">
        	<div class="span4">
        		<g:link controller="virtualMachineImage" action="newUploadImage"><g:img file="image.png"/></g:link>
        		<p>Upload New Image</p>
        	</div>
        	<div class="span4"></div>
        	<div class="span4">
        		<g:link controller="virtualMachineImage" action="newPublicImage"><g:img file="cluster.png"/></g:link>
        		<p>Use Public Image</p>
        	</div>
            
        </div>
        	
        </div>	
        	
          
</div>

    
</body>
</html>