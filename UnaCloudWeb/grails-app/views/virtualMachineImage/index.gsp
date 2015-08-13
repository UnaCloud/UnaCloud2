<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
<body>

<div class="hero-unit span9">
<h3>My Images</h3>
<table class="table table-bordered table-condensed text-center" style="background:white">
  <tr class="info">
  	<td class="info" colspan="6">
  	<a href="${createLink(uri:"/virtualMachineImage/newImage", absolute: true)}"><i class="icon-plus-sign" title="New Image"></i></a>
  	</td>
  </tr>
  	
  <tr>
  <th>Image Name</th>
  <th>Options</th>
  </tr>
 
 <g:each in="${images}" status="i" var="image">   
  <tr>
    <td>
      <small>${image.name}</small>
    </td>
    <td >
    <div class="row-fluid text-center">
    <g:link action="edit" params="${[id: image.id]}"><i class="icon-pencil" title="Edit Image"></i></g:link>
    <a title="Delete Image" class="deleteImages" data-id="${image.id}" style="cursor:pointer"><span class="icon-remove-sign"></span></a>  		  	
	<g:link action="changeVersion" params="${[id: image.id]}"><i class="icon-repeat" title="Change Version"></i></g:link>
    <a title="Clear this image from cache" class="clearImageFromCache" data-id="${image.id}" style="cursor:pointer"><span class="icon-fire"></span></a>  		  	
	<g:link action="addExternalId" params="${[id: image.id]}"><i class="icon-globe" title="Add/Modify external account id"></i></g:link>
    </div>
    </td>
  </tr>
</g:each>
</table>
</div>
<g:javascript src="images.js" />
<script>$(document).ready(function(){loadImages();});</script>
</body>
