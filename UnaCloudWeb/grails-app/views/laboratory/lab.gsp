<%@page import="uniandes.unacloud.share.enums.PhysicalMachineStateEnum"%>
<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            ${lab.name}
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li><a href="${createLink(uri: '/admin/lab/list', absolute: true)}"><i class="fa fa-sitemap"></i> All laboratories</a></li>
            <li class="active">${lab.name}</li>
        </ol>   
           	         
    </section>
    <!-- Main content -->
    <section class="content">
     	<div class="row">     		     
             <div class="col-xs-12">  
             	  <g:render template="/share/message"/>                       
                  <a href="${createLink(uri: '/admin/lab/'+lab.id+'/machine/new', absolute: true)}" class="btn btn-primary btn-sm"><i class='fa fa-plus' ></i> Add host</a>
                  <a href="${createLink(uri: '/admin/lab/'+lab.id+'/pool/new', absolute: true)}" class="btn btn-primary btn-sm"><i class='fa fa-plus' ></i> Add IP Pool</a>
                  <div class="pull-right">                  
		              <a href="${createLink(uri: '/admin/lab/'+lab.id+'/edit', absolute: true)}" class="btn btn-primary btn-sm"><i class='fa fa-pencil-square' ></i> Edit</a>
	                  <g:if test="${lab.enable}">
	                  <a id="disable-lab" data-state="true" data-id="${lab.id}" data-method="disable" href="${createLink(uri: '/admin/lab/', absolute: true)}" class="btn btn-danger btn-sm"><i class='fa fa-ban' ></i> Disable</a>
		              </g:if>
		              <g:else>
		              <a id="disable-lab" data-state="false" data-id="${lab.id}" data-method="disable" href="${createLink(uri: '/admin/lab/', absolute: true)}" class="btn btn-primary btn-sm"><i class='fa fa-check' ></i> Enable</a>
		              </g:else>
					  <!-- For a strange reason delete does not work as well as disable, for that this is the only other way-->
		              <a title="Delete" href="${createLink(uri: '/admin/lab/'+lab.id+"/delete", absolute: true)}" class="btn btn-danger btn-sm"><i class='fa fa-trash-o' ></i> Delete</a>
		          </div>
                  <hr>
                  <div class="nav-tabs-custom">
                     <ul class="nav nav-tabs">
                         <li class="active"><a href="#tab_host" data-toggle="tab">Hosts</a></li>
                         <li><a href="#tab_ips" data-toggle="tab">IPs</a></li>
                     </ul>
                     <div class="tab-content">
                         <div class="tab-pane active" id="tab_host">
                            <form method="post" id="form_machines">
	                         	<p class="help-block">Lab host list.</p>	                       		
	                            <div class="box-body table-responsive">
				                      <table id="unacloudTable" class="table table-bordered table-striped">
				                          <thead>
				                          	  <g:if test="${machineSet.size()>0}">
				                              <tr class="info">
												  	<td colspan="12">
													  	<div class="pull-left text-head"><input type = "checkbox" id = "selectAll" ><strong>&nbsp;Select All</strong> </div>				  	
													  	<div id = "btn-group-agent" class = "btn-group pull-right ">
					                                 	 	<a title = "Stop Agents" class = "stop-agents btn btn-default" href = "${createLink(uri: '/admin/lab/' + lab.id + '/machine/task/stop/', absolute: true)}" data-toggle = "tooltip"><i class = 'fa fa-stop' ></i></a>
					                                   	 	<a title = "Clean host cache" class = "cache-agents btn btn-default" href = "${createLink(uri: '/admin/lab/' + lab.id + '/machine/task/cache/', absolute: true)}" data-toggle = "tooltip"><i class = "fa fa-eraser" ></i></a>
					                                        <a title = "Update Agents" class = "update-agents btn btn-default" href = "${createLink(uri: '/admin/lab/' + lab.id + '/machine/task/update/', absolute: true)}" data-toggle = "tooltip"><i class = "fa fa-level-up"></i></a>
															<a title = "Request Version" class = "no_required_confirm_task btn btn-default" href = "${createLink(uri: '/admin/lab/' + lab.id + '/machine/task/version/', absolute: true)}" data-toggle = "tooltip"><i class = "glyphicon glyphicon-save"></i></a>
					                                        <a title = "Request used disk space" class = "no_required_confirm_task btn btn-default" href = "${createLink(uri: '/admin/lab/' + lab.id + '/machine/task/size/', absolute: true)}" data-toggle = "tooltip"><i class = "glyphicon glyphicon-floppy-save"></i></a>
															<a title = "Request Logs" class = "no_required_confirm_task btn btn-default"  href = "${createLink(uri: '/admin/lab/' + lab.id + '/machine/task/logs/', absolute: true)}" data-toggle = "tooltip"><i class = "glyphicon glyphicon-file" ></i></a>
						                                </div>		  	
												  	</td>
											  </tr>
											  </g:if>
				                              <tr>
				                                  <th></th>
				                                  <th>Host Name</th>
				                                  <th>IP</th>
				                                  <th>State</th>
				                                  <th>Used Space</th>
				                                  <th>Activity</th>
				                                  <th>Platforms</th>
				                                  <th>Actions</th>
				                              </tr>
				                          </thead>
				                          <tbody>
				                          <g:each in="${machineSet}" status="i" var="machine"> 
				                              <tr>
				                              	  <td class="column-center">	
										      		<input type="checkbox" name="machine_${machine.id}" class="all"/>  
										      	  </td>
										      	  <td>
										      	  	<small>${machine.name} 
										      	  		<i class="fa fa-info-circle text-info" data-toggle="tooltip" title="
										      	  			<g:if test = "${machine.getCurrentAgentVersion()}">V ${machine.getCurrentAgentVersion()}</g:if><g:else>NO-VERSION</g:else>">
										      	  		</i>
										      	  	  </small>
										      	  </td>
										      	  <td class="column-center">
										      	  	<small>${machine.ip.ip}</small>
										      	  </td>
				                                  <td class="column-center">
					                                <g:if test="${machine.state.equals(PhysicalMachineStateEnum.ON) }">
											   			<span class="label label-success">${machine.state.toString()}</span>
											   		</g:if>
											   		<g:if test="${machine.state.equals(PhysicalMachineStateEnum.DISABLED) }">
											   			<span class="label label-default">${machine.state.toString()}</span>
											   		</g:if>
											   		<g:if test="${machine.state.equals(PhysicalMachineStateEnum.OFF) }">
											   			<span class="label label-danger">${machine.state.toString()}</span>
											   		</g:if>
											   		<g:if test="${machine.state.equals(PhysicalMachineStateEnum.PROCESSING) }">
											   			<span class="label label-warning">${machine.state.toString()}</span>
											   		</g:if>
				                                  </td>
				                                  <td class="column-center">
				                                  	<small class = 
				                                  		<g:if test = "${machine.getUsedPercentage() > 70}"> "text-danger" </g:if>
				                                  		<g:elseif test = "${machine.getUsedPercentage() > 50}"> "text-warning" </g:elseif>
				                                  		<g:else> "text-success" </g:else>>
				                                  		<g:formatNumber number="${machine.getUsedPercentage()}" format="###,##" />%
				                                  		<i class="fa fa-info-circle text-info" data-toggle="tooltip" title="T: ${machine.getTotalDiskSize()} - F: ${machine.getAvailableDisk()}"></i>
				                                  	</small>
				                                  </td>
				                                  <td class = "column-center">	
										      		<g:if test = "${machine.withUser}"><i class = "fa fa-user text-green" title = "With user" data-toggle="tooltip"></i></g:if> 
										      		<g:if test = "${machine.withExecution()}"><i class = "fa fa-laptop text-green" title = "With executions" data-toggle="tooltip"></i></g:if>
										      	  </td>
										      	  <td class = "platform-list">
										      	 	<g:each in = "${machine.platforms}"  var = "platform">
										      	 		<span class="label label-primary">${platform.name}</span>
										      	 	</g:each>					                               
				                                  </td>
											      <td class="column-center"> 
					                               	  <div class="btn-group">
					                               	  	  <a title = "Detail" class = "btn btn-default" href = "${createLink(uri: '/admin/lab/' + lab.id + '/machine/' + machine.id, absolute: true)}" data-toggle = "tooltip"><i class = 'glyphicon glyphicon-eye-open' ></i></a>
					                               	  </div>
												  </td>
				                              </tr>
					                      </g:each>                         
				                          </tbody>
				                      </table>
				                  </div><!-- /.box-body -->
			                  </form>
                         </div><!-- /.tab-pane -->
                         <div class="tab-pane" id="tab_ips">                         	
                         	<p class="help-block">IP Pool list from this Lab.</p>
                         	<div class="box-body table-responsive">
                           	   <table id="unacloudTable2" class="table table-bordered table-striped">
			                          <thead>			                          	 
			                              <tr>
			                                  <th>Init Range</th>
			                                  <th>End Range</th>
			                                  <th>Network Gateway</th>
			                                  <th>Network Mask</th>
			                                  <th>Actions</th>
			                              </tr>
			                          </thead>
			                          <tbody>
			                          <g:each in="${lab.getPools()}" status="i" var="pool"> 
			                              <tr>
			                              	  <td><small>${pool.first().ip}</small></td>
			                                  <td><small>${pool.last().ip}</small></td>
			                                  <td><small>${pool.gateway}</small></td>
			                                  <td><small>${pool.mask}</small></td>
			                                  <td class="column-center"> 
				                               	  <div class="btn-group">
													  <a title="Delete" class="btn btn-default" href="${createLink(uri: '/admin/lab/' + lab.id + '/pool/'+pool.id+'/delete', absolute: true)}" data-toggle="tooltip"><i class='fa fa-trash-o' ></i></a>
					                                  <a title="Detail" class="btn btn-default" href="${createLink(uri: '/admin/lab/' + lab.id + '/pool/' + pool.id, absolute: true)}" data-toggle="tooltip"><i class="glyphicon glyphicon-eye-open" ></i></a>
					                              </div>
											  </td>
			                              </tr>
				                      </g:each>                         
			                          </tbody>
			                      </table>
			                  </div><!-- /.box-body -->
                         </div><!-- /.tab-pane -->
                     </div><!-- /.tab-content -->
                  </div><!-- nav-tabs-custom -->                  
             </div>
         </div>
	</section><!-- /.content -->   
</body>
               