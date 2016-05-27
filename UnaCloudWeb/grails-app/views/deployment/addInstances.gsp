<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            Add Instances to Deployed Image ${image.image.name}
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li><a href="${createLink(uri: '/services/deployment/list', absolute: true)}"><i class="fa fa-rocket"></i> Deployments</a></li>
            <li class="active">Add instances</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">       		 		
    		<div class="col-lg-6 col-sm-6 col-xs-12 pull-right">  
    			<div class="box box-primary"> 
    				<div class="box-header">
             			<h5 class="box-title">Available Deployments</h5>            									    			
             		</div>            		
    				<div class="box-body">
    					<p class="help-block"><strong>MAXIMUN INSTANCES</strong> to deploy.</p>
    					<div>
    					<g:each in="${quantities}" var="val">
	    					<div class="box-info <g:if test="${val.quantity>0}"> bg-green</g:if>">
	                        	<h3><g:formatNumber number="${val.quantity}" format="0" /></h3>${val.name}	                        	
	                        </div>
                        </g:each>
                        </div>     
    	 			</div> 	
    	 		</div> 	
    	 	</div> 	
    	 	<div class="col-lg-6 col-sm-6 col-xs-12"> 
    	 		<g:render template="/share/message"/>  
    	 		<div class="box box-primary">       
    	 			<form id="form-new" method="post" action="${createLink(uri: '/services/deployment/'+image.id+'/add/save', absolute: true)}" role="form"> 
    	 				<input type=hidden name="id" value="${image.id}"> 
            			<div class="box-header">
            				<h5 class="box-title">${image.image.name}</h5><br>            									    			
            			</div>		
                 		<!-- form start -->
                 		<div class="box-body">
                 			<p class="help-block">Currently you have <strong>${image.getActiveExecutions().size()} ACTIVE EXECUTIONS</strong> in this deployed image</p> 	
		                	<div class="form-group">
		                		<label>Instances to deploy</label>
		                    	<input type="text" class="form-control" name="instances_${image.id}" placeholder="Number of deployments. i.e. 1, 3, 15">
		                    </div>	  
            				<div class="form-group">
		                		<label>Execution Time</label>
		                    	<select name= "time" class="form-control">
		                    		<option value="${1}">1 hour</option>
									<option value="${2}">2 hours</option>
									<option value="${4}">4 hours</option>
									<option value="${12}">12 hours</option>
									<option value="${24}">1 day</option>
									<option value="${90*24}">90 days</option>
		                    	</select>
		                    </div> 
            			</div>
            	        <div class="box-footer"> 			
	             			<g:submitButton name="button-submit" class="btn btn-success" value="Submit" />	
		           			<a class="btn btn-danger" href="${createLink(uri: '/services/deployment/list', absolute: true)}" >Cancel</a>	      
		           		</div>
        			</form>  
        		</div>
			</div>	      
        </div> 
	</section>
</body>