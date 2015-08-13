<%@page import="unacloudEnums.*"%>
<html>
   <head>
      <meta name="layout" content="main"/>
      <g:javascript src="pages.js" />
   </head>
<body>
<div class="hero-unit span9">
<h3>My Deployments</h3>
<g:form controller="deployment">
	<script>
	var myVar=setInterval(function(){reload()},8000);
	
	function reload()
	{
		
	}
	</script>
	<table class="table table-bordered table-condensed text-center" style="background:white" >
	  <g:if test="${session.user.userType == 'Administrator'}">
	  <tr class="info">
	  	<td class="info" colspan="8">
	  	 
	  	<input type="checkbox" id="View All" ${checkViewAll?"checked":""}  onChange="${remoteFunction(action:'index', update:'body',params:'\'viewAll=\' + this.checked') }"><small>&nbsp;View All</small>
	  	</td>
	  </tr>
	  </g:if>
	  <tr class="info">
	  	<td class="info" colspan="8" >
	  		<input type="checkbox" id="selectAll"><small>&nbsp;Select All</small>
	  	          <g:actionSubmitImage value="stop" src="${resource(dir: 'images', file: 'empty.gif')}" action="stop" class="icon-off pull-right" title="Stop Selected Instances"/>
	              
	              <g:actionSubmitImage value="reset" src="${resource(dir: 'images', file: 'empty.gif')}" action="reset" class="icon-refresh pull-right" title="Reset Selected Instances"/>
	              
	    </td>
	  </tr>	
	  <tr>
	  <th>Cluster</th>
	  <th>Images</th>
	  <th>Access Type</th>
	  <th>HostName</th>
	  <th>Status</th>
	  <th>Time Left</th>
	  <th>IP</th>
	  <th>Options</th>
	  </tr>
	 <div class="all">
	 <g:each in="${deployments}" status="i" var="deployment">   
	  <tr>
	    <td rowspan="${deployment.getTotalActiveVMs()}">
	      <input type="checkbox" name="cluster" id="selectCluster${i}" class="all">
	      <small>
	      <g:if test="${deployment.cluster.cluster!=null }">
	      ${deployment.cluster.cluster.name}
	      </g:if>
	      <g:if test="${deployment.cluster.cluster==null }">
	      None
	      </g:if>
	      </small>
	    </td>
	    
	    <g:each in="${deployment.cluster.images}" status="j" var="image">
	   		
		    <td rowspan="${image.numberOfActiveMachines() }">
		   	<small>${image.image.name }</small>
		   	<br>
		   	<g:link action="addInstancesOptions" controller="deployment" params="${ [id:image.id] }"><i class="icon-plus-sign" title="Add Instances"></i></g:link></td>
		    <td rowspan="${image.numberOfActiveMachines() }">
		    <small>
		    ${image.image.accessProtocol }
		    <g:if test= "${(deployment.stopTime==null)}">
				<g:link action="downloadKey" controller="user">(KeyPair)</g:link> 
			</g:if>
		    </small>
		    </td> 
		    <g:each in="${image.getOrderedVMs() }" status="k" var="virtualMachine">  
			   	<g:if test="${ virtualMachine.status!= VirtualMachineExecutionStateEnum.FINISHED}">
				   	<td >
	
					   	<input type="checkbox" name="hostname${virtualMachine.id}" class="hostname${i} all">
					   	<small>${virtualMachine.name }</small>
				    </td>
				    <td>
					    <g:if test="${virtualMachine.status== VirtualMachineExecutionStateEnum.CONFIGURING || virtualMachine.status== VirtualMachineExecutionStateEnum.COPYING}">
					    	 <g:img file="blue.png" title="${virtualMachine.message }"/>
					    </g:if>
					    <g:if test="${virtualMachine.status==VirtualMachineExecutionStateEnum.DEPLOYING}">
					    	 <g:img file="amber.png" title="${virtualMachine.message }"/>
					    </g:if>
					    <g:if test="${virtualMachine.status==VirtualMachineExecutionStateEnum.DEPLOYED}">
					    	 <g:img file="green.png" title="${virtualMachine.message }"/>
					    </g:if>
					    <g:if test="${virtualMachine.status==VirtualMachineExecutionStateEnum.FAILED}">
					    	 <g:img file="red.png" title="${virtualMachine.message }"/>
					    </g:if>
					</td>
					<td>
					  	<small>${(virtualMachine.stopTime==null)? 'N/A':virtualMachine.remainingTime()}</small>
					</td>
					<td>
					    <g:if test="${virtualMachine.ip!=null}">
					    	<small>${virtualMachine.ip.ip }</small>	
					    </g:if>
					    <g:if test="${virtualMachine.ip==null}">
					  	    <small>None</small>	
					    </g:if>
				    </td>	
				    <td>		
				        		  
					    <g:if test="${virtualMachine.status==VirtualMachineExecutionStateEnum.DEPLOYED && virtualMachine.stopTime!=null}">
					        <a class="dialog_button" data-imageid="${image.id}" data-vmachineid="${virtualMachine.id}" data-imagename="${image.image.name}">
					            <i class="icon-download-alt" title="Save image"></i>							
					        </a>    					
					    </g:if>					
				    </td>		
				    </tr>
				    <tr>
			    </g:if>
		    </g:each>    
	    </g:each>
	
	
	  </tr>
	  <tr>
	</g:each>
	</div>
	</table>
	
	</g:form>
	</div>
	<script type="text/javascript">
	$(function () {
	    $('#selectAll').click(function (event) {
	
	        var selected = this.checked;
	        // Iterate each checkbox
	        $('.all:checkbox').each(function () {    this.checked = selected; });
	
	    });
	 });
	</script>
	<g:each in="${deployments}" status="i" var="deployment">
	<script type="text/javascript">
	 $('#selectCluster${i}').click(function (event) {
	
	        var selected = this.checked;
	        // Iterate each checkbox
	        $('.hostname${i}:checkbox').each(function () {    this.checked = selected; });
	
	    });
	</script>
	
	</g:each> 
</body>

