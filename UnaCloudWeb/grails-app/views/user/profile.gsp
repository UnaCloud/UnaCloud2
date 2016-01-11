<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            My Profile
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li class="active">Profile</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">
    		<div class="col-lg-6 col-sm-6 col-xs-12 pull-right">  
    			<div class="box box-primary"> 
    				<div class="box-body">
    					<div class="form-group">  
    						<p class="help-block">Change your password.</p>			
    	 					<a href="${createLink(uri: '/user/profile/change/', absolute: true)}" type="submit" class="btn btn-primary">Change password</a>  
    	 				</div> 	
    	 			</div> 	
    	 		</div> 	
    	 	</div> 	
            <div class="col-lg-6 col-sm-6 col-xs-12">   
           		<g:render template="/share/message"/>            		     
           		<div class="box box-primary">  
                 	<form method="POST" id="form-edit-profile" action="${createLink(uri: '/user/profile/save', absolute: true)}"  role="form">
                 		<input name="id" type="hidden" value="${user.id}"/>  
                     	<div class="box-body">	 
                     		<p class="help-block">You are member since ${user.registerDate}</p> 
                     		<div class="form-group">
                            	<label>Full name</label>
                            	<input type="text" class="form-control var-0" name="name" value="${user.name}" placeholder="User fullname" disabled>
                         	</div>
                         	<div class="form-group">
                            	<label>Username</label>
                            	<input type="text" class="form-control var-0" name="username" value="${user.username}" placeholder="Username" disabled>
                         	</div>
                         	<div class="form-group">
                            	<label>Email</label>
                            	<input type="text" class="form-control var-0" name="email" value="${user.email}" placeholder="email" disabled>
                         	</div>	                         	
                         	<div class="form-group">
                            	<label>Description</label>
                            	<input type="text" class="form-control var-0" name="description" value="${user.description}" placeholder="Researcher, Teacher, Administrator, ..." disabled>
                         	</div>
	                     </div><!-- /.box-body -->
	                     <div id="var-0" class="box-footer"> 
	                        <input name="button" type="button" class="btn-variable btn btn-default" value="Edit">
	                 		<input name="button-submit" type="submit" class="btn-submit btn btn-success hide-segment" value="Submit">
	                 		<input name="button-cancel" type="button" class="btn-cancel btn btn-danger hide-segment" value="Cancel">
	                     </div>
                	 </form>
            	 </div>
             </div><!-- /.box -->            
        </div>  
	</section><!-- /.content -->     
</body>