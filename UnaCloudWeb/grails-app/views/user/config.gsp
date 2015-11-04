<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            User Restrictions
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-dashboard"></i> Home</a></li>
            <li><a href="${createLink(uri: '/admin/user/list', absolute: true)}"><i class="fa fa-users"></i> Users</a></li>
            <li class="active">User restrictions</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">    		
             <div class="col-lg-6 col-sm-6 col-xs-12">   
            		<g:render template="/share/message"/>
            		<div id="label-message"></div>               		     
            		<div class="box box-primary">     	
            			<div class="box-header">
            				<h5 class="box-title">Set the User restrictions</h5> 							    			
            			</div>		
                 	<!-- form start -->
                 	<div class="box-body">	 
                 	<g:each in="${restrictions}" var="restriction">
                 		<form action="${createLink(uri: '/admin/user/restrictions/set', absolute: true)}" role="form">	
                 			<input name="id" type="hidden" value="${user}"/>       
                 			<input name="restriction" type="hidden" value="${restriction.type}"/>               			
               				<label class="col-lg-12">${restriction.name}</label>
               				<div class="row">
	               				<div class="col-lg-9 col-sm-9">	
	               				<div class="form-group">
	                 		    <g:if test="${restriction.list}">
	                 		    	<g:if test="${restriction.multiple}">
	                 		    	<select name= "value" class="form-control" multiple>
	                 		    	</g:if>
	                 		    	<g:else>
	                 		    	<select name= "value" class="form-control">		                 		    	
		                 		    	<option value="NoOne" >-- No one --</option>    
	                 		    	</g:else>	                 		    	
	                                <g:each in="${restriction.values}" var="val"> 
	                                	<g:if test="${restriction.current}">
	                                    <option value="${val}" <g:if test="${restriction.current.value.equals(val)}"> selected</g:if>>${val}</option>                                                
	                                	</g:if>
	           		    				<g:else>
	           		    		 		<option value="${val}">${val}</option>                                              
	           		    				</g:else>
	                                </g:each>    
	                                </select>                
	                 		    </g:if>
	                 		    <g:else>
	                 		    	<g:if test="${restriction.current}">
	                 		    	<input type="text" class="form-control" name="value" value="${restriction.current.value}" placeholder="Without restriction">
	                 		    	</g:if>
	                 		    	<g:else>
	                 		    	<input type="text" class="form-control" name="value" placeholder="Without restriction">
	                 		    	</g:else>		                            	
	                 		    </g:else>
	                 		    </div>
	                 		    </div>
	                 			<div class="col-lg-3 col-sm-3">
	                 				<div class="form-group">
	                					<g:submitButton name="button-submit" class="btn btn-primary" value="Submit" />
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