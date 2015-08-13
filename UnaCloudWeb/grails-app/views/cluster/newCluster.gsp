<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
   <body>
   	<div class="hero-unit span9" >
   	    <g:link controller="cluster" action="index" style="display: -webkit-box;"><i class="icon-chevron-left" title="Back"></i><h5 style="margin: 3px;">Back to Cluster list</h5></g:link><br>
   	
   	    <div id="label-message"></div>
   		<form id="form-new" class="form-horizontal" action="save" >
   			<div class="control-group">
   			<label class="control-label">Cluster Name</label>
	    		<div class="controls">
	    			<input name="name" type="text">
	    		</div>
    		</div>
    		<div class="control-group">
   			<label class="control-label">Images</label>
	    		<div class="controls">
	    			<select name= "images" multiple="multiple">
	  				<g:each in="${images}" status="i" var="image">
	  					<option value="${image.id}">${image.name}</option>
	  				</g:each>
	  				</select>
	    		</div>
    		</div>
    		<div class="controls">  			
  			<a id="button-create" class="btn" style="cursor:pointer">Create</a>	  		
   			</div>   			
   		</form>
   	</div>
   <g:javascript src="cluster.js" />
   <script>$(document).ready(function()	{createCluster();});</script>
   </body>
</html>