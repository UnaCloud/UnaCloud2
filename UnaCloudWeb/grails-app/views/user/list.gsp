<%@page import="unacloud.enums.UserStateEnum"%>
<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            All users
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-dashboard"></i> Home</a></li>
            <li class="active">Users</li>
        </ol>        	         
    </section>
    <!-- Main content -->
    <section class="content">
     	<div class="row">     		     
             <div class="col-xs-12">  
             	<g:if test="${flash.message && flash.message!=""}">
	          		<g:if test="${flash.type=="success"}">
	          			<div class="alert alert-success"><i class="fa fa-check"></i>
	          		</g:if>  
	          		<g:elseif test="${flash.type=="info"}">
	          			<div class="alert alert-info"><i class="fa fa-info"></i>
	          		</g:elseif> 
	          		<g:elseif test="${flash.type=="warning"}">
	          			<div class="alert alert-warning"><i class="fa fa-warning"></i>
	          		</g:elseif>             			
	          		<g:else>
	          			<div class="alert alert-danger"><i class="fa fa-ban"></i>
	          		</g:else> 	
			   		&nbsp;&nbsp;&nbsp;${flash.message}</div>
				</g:if>                         
                  <a href="${createLink(uri: '/admin/user/new', absolute: true)}" class="btn btn-primary btn-sm"><i class='fa fa-plus' ></i> New User</a>
                  <hr>
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
	                                  <g:elseif test="${user.state.equals(UserStateEnum.DISABLE)}">
	                                  	<span class="label label-default">${user.status.name}</span>
	                                  </g:elseif>
	                                  <g:elseif test="${user.state.equals(UserStateEnum.BLOCKED)}">
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
                                  	<g:if test="${session.user.id!=user.id}">
	                                  <a title="Delete" class="delete_cluster btn btn-primary" data-id="${user.id}" href="${createLink(uri: '/services/cluster/delete/', absolute: true)}" ><i class='fa fa-trash-o' ></i></a>
	                                  <a title="Edit" class="btn btn-primary" href="${createLink(uri: '/services/cluster/deploy/'+user.id, absolute: true)}" ><i class='fa fa-pencil-square' ></i></a>
	                                </g:if>
	                                <a title="Config" class="btn btn-primary" href="${createLink(uri: '/services/cluster/external/'+user.id, absolute: true)}" ><i class='fa fa-gear' ></i></a>
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
	<asset:javascript src="pages/cluster.js" />  
	<script>$(document).on('ready',function(){$("#unacloudTable").dataTable();})</script> 
</body>
               