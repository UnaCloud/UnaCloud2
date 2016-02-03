<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            Update Image Files
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-dashboard"></i> Home</a></li>
            <li><a href="${createLink(uri: '/services/image/list', absolute: true)}"><i class="fa fa-th-list"></i> Images</a></li>
            <li class="active">Update Image</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">
             <div class="col-lg-6 col-sm-6 col-xs-12">   
            		<div id="label-message"></div>  
             		<g:render template="/share/message"/>                		     
            		<div class="box box-primary">     	
            			<div class="box-header">
            				<h5 class="box-title">Update your image</h5> 							    			
            			</div>		
                 	<!-- form start -->
                 	<form id="form-change" action="${createLink(uri: '/services/image/update/save', absolute: true)}" enctype="multipart/form-data" role="form">
                     	 <div class="box-body">	                     		
            				 <p class="help-block">Use confirm to upload image files.</p> 
            				 <input id="id" name="id" type="hidden" value="${id}">                        	           
	                         <a id="button-update" class="btn btn-success" style="cursor:pointer">Confirm</a>	                         
	                         <a class="btn btn-danger" href="${createLink(uri: '/services/image/list', absolute: true)}" >Cancel</a>
	                     </div>
                	</form>
             	</div>
            </div><!-- /.box -->            
        </div>     	
	</section><!-- /.content -->     
</body>