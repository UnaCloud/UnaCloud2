<!-- Creada por Carlos E. Gomez - diciembre 11 de 2015 -->  
<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            All Repositories
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li class="active">Repositories</li>
        </ol>        	         
    </section>
    <!-- Main content -->
    <section class="content">
     	<div class="row">     		     
             <div class="col-xs-12">  
             	  <g:render template="/share/message"/>                       
                  <a href="${createLink(uri: '/admin/repository/new', absolute: true)}" class="btn btn-primary btn-sm"><i class='fa fa-plus' ></i> New Repository</a>
                  <hr>
                  <div class="box-body table-responsive">
                      <table id="unacloudTable" class="table table-bordered table-striped">
                          <thead>
                              <tr>
                                  <th>Name</th>
                                  <th>Path</th>
                                  <th>Actions</th>
                              </tr>
                          </thead>
                          <tbody>
                          <g:each in="${repositories}" var="repo"> 
                              <tr>
                                 <td>${repo.name}</td>
                                 <td>${repo.path}</td>
                                 <td class="column-center">                                  
	                                 <div class="btn-group">
		                                 <a title="Delete" class="delete_os btn btn-default" data-id="${repo.id}" href="${createLink(uri: '/admin/repository/delete/', absolute: true)}" >
		                                 	<i class='fa fa-trash-o'></i>
		                                 </a>
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
               