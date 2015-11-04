<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            All Groups
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-dashboard"></i> Home</a></li>
            <li class="active">Groups</li>
        </ol>        	         
    </section>
    <!-- Main content -->
    <section class="content">
     	<div class="row">     		     
             <div class="col-xs-12">  
             	  <g:render template="/share/message"/>            
                  <a href="${createLink(uri: '/admin/group/new', absolute: true)}" class="btn btn-primary btn-sm"><i class='fa fa-plus' ></i> New Group</a>
                  <hr>
                  <div class="box-body table-responsive">
                      <table id="unacloudTable" class="table table-bordered table-striped">
                          <thead>
                              <tr>
                                  <th>Group Name</th>
                                  <th>Members</th>
                                  <th>Actions</th>
                              </tr>
                          </thead>
                          <tbody>
                          <g:each in="${groups}" var="group"> 
                              <tr>
                                 <td>${group.visualName} <g:if test="${group.isAdmin()}"><i class='fa fa-trophy text-orange' title="Admin group"></i></g:if></td>
                                 <td style="padding:0px !important">
                                  	<table class="table insert-table">
	                                  	<tbody> 
	                                  	<g:each in="${group.users}" var="user" status="index"> 
		                                  	<tr>
		                                  	<g:if test="${index==0}"><td class="insert-row"></g:if><g:else><td></g:else>
		                                  	${user.name}</td>
		                                  	</tr>
		                                  	</g:each> 
	                                  	</tbody>
                                  	</table>
                                  </td>                                 
                                  <td class="column-center">
                                  <div class="btn-group">
	                                  <g:if test="${!group.isAdmin()&&!group.isDefault()}">
	                                  <a title="Delete" class="delete_group btn btn-primary" data-id="${group.id}" href="${createLink(uri: '/admin/group/delete/', absolute: true)}" ><i class='fa fa-trash-o' ></i></a>
	                                  </g:if>
	                                  <a title="Edit" class="btn btn-primary" href="${createLink(uri: '/admin/group/edit/'+group.id, absolute: true)}" ><i class='fa fa-users' ></i></a>
	                                  <a title="Config" class="btn btn-primary" href="${createLink(uri: '/admin/group/restrictions/'+group.id, absolute: true)}" ><i class='fa fa-gear' ></i></a>
	                              </div>
								  </td>  
                              </tr>                                                          
                          </g:each>                                   
                          </tbody>
                      </table>
                  </div><!-- /.box-body -->
             </div>
        </div>     	
	</section><!-- /.content -->    
	<asset:javascript src="pages/group.js" />  
	<script>$(document).on('ready',function(){$("#unacloudTable").dataTable();})</script> 
</body>
               