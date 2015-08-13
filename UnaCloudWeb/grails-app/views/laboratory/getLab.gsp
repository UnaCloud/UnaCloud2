<%@page import="unacloudEnums.MonitoringStatus"%>
<%@page import="unacloud2.PhysicalMachineStateEnum"%>

<html>
   <head>
      <meta name="layout" content="main"/>
      
      <r:require modules="bootstrap"/>
   </head>
<body>

<div class="hero-unit span9">
	<g:link controller="laboratory" action="index" style="display: -webkit-box;"><i class="icon-chevron-left" title="Back"></i><h5 style="margin: 3px;">Back to Laboratory list</h5></g:link>
	<h3>${lab.name}</h3>
	<form id="form_machines">
		<div id="label-message"></div>
		<table class="table table-bordered"  style="background:white" >
			<tr class="info">
			  	<td class="info" colspan="12">
				  	<input type="checkbox" id="selectAll"><small>&nbsp;Select All</small> 				  	
				  	<g:link controller="laboratory" action="createMachine" params="${[id: lab.id]}" title="Create PM"><i class="icon-plus-sign pull-right"></i></g:link>  	
				  	<a title="Configurate Monitoring" id="monitorConfig" style="cursor:pointer"><span class="icon-time pull-right"></span></a>
				  	<a title="Get Monitoring Reports" id="monitorReports" data-id="${lab.id}" style="cursor:pointer"><span class="icon-eye-open pull-right"></span></a>
				  	<a title="Update agents" id="updateMachines" style="cursor:pointer"><span class="icon-refresh pull-right"></span></a>	
				  	<a title="Clear VM cache" id="clearCache" style="cursor:pointer"><span class="icon-fire pull-right"></span></a>  
				  	<a title="Stop agents" id="stopMachines" style="cursor:pointer"><span class="icon-stop pull-right"></span></a>  		  	
			  	</td>
			</tr>
			<tr>
				<th>Name</th>
			  	<th>IP</th>
			  	<th>State</th>
			  	<th>User</th>
			  	<th>Monitor<br> CPU</th>
			  	<th>Monitor<br> Energy</th>	
				<th>Options</th>
			</tr>		 
			<g:each in="${machineSet}" status="i" var="machine">    
		    	<tr>
			      	<td>	
			      		<input type="checkbox" name="machine${machine.id}" class="all"/>   		
			        	<small>&nbsp;${machine.name}</small> 
			        </td>
			        <td>
			   			<small>${machine.ip.ip}</small>
			   		</td>
			   		<td>
				   		<g:if test="${machine.state.equals(PhysicalMachineStateEnum.ON) }">
				   			<g:img file="green.png" title="On"/>
				   		</g:if>
				   		<g:if test="${machine.state.equals(PhysicalMachineStateEnum.DISABLED) }">
				   			<g:img file="blue.png" title="Disabled"/>
				   		</g:if>
				   		<g:if test="${machine.state.equals(PhysicalMachineStateEnum.OFF) }">
				   			<g:img file="red.png" title="Off"/>
				   		</g:if>
			   		</td>
			   		<td>
				   		<g:if test="${machine.withUser }">
				   			<small>Yes</small>
				   		</g:if>
				   		<g:if test="${!machine.withUser }">
				   			<small>No</small>
				   		</g:if>
			   		</td>
			   		<td>
				   		<g:if test="${machine.monitorStatus.equals(MonitoringStatus.RUNNING) }">
				   			<g:img file="green.png" title="${machine.monitorStatus.getTitle()}"/>
				   		</g:if>
				   		<g:if test="${machine.monitorStatus.equals(MonitoringStatus.OFF) }">
				   			<g:img file="red.png" title="${machine.monitorStatus.getTitle()}"/>
				   		</g:if>
				   		<g:if test="${machine.monitorStatus.equals(MonitoringStatus.ERROR) }">
				   			<g:img file="red.png" title="${machine.monitorStatus.getTitle()}"/>
				   		</g:if>
				   		<g:if test="${machine.monitorStatus.equals(MonitoringStatus.DISABLE)}">
				   			<g:img file="gray.png" title="${machine.monitorStatus.getTitle()}"/>
				   		</g:if>
				   		<g:if test="${machine.monitorStatus.equals(MonitoringStatus.STOPPED) }">
				   			<g:img file="amber.png" title="${machine.monitorStatus.getTitle()}"/>
				   		</g:if>
				   		<g:if test="${machine.monitorStatus.equals(MonitoringStatus.INIT) }">
				   			<g:img file="blue.png" title="${machine.monitorStatus.getTitle()}"/>
				   		</g:if>
			   		</td>
			   		<td>
				   		<g:if test="${machine.monitorStatusEnergy.equals(MonitoringStatus.RUNNING) }">
				   			<g:img file="green.png" title="${machine.monitorStatusEnergy.getTitle()}"/>
				   		</g:if>
				   		<g:if test="${machine.monitorStatusEnergy.equals(MonitoringStatus.OFF)}">
				   			<g:img file="red.png" title="${machine.monitorStatusEnergy.getTitle()}"/>
				   		</g:if>
				   		<g:if test="${machine.monitorStatusEnergy.equals(MonitoringStatus.ERROR) }">
				   			<g:img file="red.png" title="${machine.monitorStatusEnergy.getTitle()}"/>
				   		</g:if>
				   		<g:if test="${machine.monitorStatusEnergy.equals(MonitoringStatus.DISABLE) }">
				   			<g:img file="gray.png" title="${machine.monitorStatusEnergy.getTitle()}"/>
				   		</g:if>
				   		<g:if test="${machine.monitorStatusEnergy.equals(MonitoringStatus.STOPPED) }">
				   			<g:img file="amber.png" title="${machine.monitorStatusEnergy.getTitle()}"/>
				   		</g:if>
				   		<g:if test="${machine.monitorStatusEnergy.equals(MonitoringStatus.INIT) }">
				   			<g:img file="blue.png" title="${machine.monitorStatusEnergy.getTitle()}"/>
				   		</g:if>
			   		</td>
			   		<td>
			   		    <g:link controller="monitoring" params="${[id: machine.id] }" ><i class="icon-eye-open" title="Show Monitoring"></i></g:link>
			   			<g:link action="editMachine" params="${[id: machine.id, labId: lab.id] }" ><i class="icon-pencil" title="Edit Machine"></i></g:link>
			   		</td>
			   		
		       </tr>	   	
			</g:each>
		</table>
	</form>	
</div>
 <g:javascript src="laboratory.js" />
 <script>$(document).on('ready',getLab())</script>
</body>
   