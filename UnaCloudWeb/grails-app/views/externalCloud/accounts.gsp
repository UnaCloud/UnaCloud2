<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
<body>
<div class="hero-unit span9">
<h3>External Cloud Accounts Management</h3>
<table class="table table-bordered table-condensed text-center" style="background:white">
  <tr class="info">
  	<td class="info" colspan="6">
  	<a href="${createLink(uri: '/externalCloud/createAccount', absolute: true)}"><i class="icon-plus-sign" title="New Account"></i></a>
  	</td>
  </tr>
  	
  <tr>
  <th>Account Name</th>
  <th>Provider</th>
  <th>Options</th>
  </tr>
 
 <g:each in="${accountList}" status="i" var="account">   
  <tr>
    <td>
      <small>${account.name }</small>
    </td>
    <td>
      <small>${account.provider.name }</small>
    </td>
   <td>
    <div class="row-fluid text-center">
    <g:link action="editAccount" params="${[account_id:account.id]}"><i class="icon-pencil" title="Edit Account Info"></i></g:link>
    <g:link action="deleteAccount" params="${[account_id:account.id]}"><i class="icon-remove-sign" title="Delete Account"></i></g:link>
    </div>
    </td>
  </tr>
</g:each>
</table>
</div>
</body>
