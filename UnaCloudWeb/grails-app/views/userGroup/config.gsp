<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            Group Restrictions
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-dashboard"></i> Home</a></li>
            <li><a href="${createLink(uri: '/admin/group/list', absolute: true)}"><i class="fa fa-users"></i> Group</a></li>
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
            				<h5 class="box-title">Set the Group restrictions</h5> 							    			
            			</div>		
                 	<!-- form start -->
                 	<div class="box-body">	 
                 	<g:each status="i" in="${restrictions}" var="restriction">
                 		<form action="${createLink(uri: '/admin/group/restrictions/set', absolute: true)}" role="form">	
                 			<input name="id" type="hidden" value="${group}"/>       
                 			<input name="restriction" type="hidden" value="${restriction.type}"/>               			
               				<label class="col-lg-12">${restriction.name}</label>
               				<div class="row">
	               				<div class="col-lg-8 col-sm-8 col-xs-12">	
	               				<div class="form-group">
	                 		    <g:if test="${restriction.list}">
	                 		    	<g:if test="${restriction.multiple}">
	                 		    	<select name= "value" class="form-control var-${i}" disabled multiple>
	                 		    	</g:if>
	                 		    	<g:else>
	                 		    	<select name= "value" class="form-control var-${i}" disabled>		                 		    	
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
	                 		    	<input type="text" class="form-control" name="value var-${i}" disabled value="${restriction.current.value}" placeholder="Without restriction">
	                 		    	</g:if>
	                 		    	<g:else>
	                 		    	<input type="text" class="form-control" name="value var-${i}" disabled placeholder="Without restriction">
	                 		    	</g:else>		                            	
	                 		    </g:else>
	                 		    </div>
	                 		    </div>
	                 			<div class="col-lg-4 col-sm-4 col-xs-12">
	                 				<div id="var-${i}" class="form-group">
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