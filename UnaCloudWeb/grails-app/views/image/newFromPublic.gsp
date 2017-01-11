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
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li><a href="${createLink(uri: '/services/image/list', absolute: true)}"><i class="fa fa-th-list"></i> Images</a></li>
            <li><a href="${createLink(uri: '/services/image/new', absolute: true)}"><i class="fa fa-file"></i> New Image</a></li>
            <li class="active">Public</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">
    		<div class="col-lg-6 col-sm-6 col-xs-12">      			
    			<div id="label-message"></div>     
    			<g:render template="/share/message"/>  
    			<form id="form-create" name="imageNewPublic" action="${createLink(uri: '/services/image/public/copy', absolute: true)}" enctype="multipart/form-data" role="form">
	                        		     
		       		<div class="box box-primary">     
		            	<!-- form start -->
		            	 <div class="box-header">
		            	 	<h5 class="box-title">Select the image to copy</h5>
		            	 </div>
		           	 	 <div class="box-body"> 
		                   	 <div class="form-group">
		                         <div class="table-responsive">
	                         	 	<div class="form-group">
	                         	 		<p class="help-block">* Operating System - Image Name - Access protocol</p>
                                        <select name= "image" class="form-control">
                                        <g:each in="${publicImages}" var="image"> 
                                        	<option value="0">--Select a Public Image--</option>
                                            <option value="${image.id}">${image.operatingSystem.name} - ${image.name} - ${image.accessProtocol}</option>                                                
                                        </g:each>    
                                        </select>                                            
                                 	</div>
                                 </div>
		           			 </div>  
		           	 	 	<div class="form-group">
		                       <label>New Image Name</label>
		                       <p class="help-block">Write a new name for your copy</p>
		                       <input type="text" class="form-control" name="name" placeholder="Image Name">
		                    </div>  
		           		</div>	
		           		<div class="box-footer"> 
		           			<g:submitButton name="button-submit" class="btn btn-success" value="Submit" />	
		           			 <a class="btn btn-danger" href="${createLink(uri: '/services/image/new', absolute: true)}" >Cancel</a>	
		                </div>	  		
    				</div>   
	       	 	</form>
    		</div>			
    	</div>   	
	</section><!-- /.content -->     
</body>