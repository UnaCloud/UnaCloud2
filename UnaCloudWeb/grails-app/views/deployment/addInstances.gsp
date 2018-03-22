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
    		<form id="form-new" method="post" action="${createLink(uri: '/services/deployment/'+image.id+'/add/save', absolute: true)}" role="form"> 
	    		 <g:render template="/share/message"/> 	    	 
		    	 <div class="col-xs-12 ">   		    	 
			    	 <ul class="timeline">						
					   	<li>
					        <!-- timeline icon -->
							<i id="time-icon" class="fa fa-adjust bg-blue"></i>
					        <div class="timeline-item time-element">					
					            <h3 class="timeline-header"><a >${image.image.platform.name}</a></h3>					
					            <div class="timeline-body">
					                <div class="col-xs-12">
					                	<div class="col-lg-5 col-sm-6 col-xs-12 pull-right">  
							    			<div class="box box-solid"> 
							    				<div class="box-header">
							             			<h5 class="box-title">Available Resources</h5>            									    			
							             		</div>      	
							    				<div class="box-body">
							    					<p class="help-block"><strong>MAXIMUN INSTANCES</strong> to deploy.</p>
							    					<g:if test="${image.highAvaliavility!=true}"><label>Opportunistic</label></g:if><g:else>High Availability</g:else>
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
							    	 	<div class="col-lg-7 col-sm-6 col-xs-12">		    	 					     		 
							    	 		<div class="box box-solid">				    	 
						            			<div class="box-header">
						            				<h5 class="box-title">Image: ${image.image.name}</h5> 	           								    			
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
							                	</div><!-- /.box-body -->  			 
							        		</div>
										</div>
					                </div>
					            </div>					
					            <div class='timeline-footer'>
					                <g:submitButton name="button-submit" class="btn btn-success" value="Submit" />	
		           					<a class="btn btn-danger" href="${createLink(uri: '/services/deployment/list', absolute: true)}" >Cancel</a>	
					            </div>
					        </div>
					   </li>					  	
					   <li>
                           <i class="fa fa-bolt"></i>
                       </li>
					</ul>
				</div>			   
			</form>     	 	
        </div> 
	</section>
</body>