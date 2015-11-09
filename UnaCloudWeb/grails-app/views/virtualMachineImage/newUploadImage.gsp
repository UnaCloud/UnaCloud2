<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            New Image
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-dashboard"></i> Home</a></li>
            <li><a href="${createLink(uri: '/services/image/list', absolute: true)}"><i class="fa fa-th-list"></i> Images</a></li>
            <li class="active">New Image</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">
    		<div class="col-lg-6 col-sm-12 col-xs-12 pull-right">  
    			<div class="box box-primary"> 
    				<div class="box-body">
    					<div class="form-group">  
    						<p class="help-block">Select a public image from a list of images uploaded by other users.</p>			
    	 					<a href="${createLink(uri: '/services/image/public', absolute: true)}" type="submit" class="btn btn-primary">Use a public image</a>  
    	 				</div> 	
    	 			</div> 	
    	 		</div> 	
    	 	</div> 	
             <div class="col-lg-6 col-sm-12 col-xs-12">   
             		<div id="label-message"></div>               		     
             		<div class="box box-primary">     	
             			<div class="box-header">
             				<h5 class="box-title">Upload your image</h5> 							    			
             			</div>		
	                 	<!-- form start -->
	                 	<form id="form-new" action="${createLink(uri: '/services/image/upload', absolute: true)}" enctype="multipart/form-data" role="form">
	                     	<div class="box-body">	                     		
             					 <p class="help-block">Create, configure and upload your own image to system.</p> 
	                        	 <div class="form-group">
	                             	<label>Image Name</label>
	                            	<input type="text" class="form-control" name="name" placeholder="Image Name">
	                         	 </div>
	                        	 <div class="form-group">
		                             <label>Select</label>
		                             <select name= "osId" class="form-control">
		                             	<g:each in="${oss}" status="i" var="os">
						  					<option value="${os.id}">${os.name}</option>
						  				</g:each>
		                             </select>
		                         </div>
		                          <div class="form-group">
		                             <label>Communication protocol</label>
		                             <input type="text" class="form-control" name="protocol" placeholder="SSH, RCP">
		                         </div>
		                         <div class="form-group">
		                             <label>User</label>
		                             <input type="text" class="form-control" name="user" placeholder="Username">
		                         </div>
		                         <div class="form-group">
		                             <label>Password</label>
		                             <input type="password" class="form-control" name="passwd" placeholder="Password">
		                         </div>                    
		                         <div class="form-group">
		                             <label>Image File input</label>
		                             <input id="files" type="file" name="files" multiple>
		                         </div>
		                         <div class="form-group">
		                             <label>
		                             	<input type="checkbox" name="isPublic"  class="check-blue"/>Public?
		                             </label>
		                             <p class="help-block">All users will be able to create a copy from your image.</p>
		                         </div>
		                     </div><!-- /.box-body -->
		                     <div class="box-footer"> 			
		                         <a id="button-submit" class="btn btn-success" style="cursor:pointer">Submit</a>
		                         <a class="btn btn-danger" href="${createLink(uri: '/services/image/list', absolute: true)}" >Cancel</a>	      
		                     </div>
	                	 </form>
	             	</div>
             </div><!-- /.box -->            
        </div>     	
	</section><!-- /.content -->     
    <script>$(document).ready(function(){newUploadImage();});</script>
</body>