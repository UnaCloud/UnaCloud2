<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
<body>
<div class="hero-unit span9">
<h3>Hypervisor Management</h3>
<table class="table table-bordered table-condensed text-center" style="background:white">
  <tr class="info">
  	<td class="info" colspan="6">
  	<a href="${createLink(uri: '/hypervisor/create', absolute: true)}"><i class="icon-plus-sign" title="New Hypervisor"></i></a>
  	</td>
  </tr>
  	
  <tr>
  <th>Hypervisor ID</th>
  <th>Hypervisor Name</th>
  <th>Options</th>
  </tr>
 
 <g:each in="${hypervisors}" status="i" var="hypervisor">   
  <tr>
    <td>
      <small>${hypervisor.id }</small>
    </td>
    <td>
      <small>${hypervisor.name }</small>
    </td>
    <td >
    <div class="row-fluid text-center">
    <g:link action="edit" params="${[id:hypervisor.id]}"><i class="icon-pencil" title="Edit Hypervisor"></i></g:link>
    <g:link action="delete" params="${[id:hypervisor.id]}"><i class="icon-remove-sign" title="Delete Hypervisor"></i></g:link>
    </div>
    </td>
  </tr>
</g:each>
</table>
</div>
</body>
