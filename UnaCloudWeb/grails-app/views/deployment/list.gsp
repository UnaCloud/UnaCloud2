<%@page import="uniandes.unacloud.share.enums.ExecutionStateEnum"%>
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
            	<g:if test="${deployments==null}">
            	<div class="box box-primary"> 
             		<div class="box-header">
                        <h3 class="box-title">My deployments</h3>
                    </div><!-- /.box-header -->             			
             		<div class="box-body"> 
             	</g:if>
             	<g:else>
             	<div class="nav-tabs-custom">
                     <ul class="nav nav-tabs">
                         <li class="active"><a href="#tab_my" data-toggle="tab">My Deployments</a></li>
                         <li><a href="#tab_deploy" data-toggle="tab">Users Deployments</a></li>
                     </ul>
                     <div class="tab-content">
                         <div class="tab-pane active" id="tab_my">
             	</g:else>
             		<!-- INIT TABLE -->
		             	<div class=" table-responsive">
		                	<form method="post" id="form_deployments">
		                    	<table id="unacloudTable" class="table table-bordered table-striped">
		                          	<thead>
		                          		<tr class="info">
										  	<td colspan="12">
											  	<div class="pull-left text-head"><input type="checkbox" id="selectAll" ><strong>&nbsp;Select All</strong> </div>				  	
											  	<div id="btn-group-agent" class="btn-group pull-right ">
			                                 	 	<a title="Stop Executions" class="stop-executions btn btn-default" href="${createLink(uri: '/services/deployment/stop', absolute: true)}" data-toggle="tooltip"><i class='fa fa-stop' ></i></a>
			                                   	</div>		  	
										  	</td>
									  	</tr>
		                              	<tr>
		                                	<th></th>
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
		                              	<g:each in="${deployment.images}" var="image"> 
		                              	<g:if test="${image.getActiveExecutions().size()>0}">
							           	<input type="hidden" name="deployment_${deployment.id}" value="${deployment.id}"> 
		                              	<tr>	
		                              		<td class="column-center">	
		                              			<a title="Global Snapshot" class="global_snapshot btn btn-default" data-id="${deployment.id}" href="${createLink(uri: '/services/deployment/snap/', absolute: true)}"  data-toggle="tooltip"><i class='fa fa-rocket' ></i></a>
			                									
								      		</td>		                              	
		                              		<td class="column-center">	
								      			<input data-id="${image.id}" type="checkbox" name="image_${image.id}" class="all image_check"/>  
								      		</td>
								      		<td><small>${deployment.cluster.name}</small></td>
								      		<td><small>${image.image.name}</small> <a title="Add executions" class="add_execution" href="${createLink(uri: '/services/deployment/'+image.id+'/add', absolute: true)}"  data-toggle="tooltip"><i class='fa fa-plus-square' ></i></a>		                									</td>
								      		<td><small>${image.image.accessProtocol}</small></td>
								       		<td style = "padding:0px !important">
		                                  	 	<table class = "table insert-table embeded_table">
			                                  		<tbody> 				                                 				                                  	
			                                  			<g:each in="${image.getActiveExecutions()}" status = "index" var="execution">
			                                  		    <tr>			                                  		   
				                                  			<g:if test="${index == 0}"><td class = "insert-row"></g:if><g:else><td></g:else>
				                                  			<input type="checkbox" name="execution_${execution.id}" class="all image_${image.id}"/> 
					                                  		<small>${execution.name}</small></td>					                                  	
				                                  		</tr>
			                                  			</g:each> 
			                                  		</tbody>
		                                  	 	</table>
		                                	 </td>
		                                	 <td style = "padding:0px !important">
		                                  	 	<table class = "table insert-table embeded_table">
			                                  		<tbody> 				                                 				                                  	
			                                  			<g:each in="${image.getActiveExecutions()}" status = "index" var = "execution">
			                                  		    <tr>
			                                  			   <g:if test = "${index == 0}"><td class = "insert-row"></g:if><g:else><td></g:else>
			                                  			   <g:if test = "${		
																    execution.state.state.equals(ExecutionStateEnum.TRANSMITTING)
																 || execution.state.state.equals(ExecutionStateEnum.REQUEST_COPY)
																 || execution.state.state.equals(ExecutionStateEnum.RECONNECTING)
																 || execution.state.state.equals(ExecutionStateEnum.COPYING)
																 }">
						                                 	 <span class="label label-warning">${execution.state.state.name} </span> <i class="fa fa-info-circle text-info" data-toggle="tooltip" title="${execution.message}"></i>
						                                   </g:if>
						                                   <g:elseif test="${
															   	    execution.state.state.equals(ExecutionStateEnum.REQUESTED)
															     || execution.state.state.equals(ExecutionStateEnum.CONFIGURING)
															     || execution.state.state.equals(ExecutionStateEnum.DEPLOYING)
						                                   		 || execution.state.state.equals(ExecutionStateEnum.FINISHING)
																}">
						                                  	 <span class="label label-primary">${execution.state.state.name}</span> <i class="fa fa-info-circle text-info" data-toggle="tooltip" title="${execution.message}"></i>
						                                   </g:elseif>
						                                   <g:elseif test="${execution.state.state.equals(ExecutionStateEnum.DEPLOYED)}">
						                                  	 <span class="label label-success">${execution.state.state.name}</span> <i class="fa fa-info-circle text-info" data-toggle="tooltip" title="${execution.message}"></i>
						                                   </g:elseif>
						                                   <g:elseif test="${execution.state.state.equals(ExecutionStateEnum.FAILED)}">
						                                  	 <span class="label label-danger">${execution.state.state.name}</span> <i class="fa fa-info-circle text-info" data-toggle="tooltip" title="${execution.message}"></i>
						                                   </g:elseif>
						                                   <g:elseif test="${execution.state.state.equals(ExecutionStateEnum.FINISHED)}">
						                                  	 <span class="label label-default">${execution.state.state.name}</span> <i class="fa fa-info-circle text-info" data-toggle="tooltip" title="${execution.message}"></i>
						                                   </g:elseif>
														   </td>
				                                  		</tr>
			                                  			</g:each> 
			                                  		</tbody>
		                                  	 	</table>
		                                	 </td>
		                                 	 <td style="padding:0px !important">
		                                  	 	<table class="table insert-table embeded_table">
			                                  		<tbody> 				                                 				                                  	
			                                  			<g:each in="${image.getActiveExecutions()}" status = "index" var="execution">
			                                  		    <tr>			                                  		    
				                                  			<g:if test="${index==0}"><td class="insert-row"></g:if><g:else><td></g:else>
					                                  			<g:if test="${execution.showDetails()}"><small>${execution.remainingTime()}</small></g:if>
															</td>														
				                                  		</tr>
			                                  			</g:each> 
			                                  		</tbody>
		                                  		</table>
		                                 	</td>
		                                	<td style="padding:0px !important">
		                                  		<table class="table insert-table embeded_table">
			                                  		<tbody> 				                                 				                                  	
			                                  			<g:each in="${image.getActiveExecutions()}" status = "index" var="execution">
			                                  		    <tr>
				                                  			<g:if test="${index==0}"><td class="insert-row"></g:if><g:else><td></g:else>
					                                  			<small>${execution.mainIp().ip}</small>
															</td>
				                                  		</tr>
			                                  			</g:each> 
			                                  		</tbody>
		                                  	 	</table>
		                                 	</td>
		                                 	<td style="padding:0px !important">
		                                  		<table class="table insert-table embeded_table">
			                                  		<tbody> 				                                 				                                  	
			                                  			<g:each in="${image.getActiveExecutions()}" status="index" var="execution">
			                                  		    <tr>			                                  		    
				                                  			<g:if test="${index == 0}"><td class="insert-row"></g:if><g:else><td></g:else>
					                                  		<g:if test="${execution.state.state == ExecutionStateEnum.DEPLOYED}"><a title="Download" class="download_btn btn btn-default" data-imagename="${image.image.name}" data-id="${execution.id}" href="${createLink(uri: '/services/deployment/download/', absolute: true)}"  data-toggle="tooltip"><i class='fa fa-download' ></i></a></g:if>	
		                									</td>		                								
				                                  		</tr>
			                                  			</g:each> 
			                                  		</tbody>
		                                  	 	</table>
		                                 	</td>
		                              	</tr>	  		
							            </g:if>		                              	
		                              	</g:each>
								    </g:each>			                                                        
		                    		</tbody>
		                		</table>
		                	</form> 
		            	</div> 
             	<g:if test="${deployments==null}">
           	   		</div>                         	
	     		</div>	
             	</g:if>
             	<g:else>
             			</div><!-- /.tab-pane -->
                    	<div class="tab-pane" id="tab_deploy">  
             				<div class=" table-responsive">
			                	<form method="post" id="form_deployments">
			                    	<table id="unacloudTable2" class="table table-bordered table-striped">
			                          	<thead>
			                          		<tr class="info">
											  	<td colspan="12">
												  	<div class="pull-left text-head"><input type="checkbox" id="selectAll" ><strong>&nbsp;Select All</strong> </div>				  	
												  	<div id="btn-group-agent" class="btn-group pull-right ">
				                                 	 	<a title="Stop Executions" class="stop-executions btn btn-default" href="${createLink(uri: '/services/deployment/stop', absolute: true)}" data-toggle="tooltip"><i class='fa fa-stop' ></i></a>
				                                   	</div>		  	
											  	</td>
									  		</tr>
			                              	<tr>
			                                	<th></th>
			                                	<th>Owner</th>
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
			                          	<g:each in="${deployments}" status = "i" var="deployment"> 
			                              	<g:each in="${deployment.images}" var="image"> 
			                              	<g:if test="${image.getActiveExecutions().size()>0}">
			                              	<input type="hidden" name="deployment_${deployment.id}" value="${deployment.id}"> 
			                              	<tr>			                              	
			                              		<td class="column-center">	
									      			<input type="checkbox" name="image_${image.id}" class="all"/>  
									      		</td>
									      		<td><small>${deployment.user.username}</small></td>
									      		<td><small>${deployment.cluster.name}</small></td>
									      		<td><small>${image.image.name}</small></td>
									      		<td><small>${image.image.accessProtocol}</small></td>
									       		<td style="padding:0px !important">
			                                  	 	<table class="table insert-table embeded_table">
				                                  		<tbody> 				                                 				                                  	
				                                  			<g:each in="${image.getActiveExecutions()}" status = "index" var="execution">
				                                  		    <tr>
					                                  			<g:if test="${index==0}"><td class="insert-row"></g:if><g:else><td></g:else>
					                                  			<input type="checkbox" name="execution_${execution.id}" class="all"/> 
						                                  		<small>${execution.name}</small></td>
					                                  		</tr>
				                                  			</g:each> 
				                                  		</tbody>
			                                  	 	</table>
			                                	 </td>
			                                	 <td style="padding:0px !important">
			                                  	 	<table class="table insert-table  embeded_table">
				                                  		<tbody> 				                                 				                                  	
				                                  			<g:each in="${image.getActiveExecutions()}" status = "index" var="execution">
				                                  		    <tr>
				                                  			   <g:if test="${index==0}"><td class="insert-row"></g:if><g:else><td></g:else>
				                                  			   <g:if test = "${		
																   	   execution.state.state.equals(ExecutionStateEnum.TRANSMITTING)
																 	|| execution.state.state.equals(ExecutionStateEnum.REQUEST_COPY)
																 	|| execution.state.state.equals(ExecutionStateEnum.RECONNECTING)
																 	|| execution.state.state.equals(ExecutionStateEnum.COPYING)
																 	}">
						                                 	   <span class="label label-warning">${execution.state.state.name} </span> <i class="fa fa-info-circle text-info" data-toggle="tooltip" title="${execution.message}"></i>
							                                   </g:if>
							                                   <g:elseif test="${
																   	    execution.state.state.equals(ExecutionStateEnum.REQUESTED)
																     || execution.state.state.equals(ExecutionStateEnum.CONFIGURING)
																     || execution.state.state.equals(ExecutionStateEnum.DEPLOYING)
							                                   		 || execution.state.state.equals(ExecutionStateEnum.FINISHING)
																	}">
							                                  	 <span class="label label-primary">${execution.state.state.name}</span> <i class="fa fa-info-circle text-info" data-toggle="tooltip" title="${execution.message}"></i>
							                                   </g:elseif>
							                                   <g:elseif test="${execution.state.state.equals(ExecutionStateEnum.DEPLOYED)}">
							                                  	 <span class="label label-success">${execution.state.state.name}</span> <i class="fa fa-info-circle text-info" data-toggle="tooltip" title="${execution.message}"></i>
							                                   </g:elseif>
							                                   <g:elseif test="${execution.state.state.equals(ExecutionStateEnum.FAILED)}">
							                                  	 <span class="label label-danger">${execution.state.state.name}</span> <i class="fa fa-info-circle text-info" data-toggle="tooltip" title="${execution.message}"></i>
							                                   </g:elseif>
							                                   <g:elseif test="${execution.state.state.equals(ExecutionStateEnum.FINISHED)}">
							                                  	 <span class="label label-default">${execution.state.state.name}</span> <i class="fa fa-info-circle text-info" data-toggle="tooltip" title="${execution.message}"></i>
							                                   </g:elseif>
															   </td>
					                                  		</tr>
				                                  			</g:each> 
				                                  		</tbody>
			                                  	 	</table>
			                                	 </td>
			                                 	 <td style="padding:0px !important">
			                                  	 	<table class="table insert-table embeded_table">
				                                  		<tbody> 				                                 				                                  	
				                                  			<g:each in="${image.getActiveExecutions()}" status = "index" var="execution">
				                                  		    <tr>
					                                  			<g:if test="${index==0}"><td class="insert-row"></g:if><g:else><td></g:else>
						                                  			<small>${execution.remainingTime()}</small>
																</td>
					                                  		</tr>
				                                  			</g:each> 
				                                  		</tbody>
			                                  		</table>
			                                 	</td>
			                                	<td style="padding:0px !important">
			                                  		<table class="table insert-table embeded_table">
				                                  		<tbody> 				                                 				                                  	
				                                  			<g:each in="${image.getActiveExecutions()}" status =" index" var="execution">
				                                  		    <tr>
					                                  			<g:if test="${index==0}"><td class="insert-row"></g:if><g:else><td></g:else>
						                                  			<small>${execution.mainIp().ip}</small>
																</td>
					                                  		</tr>
				                                  			</g:each> 
				                                  		</tbody>
			                                  	 	</table>
			                                 	</td>
			                                 	<td style="padding:0px !important">
			                                  		<table class="table insert-table embeded_table">
				                                  		<tbody> 				                                 				                                  	
			                                  			<g:each in="${image.getActiveExecutions()}" status="index" var="execution">
			                                  		    <tr>			                                  		    
				                                  			<g:if test="${index == 0}"><td class="insert-row"></g:if><g:else><td></g:else>
					                                  		<g:if test="${execution.state.state == ExecutionStateEnum.DEPLOYED}"><a title="Download" class="download_btn btn btn-default" data-imagename="${image.image.name}" data-id="${execution.id}" href="${createLink(uri: '/services/deployment/download/', absolute: true)}"  data-toggle="tooltip"><i class='fa fa-download' ></i></a></g:if>	
		                									</td>		                								
				                                  		</tr>
			                                  			</g:each> 
			                                  		</tbody>
			                                  	 	</table>
			                                 	</td>
			                              	</tr>	
			                              	</g:if>				                              	
			                              	</g:each>
									    </g:each>			                                                        
			                    		</tbody>
			                		</table>
			                	</form> 
			            	</div>              	
             			</div><!-- /.tab-pane -->
                	</div><!-- /.tab-content -->
                </div><!-- nav-tabs-custom --> 
             	</g:else>
             </div>
         </div>	
	</section><!-- /.content -->    
</body>
               