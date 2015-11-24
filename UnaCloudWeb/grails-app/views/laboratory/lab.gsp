<%@page import="unacloud.enums.PhysicalMachineStateEnum"%>
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
                  <a href="${createLink(uri: '/admin/lab/'+lab.id+'/new', absolute: true)}" class="btn btn-primary btn-sm"><i class='fa fa-plus' ></i> Add host</a>
                  <a href="${createLink(uri: '/admin/lab/'+lab.id+'/pool/new', absolute: true)}" class="btn btn-primary btn-sm"><i class='fa fa-plus' ></i> Add IP Pool</a>
                  <div class="pull-right">                  
		              <a href="${createLink(uri: '/admin/lab/edit/'+lab.id, absolute: true)}" class="btn btn-primary btn-sm"><i class='fa fa-pencil-square' ></i> Edit</a>
	                  <g:if test="${lab.enable}">
	                  <a id="disable-lab" data-state="true" data-id="${lab.id}" href="${createLink(uri: '/admin/lab/disable/', absolute: true)}" class="btn btn-danger btn-sm"><i class='fa fa-ban' ></i> Disable</a>
		              </g:if> 
		              <g:else>
		              <a id="disable-lab" data-state="false" data-id="${lab.id}" href="${createLink(uri: '/admin/lab/disable/', absolute: true)}" class="btn btn-primary btn-sm"><i class='fa fa-check' ></i> Enable</a>
		              </g:else>
		              <a id="delete-lab" data-id="${lab.id}" href="${createLink(uri: '/admin/lab/delete/', absolute: true)}" class="btn btn-danger btn-sm"><i class='fa fa-trash-o' ></i> Delete</a>
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
	                         	<p class="help-block">Host which from this Lab.</p>	                        		
	                            <div class="box-body table-responsive">
				                      <table id="unacloudTable" class="table table-bordered table-striped">
				                          <thead>
				                          	  <g:if test="${machineSet.size()>0}">
				                              <tr class="info">
												  	<td colspan="12">
													  	<div class="pull-left text-head"><input type="checkbox" id="selectAll" ><strong>&nbsp;Select All</strong> </div>				  	
													  	<div id="btn-group-agent" class="hide-segment btn-group pull-right ">
					                                 	 	<a title="Stop Agents" class="stop-agents btn btn-default" href="${createLink(uri: '/admin/lab/'+lab.id+'/stop/', absolute: true)}"><i class='fa fa-stop' ></i></a>
					                                   	 	<a title="Clean cache from host" class="cache-agents btn btn-default" href="${createLink(uri: '/admin/lab/'+lab.id+'/cache/', absolute: true)}"><i class="fa fa-eraser" ></i></a>
					                                        <a title="Update Agents" class="update-agents btn btn-default" href="${createLink(uri: '/admin/lab/'+lab.id+'/update/', absolute: true)}"><i class="fa fa-level-up"></i></a>
														</div>		  	
												  	</td>
											  </tr>
											  </g:if>
				                              <tr>
				                                  <th></th>
				                                  <th>Host Name</th>
				                                  <th>IP</th>
				                                  <th>State</th>
				                                  <th>Monitoring</th>
				                                  <th>Actions</th>
				                              </tr>
				                          </thead>
				                          <tbody>
				                          <g:each in="${machineSet}" status="i" var="machine"> 
				                              <tr>
				                              	  <td class="column-center">	
										      		<input type="checkbox" name="machine_${machine.id}" class="all"/>  
										      	  <td> 		
										        	<small>${machine.name} </small><g:if test="${machine.withUser}"><i class="fa fa-user text-green" title="With user"></i></g:if>
											      </td>
				                                  <td><small>${machine.ip.ip}</small></td>
				                                  <td>
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
											      <td>
											        <g:if test="${machine.monitorSystem==null}">
											   			<span class="label label-default">DISABLED</span>
											   		</g:if>
											   		<g:elseif test="${machine.monitorSystem.disable}">
											   			<span class="label label-danger">OFF</span>
											   		</g:elseif>
											   		<g:else>
											   			<span class="label label-success">ON</span>
											   		</g:else>
											      </td>
				                                  <td class="column-center"> 
					                               	  <div class="btn-group">
						                                  <a title="Delete" class="delete_machines btn btn-default" data-id="${machine.id}" href="${createLink(uri: '/admin/lab/'+lab.id+'/delete/', absolute: true)}" ><i class='fa fa-trash-o' ></i></a>
						                                  <a title="Edit" class="btn btn-default"  href="${createLink(uri: '/admin/lab/'+lab.id+'/edit/'+machine.id, absolute: true)}" ><i class="fa fa-pencil-square" ></i></a>
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
			                          <g:each in="${lab.ipPools}" status="i" var="pool"> 
			                              <tr>
			                              	  <td><small>${pool.first().ip}</small></td>
			                                  <td><small>${pool.last().ip}</small></td>
			                                  <td><small>${pool.gateway}</small></td>
			                                  <td><small>${pool.mask}</small></td>
			                                  <td class="column-center"> 
				                               	  <div class="btn-group">
					                                  <a title="Delete" class="delete_pool btn btn-default" data-id="${pool.id}" href="${createLink(uri: '/admin/lab/'+lab.id+'/pool/delete/', absolute: true)}" ><i class='fa fa-trash-o' ></i></a>
					                                  <a title="Edit" class="btn btn-default" href="${createLink(uri: '/admin/lab/'+lab.id+'/pool/'+pool.id, absolute: true)}" ><i class="fa fa-pencil-square" ></i></a>
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
               