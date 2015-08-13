<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
<body>
<div class="hero-unit span9">
<h3>External Cloud Providers Management</h3>
<table class="table table-bordered table-condensed text-center" style="background:white">
  <tr class="info">
  	<td class="info" colspan="6">
  	<a href="${createLink(uri: '/externalCloud/create', absolute: true)}"><i class="icon-plus-sign" title="New Account"></i></a>
  	</td>
  </tr>
  	
  <tr>
  <th>Provider Name</th>
  <th>Endpoint</th>
  <th>Options</th>
  </tr>
 
 <g:each in="${providers}" status="i" var="provider">   
  <tr>
   <td>
      <small>${provider.name }</small>
    </td>
    <td>
      <small>${provider.endpoint }</small>
    </td>
    <td>
    <div class="row-fluid text-center">
    <g:link action="edit" params="${[provider_id:provider.id]}"><i class="icon-pencil" title="Edit Provider Info"></i></g:link>
    <g:link action="delete" params="${[provider_id:provider.id]}"><i class="icon-remove-sign" title="Delete Provider"></i></g:link>
    </div>
    </td>
  </tr>
</g:each>
</table>
</div>
</body>
