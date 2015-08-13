<%@page import="back.userRestrictions.UserRestrictionEnum"%>
<html>
<head>
<meta name="layout" content="main" />
<r:require modules="bootstrap" />
</head>
<body>
	<div class="hero-unit span9">
		<g:form name="userCreate" class="form-horizontal" controller="user"
			action="setPolicy">
			<div class="control-group">
				<label class="control-label">Username</label>
				<div class="controls">
					<input name="user" type="text" value="${user.username}"
						disabled>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">Restriction</label>
				<div class="controls">
					<select name="type" onChange="${remoteFunction(action:'editPerms', params:'\'username=' + user.username + '&data=\' + this.value' , update: 'data') }">
						<option value="">--Select a restriction---</option>
						<g:each in="${UserRestrictionEnum.values()}" status="j" var="var">
										<option value="${var}">
											${var.getName() }
										</option>
									</g:each>

					</select>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">Value</label>
				<div class="controls" id="data">
					<input name="value" id="value" type="text" value="">
				</div>
			</div>
			<input name="username" id="username" value="${user.username}" type="hidden">
			<div class="controls">
				<g:submitButton name="setPolicy" class="btn" value="Finish" />
			</div>

		</g:form>
	</div>
</body>
</html>