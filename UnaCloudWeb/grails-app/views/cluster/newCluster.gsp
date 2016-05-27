<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            New Cluster
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li><a href="${createLink(uri: '/services/cluster/list', absolute: true)}"><i class="fa fa-th"></i> Clusters</a></li>
            <li class="active">New Cluster</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">
    		<div class="col-lg-6 col-sm-6 col-xs-12">      			
    			<div id="label-message"></div> 
    			<g:render template="/share/message"/>         
    			<form id="form-create" name="newCluster" action="${createLink(uri: '/services/cluster/save', absolute: true)}" enctype="multipart/form-data" role="form">
	               <div class="box box-primary"> 
		           	 	<div class="box-body"> 
		           	 	 	<div class="form-group">
		                       <label>Cluster Name</label>
		                       <input type="text" class="form-control" name="name" placeholder="Cluster Name">
		                    </div>  
		                   	 <div class="form-group">
		                         <div class="table-responsive">
	                         	 	<div class="form-group">
	                         	 		<label class="control-label">Select Images</label>
	                         	 		<p class="help-block">* Operating System - Image Name - Access protocol</p>
                                        <select name= "images" class="form-control" multiple>
                                        <g:each in="${images}" var="image"> 
                                            <option  value="${image.id}">${image.operatingSystem.name} - ${image.name} - ${image.accessProtocol}</option>                                                
                                        </g:each>    
                                        </select>                   
                                 	</div>
                                 </div>
		           			 </div>                       
		                 </div><!-- /.box-body -->	
		                 <div class="box-footer"> 
		           			<g:submitButton name="button-submit" class="btn btn-success" value="Submit" />	
		           			 <a class="btn btn-danger" href="${createLink(uri: '/services/cluster/list', absolute: true)}" >Cancel</a>		
		                </div>		                         	
		       	 	</div>
	       	 	</form>
    		</div>			
    	</div>   	
	</section>
</body>