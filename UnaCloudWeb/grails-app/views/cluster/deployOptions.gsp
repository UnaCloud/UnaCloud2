<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            Configure Deployment Cluster
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li><a href="${createLink(uri: '/services/cluster/list', absolute: true)}"><i class="fa fa-th"></i> Clusters</a></li>
            <li class="active">Deploy Cluster</li>
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
    					<p class="help-block">Limit of instances to deploy that you can request by hardware profile.</p>
    					<label>Opportunistic</label>
    					<div>
    					<g:each in="${quantities}" var="val">
	    					<div class="box-info <g:if test="${val.quantity>0}"> bg-green</g:if>">
	                        	<h3><g:formatNumber number="${val.quantity}" format="0" /></h3>${val.name}	                        	
	                        </div>
                        </g:each>
                        </div>                        
                        <label>High Availability</label>
                        <div>
                        <g:each in="${quantitiesAvailable}" var="val">
	    					<div class="box-info<g:if test="${val.quantity>0}"> bg-green</g:if>">	    					
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
    	 			<form id="form-new" method="post" action="${createLink(uri: '/services/deployment/new', absolute: true)}" role="form"> 
    	 				<input type=hidden name="id" value="${cluster.id}"> 
    	 	   			<g:each in="${cluster.images}" var="image">  
            			<div class="box-header">
            				<h5 class="box-title">Image: ${image.name}</h5> 							    			
            			</div>		
                 		<!-- form start -->
                 		<div class="box-body">       
		                    <div class="form-group">
	                    		<label>Hardware Profile</label>	   
	                        	<select name= "option_hw_${image.id}" class="form-control">
	                       		<g:each in="${hardwareProfiles}" status="i" var="hwdp">
					  				<option value="${hwdp.id}">${hwdp.name}</option>
					  			</g:each>
	                			</select>
		                	</div>
		                	<div class="form-group">
		                		<label>Instances to deploy</label>
		                    	<input type="text" class="form-control" name="instances_${image.id}" placeholder="Number of deployments. i.e. 1, 3, 15">
		                    </div>  
		                    <div class="form-group">
		                		<label>Hostname</label>
		                    	<input type="text" class="form-control" name="host_${image.id}" placeholder="Name of hosts">
		                    </div> 	                    
		                    <g:if test="${high}">
		                    <div class="form-group">
								<label>High Availability</label>
								<input type="checkbox" name="highAvailability_${image.id.toString()}">
							</div>
							</g:if>
	                	</div><!-- /.box-body -->  
	                	<g:if test="${cluster.images.size()>1}"><hr></g:if>
            			</g:each>
            			<div class="box-body">   
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
		           			<a class="btn btn-danger" href="${createLink(uri: '/services/cluster/list', absolute: true)}" >Cancel</a>	      
		           		</div>
        			</form>  
        		</div>
			</div>	      
        </div> 
	</section>
</body>