<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            Welcome Guest
            <small>Control panel</small>
        </h1>       
    </section>
    <!-- Main content -->
    <section class="content">
    	<div class="row">
    		<div class="col-xs-12 col-sm-4 col-md-4">
                <!-- Success box -->
                <div class="box box-solid box-success">
                    <div class="box-header">
                        <h3 class="box-title title-link"><a class="title-link" href="${createLink(uri: '/services/image/list/', absolute: true)}">My Images <i class="fa fa-arrow-circle-right"></i></a></h3>         
                        <div class="box-tools pull-right">
                            <button class="btn btn-success btn-sm" data-widget="collapse" title="minimize" data-toggle="tooltip"><i class="fa fa-minus"></i></button>
                        </div>
                    </div>
                    <div class="box-body">
                   		<g:if test="${ myImages.size()>0}">
                            <table class="table table-condensed">
	                            <tr>
	                                <th>Group</th>
	                                <th>#</th>
	                            </tr>	                            
	                            <g:each in="${myImages.keySet()}" var="image">
	                            <tr>
	                            	<td>${image}</td>
	                            	<td><span class="badge bg-blue">${myImages.get(image)}</span></td>
	                            </tr>
	                            </g:each>	                            
	                        </table>
                        </g:if>
                        <g:else> 
                            <div class="text-center"><h4>No values</h4></div>                               
                        </g:else>                    	
                    </div><!-- /.box-body -->
                </div><!-- /.box -->
            </div><!-- /.col -->
            <div class="col-xs-12 col-sm-4 col-md-4">
                <!-- Success box -->
                <div class="box box-solid box-primary">
                    <div class="box-header">
                        <h3 class="box-title title-link"><a class="title-link" href="${createLink(uri: '/services/cluster/list/', absolute: true)}">My Clusters <i class="fa fa-arrow-circle-right"></i></a></h3>         
                        <div class="box-tools pull-right">
                            <button class="btn btn-primary btn-sm" data-widget="collapse" title="minimize" data-toggle="tooltip"><i class="fa fa-minus"></i></button>
                        </div>
                    </div>
                    <div class="box-body">
                   		<g:if test="${ myClusters.size()>0}">
                            <table class="table table-condensed">
	                            <tr>
	                                <th>Group</th>
	                                <th>#</th>
	                            </tr>	                            
	                            <g:each in="${myClusters.keySet()}" var="cluster">
	                            <tr>
	                            	<td>${cluster}</td>
	                            	<td><span class="badge bg-green">${myClusters.get(cluster)}</span></td>
	                            </tr>
	                            </g:each>	                            
	                        </table>
                        </g:if>
                        <g:else> 
                            <div class="text-center"><h4>No values</h4></div>                               
                        </g:else>                    	
                    </div><!-- /.box-body -->
                </div><!-- /.box -->
            </div><!-- /.col -->
            <div class="col-xs-12  col-sm-4 col-md-4">
                <!-- Success box -->
                <div class="box box-solid box-warning">
                    <div class="box-header">
                        <h3 class="box-title title-link"><a class="title-link" href="${createLink(uri: '/services/deployment/list/', absolute: true)}">My Deployments <i class="fa fa-arrow-circle-right"></i></a></h3>         
                        <div class="box-tools pull-right">
                            <button class="btn btn-warning btn-sm" data-widget="collapse" title="minimize" data-toggle="tooltip"><i class="fa fa-minus"></i></button>
                        </div>
                    </div>
                    <div class="box-body">
                   		<g:if test="${ myDeployments.size()>0}">
                            <table class="table table-condensed">
	                            <tr>
	                                <th>Group</th>
	                                <th>#</th>
	                            </tr>	                            
	                            <g:each in="${myDeployments.keySet()}" var="deploy">
	                            <tr>
	                            	<td>${deploy}</td>
	                            	<td><span class="badge bg-red">${myDeployments.get(deploy)}</span></td>
	                            </tr>
	                            </g:each>	                            
	                        </table>
                        </g:if>
                        <g:else> 
                            <div class="text-center"><h4>No values</h4></div>                           
                        </g:else>                    	
                    </div><!-- /.box-body -->
                </div><!-- /.box -->
            </div><!-- /.col -->
		</div>
		<div class="row">
			<g:if test="${boxes}">
			<hr>
         	<g:each in="${boxes}" var="box">
          	<div class="col-lg-3 col-xs-6">
	            <!-- small box -->
	           	<div class="small-box bg-${box.color}">
                  <div class="inner">
                      <h3>
                          ${box.quantity}
                      </h3>
                      <p>
                          ${box.name}
                      </p>
                  </div>
                  <div class="icon">
                      <i class="ion ${box.icon}"></i>
                  </div>
                  <a href="${createLink(uri: box.url, absolute: true)}" class="small-box-footer">
                     Show list <i class="fa fa-arrow-circle-right"></i>
                  </a>
	           </div>
            </div><!-- ./col -->
            </g:each>            
            </g:if> 
		</div>    
    </section><!-- /.content -->    
</body>
</html>
               