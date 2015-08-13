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
	<h3>Agent Version</h3>
		<g:link action="updateAgentVersion" controller="unaCloudServices">
			<button class="btn">Update Agent Version</button>
		</g:link>
	</div>
</body>
</html>