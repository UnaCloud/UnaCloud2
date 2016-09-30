<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            All Platforms
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li class="active">Platforms</li>
        </ol>        	         
    </section>
    <!-- Main content -->
    <section class="content">
     	<div class="row">     		     
             <div class="col-xs-12">  
             	  <g:render template="/share/message"/>                       
                  <a href="${createLink(uri: '/admin/platform/new', absolute: true)}" class="btn btn-primary btn-sm"><i class='fa fa-plus' ></i> New Platform</a>
                  <hr>
                  <div class="box box-solid">
                  <div class="box-body table-responsive">
                      <table id="unacloudTable" class="table table-bordered table-striped">
                          <thead>
                              <tr>
                                  <th>Name</th>
                                  <th>Version</th>                                  
                                  <th>Executable</th>
                                  <th>File Extensions</th>
                                  <th>Actions</th>
                              </tr>
                          </thead>
                          <tbody>
                          <g:each in="${platforms}" var="platform"> 
                              <tr>
                                 <td>${platform.name}</td>
                                 <td>${platform.platformVersion}</td>
                                 <td>${platform.mainExtension}</td>
                                 <td>${platform.filesExtensions}</td>
                                 <td class="column-center">                                  
	                                 <div class="btn-group">
		                                 <a title="Delete" class="delete_platform btn btn-default" data-id="${platform.id}" href="${createLink(uri: '/admin/platform/delete/', absolute: true)}" data-toggle="tooltip"><i class='fa fa-trash-o' ></i></a>
		                                 <a title="Edit" class="btn btn-default" href="${createLink(uri: '/admin/platform/edit/'+platform.id, absolute: true)}" data-toggle="tooltip"><i class='fa fa-pencil' data-toggle="tooltip"></i></a>	                                
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
               