<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
<body>
<div class="hero-unit span9">
<h3>User Management</h3>
<table class="table table-bordered table-condensed text-center" style="background:white">
  <tr class="info">
  	<td class="info" colspan="6">
  	<a href="${createLink(uri: '/user/create', absolute: true)}"><i class="icon-plus-sign" title="New User"></i></a>
  	</td>
  </tr>
  	
  <tr>
  <th>Username</th>
  <th>User Role</th>
  <th>Images</th>
  <th>Clusters</th>
  <th>Options</th>
  </tr>
 
 <g:each in="${users}" status="i" var="user">   
  <tr>
    <td>
      <small>${user.username }</small>
    </td>
    <td>
      <small>${user.userType }</small>
    </td>
    <td>
    <ul>
    <g:each in="${user.getImages()}" var="image">
      <li><small>${image.name }</small></li>
    </g:each>
    </ul>
    </td>
    <td>
    <ul>
    <g:each in="${user.getUserClusters()}" var="cluster">
      <li><small>${cluster.name }</small></li>
    </g:each>
    </ul>
    
    </td>
    <td >
    <div class="row-fluid text-center">
    <g:link action="edit" params="${[username:user.username]}"><i class="icon-pencil" title="Edit User"></i></g:link>
    <g:link action="editPerms" params="${[username:user.username]}"><i class="icon-check" title="Edit User Restrictions"></i></g:link>
    <g:link action="delete" params="${[username:user.username]}"><i class="icon-remove-sign" title="Delete User"></i></g:link>
    </div>
    </td>
  </tr>
</g:each>
</table>
</div>
</body>
