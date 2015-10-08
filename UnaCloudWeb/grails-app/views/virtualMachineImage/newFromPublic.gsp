<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            New Image from Public
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-dashboard"></i> Home</a></li>
            <li><a href="${createLink(uri: '/services/image/list', absolute: true)}"><i class="fa fa-th-list"></i> Images</a></li>
            <li><a href="${createLink(uri: '/services/image/new', absolute: true)}"><i class="fa fa-file"></i> New Image</a></li>
            <li class="active">Public</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">
    		<div class="col-xs-12">  
    			<g:if test="${flash.message && flash.message!=""}">
			   		<div class="alert alert-danger"><i class="fa fa-ban"></i>&nbsp;&nbsp;&nbsp;${"Error: "+flash.message }</div>
			   	</g:if> 
    			<div id="label-message"></div>      
    			<form id="form-create" name="imageNewPublic" action="${createLink(uri: '/services/image/public/copy', absolute: true)}" enctype="multipart/form-data" role="form">
	                        		     
		       		<div class="box box-primary">     
		            	<!-- form start -->
		            	 <div class="box-header">
		            	 	<h5 class="box-title">Step 1: Select the image to copy</h5>
		            	 </div>
		           	 	 <div class="box-body"> 
		                   	 <div class="form-group">
		                         <div class="table-responsive">
			                     	  <table id="unacloudTable" class="table table-bordered table-striped">
				                          <thead>
				                              <tr>
				                             	  <th></th>
				                                  <th>Image Name</th>
				                                  <th>Operating System</th>
				                                  <th>Protocol</th>
				                              </tr>
				                          </thead>
				                          <tbody>
				                           	 <g:each in="${publicImages}" var="image"> 
				                           	 <tr>
				                           	 	<td class="column-center"><g:radio name="pImage" value="${image.id}"/></td>
				                                <td>${image.name}</td>
				                                <td>${image.operatingSystem.name}</td>
				                                <td>${image.accessProtocol}</td>				                                
					                         </tr>
				                       		 </g:each>                                       
				                          </tbody>
			                    	  </table>
		                 		</div>
		           			</div>                       
		                 </div><!-- /.box-body -->		                         	
		       	 	</div>
		       	 	          		     
		       		<div class="box box-primary">  
		            	<div class="box-header">
		            	 	<h5 class="box-title">Step 2: Write a new name for the image</h5>
		            	 </div>
		           	 	<div class="box-body">	
		           	 	 	<div class="form-group">
		                       <label>Image Name</label>
		                       <input type="text" class="form-control" name="name" placeholder="Image Name">
		                    </div>  
		           		</div>	
		           		<div class="box-footer"> 
		           			<g:submitButton name="button-submit" class="btn btn-primary" value="Submit" />		
		                </div>	  		
    				</div>   
	       	 	</form>
    		</div>			
    	</div>   	
	</section><!-- /.content -->     
	<asset:javascript src="pages/images.js" />
	<script>$(document).on('ready',function(){ $("#unacloudTable").dataTable();})</script>
</body>