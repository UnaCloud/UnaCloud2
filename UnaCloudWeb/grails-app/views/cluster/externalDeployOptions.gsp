<html>
<head>
<meta name="layout" content="main" />
<r:require modules="bootstrap" />
</head>
<body>
	<div class="hero-unit span9">
		<g:form name="clusterDeploy" class="form-horizontal"
			controller="deployment" >
			<input type=hidden name="id" value="${cluster.id}">
			<g:if test="${flash.message && flash.message!=""}">
				<div class="alert alert-error">
					<i class="icon-exclamation-sign"></i>&nbsp;&nbsp;&nbsp;${flash.message }
				</div>
			</g:if>
			<br>
			<g:each in="${cluster.images}" status="i" var="image">
				<div class="control-group">
					<h5>
						${image.name}
					</h5>
				</div>
				<table border="0" cellpadding="10"">
					<tr>
						<td><label>Instances to deploy</label></td>
						<td><label>Hardware Profile</label></td>
						
					</tr>
					<tr>
						<td><input name="instances" class="input-small" type="text">
						</td>
						<td><select name="hardwareProfile" class="input-small">
								<g:each in="${hardwareProfiles}" status="j" var="hp">
								<option value="${hp.id}">
									${hp.name}
								</option>
								</g:each>
							</select>
						</td>
						
					</tr>
				</table>
				<br>
			</g:each>
			<div class="controls">
				<g:actionSubmit name="extdeploy" class="btn" value="External Deploy" action="externalDeploy"/>
			</div>
		</g:form>
		
	</div>
</body>
</html>