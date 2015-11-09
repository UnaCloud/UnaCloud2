<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            Edit User
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-dashboard"></i> Home</a></li>
            <li><a href="${createLink(uri: '/admin/user/list', absolute: true)}"><i class="fa fa-users"></i> Users</a></li>
            <li class="active">Edit User</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">    		
             <div class="col-xs-12">   
             		<g:render template="/share/message"/>
             		<div id="label-message"></div>               		     
             		<div class="box box-primary">     	
             			<div class="box-header">
             				<h5 class="box-title">Edit a selected user</h5> 							    			
             			</div>		
	                 	<!-- form start -->
	                 	<form method="POST" id="form-edit" action="${createLink(uri: '/admin/user/edit/save', absolute: true)}"  role="form">
	                 		<input name="id" type="hidden" value="${user.id}"/>  
	                     	<div class="box-body">	 
	                     		<div class="form-group">
	                            	<label>Full name</label>
	                            	<input type="text" class="form-control" name="name" value="${user.name}" placeholder="User fullname">
	                         	</div>
	                         	<div class="form-group">
	                            	<label>Username</label>
	                            	<input type="text" class="form-control" name="username" value="${user.username}" placeholder="Username">
	                         	</div>
	                         	<div class="form-group">
	                            	<label>Password</label>
	                            	<p class="help-block">* If password field left empty, the user password won't be modified.</p> 
	                            	<input type="password" class="form-control" name="passwd">
	                         	</div>
	                         	<div class="form-group">
	                            	<label>Confirm Password</label>
	                            	<input type="password" class="form-control" name="cpasswd">
	                         	</div>
	                         	<div class="form-group">
	                            	<label>Description</label>
	                            	<input type="text" class="form-control" name="description" value="${user.description}" placeholder="Researcher, Teacher, Administrator, ...">
	                         	</div>
		                     </div><!-- /.box-body -->
		                     <div class="box-footer"> 
		                        <g:submitButton name="button-submit" class="btn btn-success" value="Submit" />
		           			 	<a class="btn btn-danger" href="${createLink(uri: '/admin/user/list', absolute: true)}" >Cancel</a>		
		                     </div>
	                	 </form>
	             	</div>
             </div><!-- /.box -->            
        </div>     	
	</section><!-- /.content -->     
</body>