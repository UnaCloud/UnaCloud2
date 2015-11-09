<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            New Operating System
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-dashboard"></i> Home</a></li>
            <li><a href="${createLink(uri: '/admin/os/list', absolute: true)}"><i class="fa fa-desktop"></i> Operating Systems</a></li>
            <li class="active">New Operating System</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">    		
             <div class="col-xs-12">   
             		<g:render template="/share/message"/>
             		<div id="label-message"></div>               		     
             		<div class="box box-primary">     	
             			<div class="box-header">
             				<h5 class="box-title">Create a new hypervisor</h5> 							    			
             			</div>		
	                 	<!-- form start -->
	                 	<form id="form-new" action="${createLink(uri: '/admin/os/save', absolute: true)}" role="form">
	                     	<div class="box-body">	                     		
	                        	<div class="form-group">
	                            	<label>Operating System name</label>
	                            	<input type="text" class="form-control" name="name" placeholder="Operating system name">
	                         	</div>
	                         	<div class="form-group">
	                            	<label>Configuration class</label>
	                            	<input type="text" class="form-control" name="configurer" placeholder="Configuration class">
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