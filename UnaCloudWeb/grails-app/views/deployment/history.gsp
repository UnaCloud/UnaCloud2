<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
<body>

<div class="hero-unit span9">
<g:link controller="cluster" action="index" style="display: -webkit-box;"><i class="icon-chevron-left" title="Back"></i><h5 style="margin: 3px;">Back to cluster list</h5></g:link>
    
<table class="table table-bordered table-condensed text-center" style="background:white">
  <g:if test="${session.user.userType == 'Administrator'}">
  <tr class="info">
  	<td class="info" colspan="7">
  	<input type="checkbox" name="View All" ><small>&nbsp;View All</small>
  	</td>
  </tr>
  </g:if>
  
  <tr>
  <th>Cluster</th>
  <th>Images</th>
  <th>Instances</th>
  <th>Start Time</th>
  <th>Stop Time</th>
  </tr>
 
 <g:each in="${deployments}" status="i" var="deployment">   
  <tr>
    <td rowspan="${deployment.cluster.images.size()}">
      <small><g:if test="${deployment.cluster.cluster!=null }">
      ${deployment.cluster.cluster.name}
      </g:if>
      <g:if test="${deployment.cluster.cluster==null }">
      None/Deleted
      </g:if>
      </small>
    </td>
    
    <g:each in="${deployment.cluster.images}" status="j" var="image">
   		
    <td>
   	<small><g:if test="${image.image!=null }">
      ${image.image.name}
      </g:if>
      <g:if test="${ image.image==null }">
      Deleted
      </g:if></small>		    
    </td>
    <td>
    <small>${image.virtualMachines.size() }</small>
    </td>
    <td>
    <small>${deployment.startTime }</small>
    </td>
    <td>
    <small>${deployment.stopTime }</small>
    </td>
    </tr>
  	<tr>
    
    </g:each>
  
  </tr>
</g:each>
</table>
</div>
</body>
