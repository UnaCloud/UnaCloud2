<!-- Creada por Carlos E. Gomez - diciembre 11 de 2015 -->
<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            New Repository
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li><a href="${createLink(uri: '/admin/repository/list', absolute: true)}"><i class="fa fa-folder-open"></i> Repository</a></li>
            <li class="active">New Repository</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">    		
             <div class="col-lg-6 col-sm-6 col-xs-12">   
             		<g:render template="/share/message"/>
             		<div id="label-message"></div>               		     
             		<div class="box box-primary">     	
             			<div class="box-header">
             				<h5 class="box-title">Create a new repository</h5> 							    			
             			</div>		
	                 	<!-- form start -->
	                 	<form id="form-new" action="${createLink(uri: '/admin/repository/save', absolute: true)}" role="form">
	                     	<div class="box-body">	                     		
	                        	<div class="form-group">
	                            	<label>Repository name</label>
	                            	<input type="text" class="form-control" name="name" placeholder="Repository name">
	                         	</div>
	                         	<div class="form-group">
	                            	<label>path</label>
	                            	<input type="text" class="form-control" name="configurer" placeholder="Path">
	                         	</div>
	                         </div><!-- /.box-body -->
		                     <div class="box-footer"> 			
		                        <g:submitButton name="button-submit" class="btn btn-success" value="Submit" />
		                        <a class="btn btn-danger" href="${createLink(uri: '/admin/repository/list', absolute: true)}" >Cancel</a>	
		                    	
		                     </div>
	                	 </form>
	             	</div>
             </div><!-- /.box -->            
        </div>     	
	</section><!-- /.content -->     
</body>