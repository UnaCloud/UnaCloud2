<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
<body>
<div class="hero-unit span9">
	<h3>My Storage</h3>
	<g:if test="${endpoint!=null}" >
		<g:form>
			<table class="table table-bordered table-condensed text-center" style="background:white">
				<tr class="info">
					<td class="info" colspan="12">
				  		<!-- <a title="Update agents" class="updateMachines"><span class="icon-refresh pull-right"></span></a>   -->	  	
				  		<g:actionSubmitImage value="uploadObject" src="${resource(dir: 'images', file: 'empty.gif')}" action="uploadObject" title="Upload new file" class="icon-plus-sign"/>			  	
			  		</td>
			 	</tr>
			 	<tr>
					<th>Name</th>
				 	<th>Options</th>
			  	</tr>		 
				<g:each in="${content}" status="i" var="object">
					<g:if test="${!(object.getKey().endsWith('/'))}" >
						<tr>
							<td><g:link url="${endpoint+'/'+object.getBucketName()+'/'+ object.getKey()}"> ${object.getKey().substring(object.getKey().lastIndexOf('/') + 1)}</g:link></td>
							<td>
								<div class="row-fluid text-center">
							    	<g:link action="deleteObject" params="${[objectKey:object.getKey()]}"><i class="icon-remove-sign" title="Delete File"></i></g:link>
							    </div>
							</td>
						</tr>			
					</g:if>
				</g:each>
			</table>
		</g:form>
	</g:if>
	<g:else>
		<div class="alert alert-error">
			<i class="icon-exclamation-sign"></i>&nbsp;&nbsp;&nbsp;${flash.message }
		</div>
	</g:else>	
</div>
</body>
