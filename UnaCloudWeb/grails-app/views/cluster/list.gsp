<%@page import="unacloud.enums.ClusterEnum"%>
<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            My Clusters
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-dashboard"></i> Home</a></li>
            <li class="active">Clusters</li>
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
                  <a href="${createLink(uri: '/services/cluster/new', absolute: true)}" class="btn btn-primary btn-sm"><i class='fa fa-plus' ></i> New</a>
                  <hr>
                  <div class="box-body table-responsive">
                      <table id="unacloudTable" class="table table-bordered table-striped">
                          <thead>
                              <tr>
                                  <th>Cluster Name</th>
                                  <th>State</th>
                                  <th>Images</th>
                                  <th>Actions</th>
                              </tr>
                          </thead>
                          <tbody>
                          <g:each in="${clusters}" var="cluster"> 
                              <tr>
                                 <td>${cluster.name}</td>
                                 <td>
	                                  <g:if test="${cluster.state.equals(ClusterEnum.AVAILABLE)}">
	                                 	<span class="label label-success">${cluster.state.name}</span>
	                                  </g:if>
	                                  <g:elseif test="${cluster.state.equals(ClusterEnum.FREEZE)}">
	                                  	<span class="label label-primary">${cluster.state.name}</span>
	                                  </g:elseif>
	                                  <g:elseif test="${cluster.state.equals(ClusterEnum.DISABLE)}">
	                                  	<span class="label label-default">${cluster.state.name}</span>
	                                  </g:elseif>
	                                  <g:elseif test="${cluster.state.equals(ClusterEnum.UNAVAILABLE)}">
	                                  	<span class="label label-danger">${cluster.state.name}</span>
	                                  </g:elseif>	                                  
                                  </td>
                                  <td style="padding:0px !important">
                                  	<table class="table table-striped insert-table">
	                                  	<tbody>
		                                  	<g:each in="${cluster.getOrderedImages()}" var="image"> 
		                                  	<tr><td>${image.name}</td></tr>
		                                  	</g:each>  
	                                  	</tbody>
                                  	</table>
                                  </td>
                                  <td class="column-center">
                                  <div class="btn-group">
	                                  <g:if test="${!cluster.state.equals(ClusterEnum.FREEZE)}">
	                                  <a title="Delete" class="delete_cluster btn btn-primary" data-id="${cluster.id}" href="${createLink(uri: '/services/cluster/delete/', absolute: true)}" ><i class='fa fa-trash-o' ></i></a>
	                                  <a title="Deploy Cluster" class="btn btn-primary" href="${createLink(uri: '/services/cluster/deploy/'+cluster.id, absolute: true)}" ><i class='fa fa-play' ></i></a>
	                                  <a title="Deploy Cluster External Provider" class="btn btn-primary" href="${createLink(uri: '/services/cluster/external/'+cluster.id, absolute: true)}" ><i class='fa fa-cloud-upload' ></i></a>
	                                  </g:if>
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
	<script>$(document).on('ready',function(){$("#unacloudTable").dataTable();loadCluster();})</script> 
</body>
               