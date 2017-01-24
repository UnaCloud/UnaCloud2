<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            Edit Operating System
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li><a href="${createLink(uri: '/admin/os/list', absolute: true)}"><i class="fa fa-desktop"></i> Operating Systems</a></li>
            <li class="active">Edit Operating System</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">    		
             <div class="col-lg-6 col-sm-6 col-xs-12">   
             		<g:render template="/share/message"/>
             		<div id="label-message"></div>               		     
             		<div class="box box-primary">     	
             			<div class="box-header">
             				<h5 class="box-title">Edit a selected Operating System</h5> 							    			
             			</div>		
	                 	<!-- form start -->
	                 	<form method="post" action="${createLink(uri: '/admin/os/edit/save', absolute: true)}" role="form">
	                     	<div class="box-body">	                     		
	                        	<div class="form-group">
	                            	<label>Operating System name</label>
	                            	<input name="id" type="hidden" value="${os.id}">
	                            	<input type="text" class="form-control" value="${os.name}" name="name" placeholder="Operating System name">
	                         	</div>
	                         	<div class="form-group">
	                            	<label>Configuration class</label>
	                            	<input type="text" class="form-control" value="${os.configurer}" name="configurer" placeholder="Configuration class">
	                         	</div>
	                         </div><!-- /.box-body -->
		                     <div class="box-footer"> 			
		                        <g:submitButton name="button-submit" class="btn btn-success" value="Submit" />	
		                        <a class="btn btn-danger" href="${createLink(uri: '/admin/os/list', absolute: true)}" >Cancel</a>	
		                    	
		                     </div>
	                	 </form>
	             	</div>
             </div><!-- /.box -->            
        </div>     	
	</section><!-- /.content -->     
</body>