<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            Change Password
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li><a href="${createLink(uri: '/user/profile', absolute: true)}"><i class="fa fa-user"></i> Profile</a></li>
            <li class="active">Change Password</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">    		
             <div class="col-lg-6 col-sm-6 col-xs-12">   
             		<g:render template="/share/message"/>
             		<div id="label-message"></div>               		     
             		<div class="box box-primary">     	
             			<div class="box-header">
             				<h5 class="box-title">Set a new password</h5> 							    			
             			</div>		
	                 	<!-- form start -->
	                 	<form id="form-change" action="${createLink(uri: '/user/profile/change/save', absolute: true)}" role="form">
	                     	<div class="box-body">	
	                         	<div class="form-group">
	                            	<label>Current Password</label>
	                            	<input type="password" class="form-control" name="passwd">
	                         	</div>
	                         	<div class="form-group">
	                            	<label>New Password</label>
	                            	<input type="password" class="form-control" name="newPasswd">
	                         	</div>
	                         	<div class="form-group">
	                            	<label>Confirm New Password</label>
	                            	<input type="password" class="form-control" name="confirmPasswd">
	                         	</div>
		                     </div><!-- /.box-body -->
		                     <div class="box-footer"> 			
		                        <g:submitButton name="button-submit" class="btn btn-success" value="Submit" />
		           			 	<a class="btn btn-danger" href="${createLink(uri: '/user/profile', absolute: true)}" >Cancel</a>	
		                     </div>
	                	 </form>
	             	</div>
             </div><!-- /.box -->            
        </div>     	
	</section><!-- /.content -->     
</body>