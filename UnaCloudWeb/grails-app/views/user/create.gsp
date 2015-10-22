<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            New User
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-dashboard"></i> Home</a></li>
            <li><a href="${createLink(uri: '/admin/user/list', absolute: true)}"><i class="fa fa-users"></i> Users</a></li>
            <li class="active">New User</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">    		
             <div class="col-xs-12">   
             		<div id="label-message"></div>               		     
             		<div class="box box-primary">     	
             			<div class="box-header">
             				<h5 class="box-title">Create a new user</h5> 							    			
             			</div>		
	                 	<!-- form start -->
	                 	<form id="form-new" action="${createLink(uri: '/admin/user/save', absolute: true)}" enctype="multipart/form-data" role="form">
	                     	<div class="box-body">	                     		
	                        	<div class="form-group">
	                            	<label>Full name</label>
	                            	<input type="text" class="form-control" name="name" placeholder="User fullname">
	                         	</div>
	                         	<div class="form-group">
	                            	<label>Username</label>
	                            	<input type="text" class="form-control" name="username" placeholder="Username">
	                         	</div>
	                         	<div class="form-group">
	                            	<label>Password</label>
	                            	<input type="password" class="form-control" name="passwd">
	                         	</div>
	                         	<div class="form-group">
	                            	<label>Description</label>
	                            	<input type="text" class="form-control" name="description" placeholder="Researcher, Teacher, Administrator, ...">
	                         	</div>
		                     </div><!-- /.box-body -->
		                     <div class="box-footer"> 			
		                        <g:submitButton name="button-submit" class="btn btn-primary" value="Submit" />	
		                     </div>
	                	 </form>
	             	</div>
             </div><!-- /.box -->            
        </div>     	
	</section><!-- /.content -->     
	<asset:javascript src="pages/images.js" />
    <script>$(document).ready(function(){newUploadImage();});</script>
</body>