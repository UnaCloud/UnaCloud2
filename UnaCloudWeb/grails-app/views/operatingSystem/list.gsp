<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            All Operating Systems
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li class="active">Operating System</li>
        </ol>        	         
    </section>
    <!-- Main content -->
    <section class="content">
     	<div class="row">     		     
             <div class="col-xs-12">  
             	  <g:render template="/share/message"/>                       
                  <a href="${createLink(uri: '/admin/os/new', absolute: true)}" class="btn btn-primary btn-sm"><i class='fa fa-plus' ></i> New Hypervisor</a>
                  <hr>
                  <div class="box-body table-responsive">
                      <table id="unacloudTable" class="table table-bordered table-striped">
                          <thead>
                              <tr>
                                  <th>Name</th>
                                  <th>Configuration class</th>
                                  <th>Actions</th>
                              </tr>
                          </thead>
                          <tbody>
                          <g:each in="${oss}" var="os"> 
                              <tr>
                                 <td>${os.name}</td>
                                 <td>${os.configurer}</td>
                                 <td class="column-center">                                  
	                                 <div class="btn-group">
		                                 <a title="Delete" class="delete_os btn btn-default" data-id="${os.id}" href="${createLink(uri: '/admin/os/delete/', absolute: true)}" ><i class='fa fa-trash-o' ></i></a>
		                                 <a title="Edit" class="btn btn-default" href="${createLink(uri: '/admin/os/edit/'+os.id, absolute: true)}" ><i class='fa fa-pencil' ></i></a>	                                
	                                 </div>
								 </td>  
                              </tr>                                                          
                          </g:each>                                   
                          </tbody>
                      </table>
                  </div><!-- /.box-body -->
             </div>
        </div>     	
	</section><!-- /.content -->    
</body>
               