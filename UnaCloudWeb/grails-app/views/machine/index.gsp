<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
           ${machine.name}
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li><a href="${createLink(uri: '/admin/lab/list', absolute: true)}"><i class="fa fa-sitemap"></i> All laboratories</a></li>
            <li><a href="${createLink(uri: '/admin/lab/' + lab.id, absolute: true)}"><i class="fa fa-flask"></i> ${lab.name}</a></li>
            <li class="active">Machine Detail</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">
    		<div class="col-xs-12">  
	    	    <a href="${createLink(uri: '/admin/lab/' + lab.id + '/machine/', absolute: true)}" data-id = "${machine.id}" class="delete_machines btn btn-danger btn-sm"><i class='fa fa-trash-o' ></i> Delete</a>
	            <a href="${createLink(uri: '/admin/lab/' + lab.id + '/machine/' + machine.id + '/edit', absolute: true)}" class="btn btn-primary btn-sm"><i class='fa fa-pencil-square' ></i> Edit</a>
	            <hr>
	           	<div class="col-lg-6 col-sm-6 col-xs-12"> 
	    			<g:render template="/share/message"/>                   		     
	          		<div class="box box-primary">     	
	          			<div class="box-header">
	          				<h5 class="box-title">Detail Info</h5> 							    			
	          			</div>		
	                	<div class="box-body">	 
	                	    <label class="col-lg-6 col-sm-6 col-xs-6">Hostname:</label>   
	                		<p class="help-block">${machine.name}.</p>
	                		<label class="col-lg-6 col-sm-6 col-xs-6">IP Address:</label>   
	                		<p class="help-block">${machine.ip.ip}.</p>
	                		<hr>
	                		<label class="col-lg-6 col-sm-6 col-xs-6">Last Log in server:</label>   
	                		<p class="help-block"><g:if test="${machine.lastLog != null && !machine.lastLog.isEmpty() && !machine.lastLog.equals("None")}">${machine.lastLog}</g:if><g:else>None</g:else></p>
	                		<g:if test="${machine.lastLog != null && !machine.lastLog.isEmpty() && !machine.lastLog.equals("None")}">
	                		<a class="btn btn-success" title="Download last log" href="${createLink(uri: fileUrl, absolute: true)}" ><i class='fa fa-download' ></i> Download log</a>
	                		</g:if>
							<p class="help-block"><g:if test="${machine.lastMonitoring != null && !machine.lastMonitoring.isEmpty() && !machine.lastMonitoring.equals("None")}">${machine.lastMonitoring}</g:if><g:else>None</g:else></p>
							<g:if test="${machine.lastMonitoring != null && !machine.lastMonitoring.isEmpty() && !machine.lastMonitoring.equals("None")}">
								<a class="btn btn-success" title="Download last monitoring log" href="${createLink(uri: fileUrl, absolute: true)}" ><i class='fa fa-download' ></i> Download monitoring log</a>
							</g:if>
	                	</div>	                 	
	            	</div>
		        </div><!-- /.box -->    
	      	</div> 		           
        </div>     	
	</section><!-- /.content -->   
</body>