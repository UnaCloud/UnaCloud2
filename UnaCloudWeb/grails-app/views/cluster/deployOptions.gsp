<html>
<head>
<meta name="layout" content="main" />
<r:require modules="bootstrap" />
</head>
<body>
	<div class="hero-unit span9">
	<g:link controller="cluster" action="index" style="display: -webkit-box;"><i class="icon-chevron-left" title="Back"></i><h5 style="margin: 3px;">Back to Cluster list</h5></g:link><br>
   	
		<g:form name="clusterDeploy" class="form-horizontal"
			controller="deployment" >
			<input type=hidden name="id" value="${cluster.id}">
			<div id="remaining" class="alert alert-info">
				<table>
					<tr>
						<td rowspan="3"><i class="icon-exclamation-sign"></i>&nbsp;&nbsp;&nbsp;</td>
						<td><label class="info">Remaining Common Instances: <strong>${limit}</strong> <small>(Physical Machines)</small></label></td>
					</tr>
					<tr>
						<td><label> Remaining High Availability Instances: <strong>${limitHA}</strong> <small>(Physical Machines)</small></label></td>
					</tr>
					<g:if test="${limit==0 && account!= null}">
						<tr>
							<td><label> There are no remaining local physical machines, but you can deploy on ${account.provider.name } </label></td>
						</tr> 
					</g:if>
				</table>
			</div>			
			<g:if test="${flash.message && flash.message!=""}">
			    <br>
				<div class="alert alert-error">
					<i class="icon-exclamation-sign"></i>&nbsp;&nbsp;&nbsp;${flash.message }
				</div>
				<br>
			</g:if>
			
			<g:each in="${cluster.images}" status="i" var="image">
				<div class="control-group">
					<h5>
						${image.name}
					</h5>
				</div>
				<table border="0" cellpadding="10"">
					<tr>
						<td><label>Instances to deploy</label></td>
						<td><input name="instances" class="input-medium" type="text"></td>
						<td><label style="font-size: 18px"><span> <= </span><strong id="max-${image.id}">${max}</strong></label></td>					
					</tr>
					<tr>
						<td><label>Hardware Profile</label></td>
						<td><select id="option-hw" data-img="max-${image.id}" name="hardwareProfile" class="input-medium">
								<g:each in="${hardwareProfiles}" status="j" var="hp">
								<option value="${hp.id}">
									${hp.name}
								</option>
								</g:each>
							</select>
						</td>						
					</tr>
					<g:if test="${limitHA > 0}">
						<tr>
							<td><label>High Availability</label></td>
							<td><input type="checkbox" name="highAvailability${image.id.toString()}"></td>
						</tr>
					</g:if>			
					
					<tr>
						<td><label>Hostname</label></td>
						<td colspan="2"><input type="text" class="input-medium" name="hostname" value="${image.name}"></td>
					</tr>
				</table>
				<br>
			</g:each>
			<div class="control-group">
				<label class="control-label">Execution Time</label>
				<div class="controls">
					<select name="time" class="input-small">
						<option value="${1}">1 hour</option>
						<option value="${2}">2 hours</option>
						<option value="${4}">4 hours</option>
						<option value="${12}">12 hours</option>
						<option value="${24}">1 day</option>
						<option value="${90*24}">90 days</option>
					</select>
				</div>
			</div>
			<g:if test="${limit > 0}">
				<div class="controls">
					<g:actionSubmit name="deploy" class="btn" value="Deploy" action="deploy"/>
				</div>
			</g:if>
		</g:form>
		
	</div>
	
<g:javascript src="cluster.js" />
 <script>$(document).ready(function(){calculateDeploy();});</script>
</body>
</html>