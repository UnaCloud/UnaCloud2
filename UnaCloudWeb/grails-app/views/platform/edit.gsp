<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            Edit Platform
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li><a href="${createLink(uri: '/admin/platform/list', absolute: true)}"><i class="fa fa-star"></i> Platforms</a></li>
            <li class="active">Edit Platform</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">    		
             <div class="col-lg-6 col-sm-6 col-xs-12">   
             		<g:render template="/share/message"/>
             		<div id="label-message"></div>               		     
             		<div class="box box-primary">     	
             			<div class="box-header">
             				<h5 class="box-title">Edit a selected platform</h5> 							    			
             			</div>		
	                 	<!-- form start -->
	                 	<form method="post" id="form-new" action="${createLink(uri: '/admin/platform/edit/save', absolute: true)}" role="form">
	                     	<div class="box-body">	                     		
	                        	<div class="form-group">
	                            	<label>Platform name</label>
	                            	<input name="id" type="hidden" value="${platform.id}">
	                            	<input type="text" class="form-control" value="${platform.name}" name="name" placeholder="Platform name">
	                         	</div>
	                         	<div class="form-group">
	                            	<label>Version</label>
	                            	<input type="text" class="form-control" value="${platform.platformVersion}" name="vers" placeholder="Platform version">
	                         	</div>
	                         	<div class="form-group">
	                            	<label>Version</label>
	                            	<input type="text" class="form-control" value="${platform.classPlatform}" name="cls" placeholder="Platform class in agent">
	                         	</div>
	                         	<div class="form-group">
	                            	<label>Main file extension *</label>
	                            	<input type="text" class="form-control" value="${platform.mainExtension}"  name="ext" placeholder="Platform main file extension">
	                         	</div>
	                         	<div class="form-group">
	                            	<label>Other file extensions</label>
	                            	<p class="help-block">Allowed file extension list for this platform separated by comma (.vbox,.vdi,...), but include main file.</p> 
	                            	<input type="text" class="form-control" value="${platform.filesExtensions}"  name="files_ext" placeholder="Platform file extension list">
	                         	</div>
	                         </div><!-- /.box-body -->
		                     <div class="box-footer"> 			
		                        <g:submitButton name="button-submit" class="btn btn-success" value="Submit" />		
		           				<a class="btn btn-danger" href="${createLink(uri: '/admin/platform/list', absolute: true)}" >Cancel</a>
		                     </div>
	                	 </form>
	             	</div>
             </div><!-- /.box -->            
        </div>     	
	</section><!-- /.content -->     
</body>