<%@page import="unacloud.enums.VirtualMachineExecutionStateEnum"%>
<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            Deployment list
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li class="active">Deployments</li>
        </ol>        	         
    </section>
    <!-- Main content -->
    <section class="content">
     	 <div class="row">     		     
         	<div class="col-xs-12">  
            	<g:render template="/share/message"/> 
             	<form method="post" id="form_deployments">
             		<div class="box box-primary"> 
             			<div class="box-header">
                           <h3 class="box-title">My deployments</h3>
                        </div><!-- /.box-header -->             			
             			<div class="box-body"> 		           	 	 
                        	<div class=" table-responsive">
			                     <table id="unacloudTable" class="table table-bordered table-striped">
			                          <thead>
			                          	  <g:if test="${myDeployments.size()>0}">
			                              <tr class="info">
											  	<td colspan="12">
												  	<div class="pull-left text-head"><input type="checkbox" id="selectAll" ><strong>&nbsp;Select All</strong> </div>				  	
												  	<div id="btn-group-agent" class="hide-segment btn-group pull-right ">
				                                 	 	<a title="Stop Agents" class="stop-agents btn btn-default" href="${createLink(uri: '/admin/lab//stop/', absolute: true)}"><i class='fa fa-stop' ></i></a>
				                                   	 	<a title="Clean cache from host" class="cache-agents btn btn-default" href="${createLink(uri: '/admin/lab/cache/', absolute: true)}"><i class="fa fa-eraser" ></i></a>
				                                        <a title="Update Agents" class="update-agents btn btn-default" href="${createLink(uri: '/admin/lab/update/', absolute: true)}"><i class="fa fa-level-up"></i></a>
													</div>		  	
											  	</td>
										  </tr>
										  </g:if>
			                              <tr>
			                                  <th></th>
			                                  <th>Cluster</th>
			                                  <th>Image</th>
			                                  <th>Access by</th>
			                                  <th>Hostname</th>
			                                  <th>Status</th>
			                                  <th>Time Left</th>
			                                  <th>IP</th>
			                                  <th>Actions</th>
			                              </tr>
			                          </thead>
			                          <tbody>
			                          <g:each in="${myDeployments}" status="i" var="deployment"> 
			                          	  <input type="hidden" name="deployment_${deployment.id}" value="${deployment.id}"> 
			                              <tr>
			                              	  <td class="column-center">	
									      		<input type="checkbox" name="cluster_${deployment.cluster.id}" class="all"/>  
									      	  </td>
									      	  <td><small>${deployment.cluster.cluster.name}</small></td>									      	  
									      	  <td style="padding:0px !important">
			                                  	 <table class="table insert-table">
				                                  	<tbody> 
				                                  	<g:each in="${deployment.cluster.images}" status="index" var="image"> 
					                                  	<tr>						                                  	
						                                  	<g:if test="${index==0}"><td class="insert-row"></g:if><g:else><td></g:else>
						                                  	<input type="checkbox" name="image_${image.id}" class="all"/> 
						                                  	<small>${image.image.name }</small></td>
					                                  	</tr>
					                                </g:each> 
				                                  	</tbody>
			                                  	 </table>
			                                  </td>								      	  
									      	  <td style="padding:0px !important">
			                                  	 <table class="table insert-table">
				                                  	<tbody> 
				                                  	<g:each in="${deployment.cluster.images}" status="index" var="image"> 
					                                  	<tr>
						                                  	<g:if test="${index==0}"><td class="insert-row"></g:if><g:else><td></g:else>
						                                  	<small>${image.image.accessProtocol}</small></td>
					                                  	</tr>
					                                </g:each> 
				                                  	</tbody>
			                                  	 </table>
			                                  </td>			                                  
			                                  <td style="padding:0px !important">
			                                  	 <table class="table insert-table">
				                                  	<tbody> 
				                                  	<g:each in="${deployment.cluster.images}" status="index" var="image"> 					                                  	
				                                  		<g:each in="${image.getActiveExecutions()}" status="index_col" var="execution">
				                                  		    <tr>
				                                  			<g:if test="${index_col==0 && index==0}"><td class="insert-row"></g:if><g:else><td></g:else>
				                                  			<input type="checkbox" name="execution_${execution.id}" class="all"/> 
					                                  		<small>${execution.name}</small></td>
					                                  		</tr>
				                                  		</g:each> 
					                                </g:each> 
				                                  	</tbody>
			                                  	 </table>
			                                  </td>
			                                  <td></td>
			                                  <td></td>
			                                  <td></td>
			                                  <td></td>
			                                </tr>	
									     </g:each>			                                                        
			                    	</tbody>
			                	</table>
			            	</div><!-- /.box-body -->                         
		                </div>                         	
		     		</div>
             	</form>  
             </div>
         </div>	
	</section><!-- /.content -->    
</body>
               