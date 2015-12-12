<%@page import="unacloud.enums.UserStateEnum"%>
<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            All Users
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li class="active">Users</li>
        </ol>        	         
    </section>
    <!-- Main content -->
    <section class="content">
     	<div class="row">     		     
             <div class="col-xs-12">  
             	  <g:render template="/share/message"/>            
                  <a href="${createLink(uri: '/admin/user/new', absolute: true)}" class="btn btn-primary btn-sm"><i class='fa fa-plus' ></i> New User</a>
                  <hr>
                  <div class="box box-solid">
                  <div class="box-body table-responsive">
                      <table id="unacloudTable" class="table table-bordered table-striped">
                          <thead>
                              <tr>
                                  <th>User</th>
                                  <th>Username</th>
                                  <th>Status</th>
                                  <th>Images</th>
                                  <th>Clusters</th>
                                  <th>Actions</th>
                              </tr>
                          </thead>
                          <tbody>
                          <g:each in="${users}" var="user"> 
                              <tr>
                                 <td>${user.name} <g:if test="${user.isAdmin()}"><i class='fa fa-trophy text-orange' title="Admin user"></i></g:if></td>
                                 <td>${user.username}</td>
                                 <td>
	                                  <g:if test="${user.status.equals(UserStateEnum.AVAILABLE)}">
	                                 	<span class="label label-success">${user.status.name}</span>
	                                  </g:if>
	                                  <g:elseif test="${user.status.equals(UserStateEnum.DISABLE)}">
	                                  	<span class="label label-default">${user.status.name}</span>
	                                  </g:elseif>
	                                  <g:elseif test="${user.status.equals(UserStateEnum.BLOCKED)}">
	                                  	<span class="label label-danger">${user.status.name}</span>
	                                  </g:elseif>                                 
                                  </td>
                                  <td style="padding:0px !important">
                                  	<table class="table insert-table">
	                                  	<tbody> 
	                                  	<g:each in="${user.getOrderedImages()}" var="image" status="index"> 
		                                  	<tr>
		                                  	<g:if test="${index==0}"><td class="insert-row"></g:if><g:else><td></g:else>
		                                  	${image.name}</td>
		                                  	</tr>
		                                  	</g:each> 
	                                  	</tbody>
                                  	</table>
                                  </td>
                                  <td style="padding:0px !important">
                                  	<table class="table insert-table">
	                                  	<tbody> 
	                                  	<g:each in="${user.getOrderedClusters()}" var="cluster" status="index"> 
		                                  	<tr>
		                                  	<g:if test="${index==0}"><td class="insert-row"></g:if><g:else><td></g:else>
		                                  	${cluster.name}</td>
		                                  	</tr>
		                                  	</g:each> 
	                                  	</tbody>
                                  	</table></td>
                                  <td class="column-center">
                                  <div class="btn-group">
                                  <g:if test="${!user.status.equals(UserStateEnum.BLOCKED)}">
                                  	<g:if test="${session.user.id!=user.id}">
	                                  <a title="Delete" class="delete_user btn btn-default" data-id="${user.id}" href="${createLink(uri: '/admin/user/delete/', absolute: true)}" ><i class='fa fa-trash-o' ></i></a>
	                                  <a title="Edit" class="btn btn-default" href="${createLink(uri: '/admin/user/edit/'+user.id, absolute: true)}" ><i class='fa fa-pencil-square' ></i></a>
	                                </g:if>
	                                <a title="Config" class="btn btn-default" href="${createLink(uri: '/admin/user/restrictions/'+user.id, absolute: true)}" ><i class='fa fa-gear' ></i></a>
	                              </g:if>
	                              </div>
								  </td>  
                              </tr>                                                          
                          </g:each>                                   
                          </tbody>
                      </table>
                  </div><!-- /.box-body -->
                  </div><!-- /.box-->
             </div>
        </div>     	
	</section><!-- /.content -->    
</body>
               