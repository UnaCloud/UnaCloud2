<%@page import="uniandes.unacloud.share.enums.ImageEnum"%>
<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            My Images
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li class="active">Images</li>
        </ol>        	         
    </section>
    <!-- Main content -->
    <section class="content">
     	<div class="row ">     		     
             <div class="col-xs-12">  
             	<g:render template="/share/message"/>                       
                  <a href="${createLink(uri: '/services/image/new', absolute: true)}" class="btn btn-primary btn-sm"><i class='fa fa-plus' ></i> New Image</a>
                  <hr>
                  <div class="box box-solid">
                  <div class="box-body table-responsive">
                      <table id="unacloudTable" class="table table-bordered table-striped">
                          <thead>
                              <tr>
                                  <th>Image Name</th>
                                  <th>Operating System</th>
                                  <th>Platform</th>
                                  <th>Size</th>
                                  <th>State</th>
                                  <th>Actions</th>
                              </tr>
                          </thead>
                          <tbody>
                          <g:each in="${images}" var="image"> 
                              <tr>
                                  <td>${image.name} <g:if test="${image.isPublic}"><i class='fa fa-globe text-green' title="This image is public" data-toggle="tooltip"></i></g:if></td>
                                  <td>${image.operatingSystem.name}</td>
                                  <td>${image.platform.name}</td>
                                  <td>${image.getSize()}</td>
                                  <td>
	                                  <g:if test="${image.state.equals(ImageEnum.AVAILABLE)}">
	                                 	<span class="label label-success">${image.state.name}</span>
	                                  </g:if>
	                                  <g:elseif test="${image.state.equals(ImageEnum.COPYING)}">
	                                  	<span class="label label-primary">${image.state.name}</span>
	                                  </g:elseif>
	                                  <g:elseif test="${image.state.equals(ImageEnum.DISABLE)}">
	                                  	<span class="label label-default">${image.state.name}</span>
	                                  </g:elseif>
	                                  <g:elseif test="${image.state.equals(ImageEnum.REMOVING_CACHE)}">
	                                  	<span class="label label-warning">${image.state.name}</span>
	                                  </g:elseif>
	                                  <g:elseif test="${image.state.equals(ImageEnum.UNAVAILABLE)}">
	                                  	<span class="label label-danger">${image.state.name}</span>
	                                  </g:elseif>
	                                  <g:elseif test="${image.state.equals(ImageEnum.IN_QUEUE)}">
	                                  	<span class="label label-warning">${image.state.name}</span>
	                                  </g:elseif>
                                  </td>
                                  <td class="column-center">                                   
                                 	 <div class="btn-group">
                                 	 <g:if test="${image.state.equals(ImageEnum.AVAILABLE)}">
                                 	 	<a title="Delete" class="delete_images btn btn-default" data-id="${image.id}" href="${createLink(uri: '/services/image/delete/', absolute: true)}" data-toggle="tooltip"><i class='fa fa-trash-o' ></i></a>
                                   	 	<a title="Edit" href="${createLink(uri: '/services/image/edit/'+image.id, absolute: true)}" class="btn btn-default" data-toggle="tooltip"><i class="fa fa-pencil-square" ></i></a>
                                    	<a title="Remove from cache" class="clear_image btn btn-default" data-id="${image.id}" href="${createLink(uri: '/services/image/clear/', absolute: true)}" data-toggle="tooltip"><i class='fa fa-eraser'></i></a>  
                                    </g:if> 
                                     <g:if test="${image.state.equals(ImageEnum.AVAILABLE)||image.state.equals(ImageEnum.UNAVAILABLE)}">
                                        <a title="Update" href="${createLink(uri: '/services/image/update/'+image.id, absolute: true)}" class="btn btn-default"  data-toggle="tooltip"><i class="fa fa-upload"></i></a>
									 </g:if>
                                     </div>                                  
								  </td>
                              </tr>
                          </g:each>                                   
                          </tbody>
                      </table>
                  </div><!-- /.box-body -->
                  </div><!-- /.box -->
             </div>
        </div>     	
	</section><!-- /.content -->   
</body>
               