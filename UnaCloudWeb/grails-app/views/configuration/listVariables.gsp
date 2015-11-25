<%@page import="unacloud.enums.ServerVariableTypeEnum"%>
<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            Server Variables
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li class="active">List of server variables</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">    		
             <div class="col-xs-12">   
            		<g:render template="/share/message"/>
            		<div id="label-message"></div>               		     
            		<div class="box box-primary">     	
            			<div class="box-header">
            				<h5 class="box-title">Set the server variables</h5>             										    			
            			</div>
            			<p class="help-block">To modify a variable in the list, click in "Edit" button, modify value and then click "submit".</p>	
	            		<hr>	
	                 	<!-- form start -->
	                 	<div class="box-body">	 
	                 	<g:each in="${variables}" var="variable">
	                 		<form action="${createLink(uri: '/config/variables/set', absolute: true)}" method="post" role="form">	
	                 			<input name="id" type="hidden" value="${variable.id}"/>    
	                 			<div class="row">            			
	               					<label class="col-lg-3 col-sm-3 col-xs-12">${variable.name}</label>               				
		               				<div class="col-lg-6 col-sm-6 col-xs-12">	
			               				<div class="form-group">
			                 		    <g:if test="${variable.isList()}">
			                 		    	<select name= "value" class="form-control var-${variable.id}" disabled>		                 		    	
				                 		    	<option value="NoOne" >-- No one --</option>           		    	
			                                <g:each in="${variable.values()}" var="val"> 
			                                	<g:if test="${variable.variable}">
			                                    <option value="${val}" <g:if test="${variable.variable.equals(val)}"> selected</g:if>>${val}</option>                                                
			                                	</g:if>
			           		    				<g:else>
			           		    		 		<option value="${val}">${val}</option>                                              
			           		    				</g:else>
			                                </g:each>    
			                                </select>                
			                 		    </g:if>
			                 		    <g:else>
			                 		        <g:if test="${variable.serverVariableType.equals(ServerVariableTypeEnum.BOOLEAN)}">
			                 		            <g:checkBox name="value" value="${variable.variable.equals('true')?true:false}" class="check-blue var-${variable.id}" DISABLED="true"/> enabled?
			                 		        </g:if>
			                 		        <g:else>
				                 		    	<g:if test="${variable.variable}">
				                 		    	<input type="text" class="form-control var-${variable.id}" name="value" value="${variable.variable}" placeholder="Without value" disabled>
				                 		    	</g:if>
				                 		    	<g:else>
				                 		    	<input type="text" class="form-control var-${variable.id}" name="value" placeholder="Without value" disabled>
				                 		    	</g:else>	
			                 		    	</g:else>	                            	
			                 		    </g:else>
			                 		    </div>
		                 		    </div>
		                 			<div class="col-lg-3 col-sm-3 col-xs-12">
		                 				<div id="var-${variable.id}" class="form-group">
		                 					<input name="button" type="button" class="btn-variable btn btn-default" value="Edit">
		                 					<input name="button-submit" type="submit" class="btn-submit btn btn-success hide-segment" value="Submit">
		                 					<input name="button-cancel" type="button" class="btn-cancel btn btn-danger hide-segment" value="Cancel">
		                				</div>	
		                			</div>
	                			</div>
	                 		</form>
	                 	</g:each>
	                 	</div>
             		</div>
             </div><!-- /.box -->            
        </div>     	
	</section><!-- /.content -->     
</body>