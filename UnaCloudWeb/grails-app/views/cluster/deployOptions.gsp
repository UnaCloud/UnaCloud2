<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            Configure Deployment
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li><a href="${createLink(uri: '/services/cluster/list', absolute: true)}"><i class="fa fa-th"></i> Clusters</a></li>
            <li class="active">Deploy Cluster</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">    	
    	
	    	<form id="form-new" method="post" action="${createLink(uri: '/services/deployment/new', absolute: true)}" role="form">	
	    		 <g:render template="/share/message"/> 	    	 
		    	 <div class="col-xs-12 ">   
		    	 
			    	 <ul class="timeline">	
			    	 	<g:each status="ind" in="${resources}" var="platform">					
					   	<li>
					        <!-- timeline icon -->
					        <g:if test="${ind == 0}">
							     <i id="time-icon${ind}" class="fa fa-adjust bg-blue"></i>
							</g:if>
							<g:else>
							     <i id="time-icon${ind}" class="fa fa-circle-o bg-gray"></i>
							</g:else>
					        <div class="timeline-item time-title${ind} <g:if test="${ind == 0}"> hidden </g:if>">					
					            <h3 class="timeline-header"><a >${platform.name}</a></h3>		
					        </div>
					        <div class="timeline-item time-element${ind} <g:if test="${ind != 0}">hidden</g:if>">					
					            <h3 class="timeline-header"><a >${platform.name}</a></h3>					
					            <div class="timeline-body">
					                <div class="col-xs-12">
					                	<div class="col-lg-5 col-sm-6 col-xs-12 pull-right">  
							    			<div class="box box-solid"> 
							    				<div class="box-header">
							             			<h5 class="box-title">Available Resources</h5>            									    			
							             		</div>      	
							    				<div class="box-body">
							    					<p class="help-block"><strong>MAXIMUN INSTANCES</strong> to deploy per hardware profile.</p>
							    					<label>Opportunistic</label>
							    					<div>
							    					<g:each in="${platform.quantities}" var="val">
								    					<div class="box-info <g:if test="${val.quantity>0}"> bg-green</g:if>">
								                        	<h3><g:formatNumber number="${val.quantity}" format="0" /></h3>${val.name}	                        	
								                        </div>
							                        </g:each>
							                        </div>                        
							                        <label>High Availability</label>
							                        <div>
							                        <g:each in="${platform.quantitiesAvailable}" var="val">
								    					<div class="box-info<g:if test="${val.quantity>0}"> bg-green</g:if>">	    					
								                           <h3><g:formatNumber number="${val.quantity}" format="0" /></h3>${val.name}
								                        </div>
							                        </g:each>
							                        </div>
							    	 			</div> 	
							    	 		</div> 	
							    	 	</div> 
							    	 	<div class="col-lg-7 col-sm-6 col-xs-12">		    	 					     		 
							    	 		<div class="box box-solid">				    	 		    	 			
						    	 	   			<g:each in="${platform.images}" var="image">  
						            			<div class="box-header">
						            				<h5 class="box-title">Image: ${image.name}</h5> 	           								    			
						            			</div>	
						            			            					
						                 		<!-- form start -->
						                 		<div class="box-body">       
								                    <div class="form-group">
							                    		<label>Hardware Profile</label>	   
							                        	<select name= "option_hw_${image.id}" class="form-control">
							                       		<g:each in="${hardwareProfiles}" var="hwdp">
											  				<option value="${hwdp.id}">${hwdp.name}</option>
											  			</g:each>
							                			</select>
								                	</div>
								                	<div class="form-group">
								                		<label>Instances to deploy</label>
								                    	<input type="text" class="form-control" name="instances_${image.id}" placeholder="Number of executions i.e. 1, 3, 15">
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
							                	<g:if test="${platform.images.size()>1}"><hr></g:if>
						            			</g:each>        			 
							        		</div>
										</div>
					                </div>
					            </div>					
					            <div class='timeline-footer'>
					                <g:if test="${ind != 0}"><a class="btn btn-default btn-time-bck" data-element="${ind-1}">Back</a></g:if>
					                <a class="btn btn-primary btn-time-nxt" data-element="${ind+1}">Next</a>					                
						           	<a class="btn btn-danger pull-right" href="${createLink(uri: '/services/cluster/list', absolute: true)}" >Cancel</a>	
					            </div>
					        </div>
					    </li>
					  	</g:each>
					  	<li>
					        <!-- timeline icon -->
					        <i id="time-icon${resources.size()}" class="fa fa-circle-o bg-gray"></i>
					        <div class="timeline-item time-title${resources.size()}">					
					            <h3 class="timeline-header"><a >Execute deployment</a></h3>		
					        </div>
					        <div class="timeline-item time-element${resources.size()} hidden">				
					            <h3 class="timeline-header"><a >Execute deployment</a></h3>					
					            <div class="timeline-body ">
					                <div class="col-xs-12">
										<div class="col-lg-6 col-sm-6 col-xs-12">           			 
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
									</div>   
					            </div>					
					            <div class='timeline-footer'>
					            	<input type=hidden name="id" value="${clusterid}"> 
					            	<a class="btn btn-default btn-time-bck" data-element="${resources.size()-1}">Back</a>
					             	<g:submitButton name="button-submit" class="btn btn-success" value="Submit" />	
						           	<a class="btn btn-danger pull-right" href="${createLink(uri: '/services/cluster/list', absolute: true)}" >Cancel</a>	
					            </div>
					        </div>
					    </li>
					   <li>
                           <i class="fa  fa-bolt"></i>
                       </li>
					</ul>
				</div>			   
			</form> 
        </div> 
	</section>
</body>