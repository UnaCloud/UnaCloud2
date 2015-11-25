<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            Agent Configuration
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li class="active">Agent Configuration</li>
        </ol>
    </section>    
    <section class="content">     
   		 <div class="row">    		
             <div class="col-xs-6">   
            		<g:render template="/share/message"/>
            		<div id="label-message"></div>               		     
            		<div class="box box-primary">     	
            			<div class="box-header">
            				<h5 class="box-title">Current version of agent: ${agent}</h5>             										    			
            			</div>            			
	                 	<!-- form start -->
	                 	<div class="box-body">	 
	                 	    <label>Update Agent Version</label>   
	                 		<p class="help-block">Click to update the agent version. When host machines are restarted, those with different agent version will update their agents.</p>
	                 		<a title="submit" class="btn btn-primary" href="${createLink(uri: '/config/agent/version/', absolute: true)}" >Update</a>
		                 	<hr>
		                 	<label>Download Agent Files</label>   
	                 		<p class="help-block">Click to download the current agent jar files and a list of variables for host machines.</p>
	                 		<a title="submit" class="btn btn-primary" href="${createLink(uri: '/config/agent/download/', absolute: true)}" >Download</a>
	                 	</div>
             		</div>
             </div><!-- /.box -->            
        </div>      	
	</section><!-- /.content -->     
</body>