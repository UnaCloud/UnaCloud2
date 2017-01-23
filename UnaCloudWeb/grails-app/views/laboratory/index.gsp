<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            Infrastructure Management
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li class="active">Laboratories</li>
        </ol>        	         
    </section>
    <!-- Main content -->
    <section class="content">
     	<div class="row">     		     
             <div class="col-xs-12">  
             	  <g:render template="/share/message"/>                       
                  <a href="${createLink(uri: '/admin/lab/new', absolute: true)}" class="btn btn-primary btn-sm"><i class='fa fa-plus' ></i> New Laboratory</a>
                  <hr>
                  <div class="row">
                  <g:if test="${labs.size()<=0}">
                  	  <div class="col-lg-12">
	                  <p class="help-block">You do not have laboratories</p>
	                  </div>
                  </g:if>
                  <g:else>
                  	   <g:each in="${labs}" var="lab"> 
	                      <div class="col-lg-3 col-sm-3 col-xs-6">
	                          <!-- small box -->
		                      <a href="${createLink(uri: '/admin/lab/'+lab.id, absolute: true)}" class="small-box  block-button <g:if test="${lab.enable}">bg-green</g:if><g:else>bg-gray</g:else>">
	                              <div class="inner">
	                              	  <h3>
	                                      ${lab.name}
	                                  </h3>
	                                  <h4>
	                                      ${lab.numberOfMachines()} Host
	                                  </h4>
	                                  <h4>
	                                      ${lab.numberOfIps()} IPs
	                                  </h4>
	                              </div>
	                              <div class="icon">
	                                  <i class="ion ion-laptop"></i>
	                              </div>
	                              <div class="small-box-footer">
	                                  Lab Detail <i class="fa fa-arrow-circle-right"></i>
	                              </div>
	                          </a>
	                      </div><!-- ./col -->
	                 </g:each> 
                 </g:else>
                 </div><!-- /.row -->
             </div>
        </div>     	
	</section><!-- /.content -->    
</body>
               