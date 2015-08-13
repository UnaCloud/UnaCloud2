<%@page import="back.userRestrictions.UserRestrictionEnum"%>
<html>
<head>
<meta name="layout" content="main" />
<r:require modules="bootstrap" />
</head>
<body>
	<div class="hero-unit span9">
		<g:form name="userCreate" class="form-horizontal" controller="group"
			action="setPolicy">
			<div class="control-group">
				<label class="control-label">Group name</label>
				<div class="controls">
					<input name="group" type="text" value="${group.name}"
						disabled>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">Restriction</label>
				<div class="controls">
					<select name="type" >
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
			<input name="name" id="name" value="${group.name}" type="hidden">
			<div class="controls">
				<g:submitButton name="setPolicy" class="btn" value="Finish" />
			</div>

		</g:form>
	</div>
</body>
</html>