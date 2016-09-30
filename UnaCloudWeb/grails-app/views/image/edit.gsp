<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            Edit Image
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li><a href="${createLink(uri: '/services/image/list', absolute: true)}"><i class="fa fa-th-list"></i> Images</a></li>
            <li class="active">Edit Image</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">
             <div class="col-lg-6 col-sm-6 col-xs-12">   
             		<g:render template="/share/message"/>
             		<div id="label-message"></div>            		     
             		<div class="box box-primary">     	
             			<div class="box-header">
             				<h5 class="box-title">Edit your image</h5> 							    			
             			</div>		
	                 	<!-- form start -->
	                 	<form id="form-edit" action="${createLink(uri: '/services/image/edit/save', absolute: true)}" role="form">
	                     	<div class="box-body">	
	                     		 <input name="id" type="hidden" value="${image.id}"/>                     		
             					 <p class="help-block">Set basic attributes from your image.</p> 
	                        	 <div class="form-group">
	                             	<label>Image Name</label>
	                            	<input type="text" class="form-control" name="name" placeholder="Image Name" value="${image.name }">
	                         	 </div>
		                         <div class="form-group">
		                             <label>User</label>
		                             <input type="text" class="form-control" name="user" placeholder="Username" value="${image.user }">
		                         </div>
		                         <div class="form-group">
		                             <label>Password</label>
		                             <input type="password" class="form-control" name="passwd" placeholder="Password">
		                         </div>  
		                         <div class="form-group">
		                             <label><g:checkBox id="check_public" name="isPublic" value="${image.isPublic?true:false}" class="check-blue"/> Public?</label>
		                         </div>
		                     </div><!-- /.box-body -->
		                     <div class="box-footer"> 			
		                         <button type="submit" id="button-submit" class="btn btn-success" style="cursor:pointer">Submit</button>		                         
		           			 	 <a class="btn btn-danger" href="${createLink(uri: '/services/image/list', absolute: true)}" >Cancel</a>	
		                     </div>
	                	 </form>
	             	</div>
             </div><!-- /.box -->            
        </div>     	
	</section><!-- /.content -->     
    <script>$(document).ready(function(){editImage();});</script>
</body>