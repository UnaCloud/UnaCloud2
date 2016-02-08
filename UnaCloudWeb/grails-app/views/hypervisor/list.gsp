<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            All Hypervisors
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li class="active">Hypervisors</li>
        </ol>        	         
    </section>
    <!-- Main content -->
    <section class="content">
     	<div class="row">     		     
             <div class="col-xs-12">  
             	  <g:render template="/share/message"/>                       
                  <a href="${createLink(uri: '/admin/hypervisor/new', absolute: true)}" class="btn btn-primary btn-sm"><i class='fa fa-plus' ></i> New Hypervisor</a>
                  <hr>
                  <div class="box box-solid">
                  <div class="box-body table-responsive">
                      <table id="unacloudTable" class="table table-bordered table-striped">
                          <thead>
                              <tr>
                                  <th>Name</th>
                                  <th>Version</th>
                                  <th>Actions</th>
                                  <th>Executable</th>
                                  <th>Extensions</th>
                              </tr>
                          </thead>
                          <tbody>
                          <g:each in="${hypervisors}" var="hypervisor"> 
                              <tr>
                                 <td>${hypervisor.name}</td>
                                 <td>${hypervisor.hypervisorVersion}</td>
                                 <td>${hypervisor.mainExtension}</td>
                                 <td>${hypervisor.filesExtensions}</td>
                                 <td class="column-center">                                  
	                                 <div class="btn-group">
		                                 <a title="Delete" class="delete_hypervisor btn btn-default" data-id="${hypervisor.id}" href="${createLink(uri: '/admin/hypervisor/delete/', absolute: true)}" data-toggle="tooltip"><i class='fa fa-trash-o' ></i></a>
		                                 <a title="Edit" class="btn btn-default" href="${createLink(uri: '/admin/hypervisor/edit/'+hypervisor.id, absolute: true)}" data-toggle="tooltip"><i class='fa fa-pencil' data-toggle="tooltip"></i></a>	                                
	                                 </div>
								 </td>  
                              </tr>                                                          
                          </g:each>                                   
                          </tbody>
                      </table>
                  </div><!-- /.box-body -->
                  </div><!-- /.box-->
             </div>
        </div>     	
	</section><!-- /.content -->    
</body>
               