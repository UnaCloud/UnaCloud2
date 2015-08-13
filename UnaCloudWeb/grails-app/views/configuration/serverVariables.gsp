<%@page import="unacloud2.ExternalCloudTypeEnum"%>
<%@page import="unacloud2.ExternalCloudAccount"%>
<%@page import="unacloud2.ExternalCloudProvider"%>
<%@page import="back.pmallocators.AllocatorEnum"%>
<%@page import="unacloud2.ServerVariable"%>
<html>
<html>
<head>
<meta name="layout" content="main" />
<r:require modules="bootstrap" />
</head>
<body>
	<div class="hero-unit span9">
	<h3>Server Variables</h3>
		<g:form name="instancesDeploy" class="form-horizontal"
			controller="unaCloudServices" action="changeServerVariables">
			<g:each in="${ServerVariable.all}" status="i" var="serverVariable">
				<g:if test="${!(serverVariable.name=='AGENT_VERSION')}">
					<div class="control-group">
						<label style="width: 250px; padding-right: 20px;"
							class="control-label"> ${serverVariable.name}
						</label>
						<div class="controls" id="data">
							<g:if test="${!(serverVariable.name=='VM_ALLOCATOR_NAME')&& !(serverVariable.name=='EXTERNAL_STORAGE_ACCOUNT')&&!(serverVariable.name=='EXTERNAL_COMPUTING_ACCOUNT')}">
								<input name="${serverVariable.name}" id="${serverVariable.name}"
									type="text" value="${serverVariable.variable}">
							</g:if>
							<g:if test="${serverVariable.name=='VM_ALLOCATOR_NAME'}">
								<select name="${ serverVariable.name}">
									<g:each in="${AllocatorEnum.values()}" status="j" var="var">
										<option value="${var}" ${(var.toString() == serverVariable.variable)?'selected':''} >
											${var.getName() }
										</option>
									</g:each>
								</select>
							</g:if>
							<g:if test="${serverVariable.name=='EXTERNAL_STORAGE_ACCOUNT'}">
								<select name="${ serverVariable.name}">
									<option value="None">None</option>
									<g:each in="${ExternalCloudAccount.list()}" status="j" var="var">
										<g:if test="${var.provider.type.equals(ExternalCloudTypeEnum.STORAGE) }">
										<option value="${var.getName()}" ${(var.getName().equals(serverVariable.variable))?'selected':''} >
											${var.getName() }
										</option>
										</g:if>
									</g:each>
								</select>
							</g:if>
							<g:if test="${serverVariable.name=='EXTERNAL_COMPUTING_ACCOUNT'}">
								<select name="${ serverVariable.name}">
									<option value="None">None</option>	
									<g:each in="${ExternalCloudAccount.list()}" status="j" var="var">
										<g:if test="${var.provider.type.equals(ExternalCloudTypeEnum.COMPUTING) }">
										<option value="${var.getName()}" ${(var.getName().equals(serverVariable.variable))?'selected':''} >
											${var.getName() }
										</option>
										</g:if>
									</g:each>
								</select>
							</g:if>
						</div>
					</div>

				</g:if>
			</g:each>
			<div class="controls">
				<g:submitButton name="changeServerVariables" class="btn"
					value="Change Variables" />

			</div>

		</g:form>
		
	</div>
</body>
</html>