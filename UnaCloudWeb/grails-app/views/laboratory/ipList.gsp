<%@page import="unacloud.share.enums.IPEnum"%>
<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            IP Pool in lab ${lab.name}
        </h1>
         <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li><a href="${createLink(uri: '/admin/lab/list', absolute: true)}"><i class="fa fa-sitemap"></i> All laboratories</a></li>
            <li><a href="${createLink(uri: '/admin/lab/'+lab.id, absolute: true)}"><i class="fa fa-flask"></i> ${lab.name}</a></li>
            <li class="active">IP Pool</li>
        </ol>          	         
    </section>
    <!-- Main content -->
    <section class="content">
     	<div class="row">     		     
             <div class="col-xs-12">  
             	  <g:render template="/share/message"/>   
             	  <div class="box box-solid">
                  <div class="box-body table-responsive">
                      <table id="unacloudTable" class="table table-bordered table-striped">
                          <thead>
                              <tr>
                                  <th>IP</th>
                                  <th>State</th>
                                  <th>Actions</th>
                              </tr>
                          </thead>
                          <tbody>
                          <g:each in="${pool.ips}" var="ip"> 
                              <tr>
                                 <td>${ip.ip}</td>
                                 <td>
	                                  <g:if test="${ip.state.equals(IPEnum.USED)}">
	                                 	<span class="label label-primary">${ip.state.toString()}</span>
	                                  </g:if>
	                                  <g:if test="${ip.state.equals(IPEnum.RESERVED)}">
	                                 	<span class="label label-warning">${ip.state.toString()}</span>
	                                  </g:if>
	                                  <g:if test="${ip.state.equals(IPEnum.DISABLED)}">
	                                 	<span class="label label-default">${ip.state.toString()}</span>
	                                  </g:if>
	                                  <g:if test="${ip.state.equals(IPEnum.AVAILABLE)}">
	                                 	<span class="label label-success">${ip.state.toString()}</span>
	                                  </g:if>                               
                                  </td>                                 
                                  <td class="column-center">
	                                  <div class="btn-group">
		                                  <g:if test="${!ip.state.equals(IPEnum.USED)&&!ip.state.equals(IPEnum.RESERVED)}">
		                                  <a title="Delete" class="delete_ip btn btn-default" data-id="${ip.id}" href="${createLink(uri: '/admin/lab/'+lab.id+'/pool/'+pool.id+'/delete/ip/', absolute: true)}" data-toggle="tooltip"><i class='fa fa-trash-o' ></i></a>
			                              <g:if test="${!ip.state.equals(IPEnum.DISABLED)}">
			                              <a title="Disable" class="btn btn-default" href="${createLink(uri: '/admin/lab/'+lab.id+'/pool/'+pool.id+'/set/ip/'+ip.id, absolute: true)}" data-toggle="tooltip"><i class='fa fa-ban' ></i></a>
			                              </g:if>
			                              <g:else>
			                              <a title="Enable" class="btn btn-default" href="${createLink(uri: '/admin/lab/'+lab.id+'/pool/'+pool.id+'/set/ip/'+ip.id, absolute: true)}" data-toggle="tooltip"><i class='fa fa-check' ></i></a>
			                              </g:else>
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
               