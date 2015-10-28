<g:if test="${flash.message && flash.message!=""}">
	<g:if test="${flash.type=="success"}">
		<div class="alert alert-success"><i class="fa fa-check"></i>
	</g:if>  
	<g:elseif test="${flash.type=="info"}">
		<div class="alert alert-info"><i class="fa fa-info"></i>
	</g:elseif> 
	<g:elseif test="${flash.type=="warning"}">
		<div class="alert alert-warning"><i class="fa fa-warning"></i>
	</g:elseif>             			
	<g:else>
		<div class="alert alert-danger"><i class="fa fa-ban"></i>
	</g:else>
	<button type="button" class="close" data-dismiss="alert" aria-hidden="true">x</button>
	&nbsp;&nbsp;&nbsp;${flash.message}</div>
</g:if>             