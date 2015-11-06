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
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-dashboard"></i> Home</a></li>
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
                  <g:each in="${labs}" var="lab"> 
                      <div class="col-lg-3 col-xs-6">
                          <!-- small box -->
                          <div class="small-box bg-aqua">
                              <div class="inner">
                              	  <h3>
                                      lab.name
                                  </h3>
                                  <h5>
                                      lab.physicalMachines.size
                                  </h5>
                                  <p>
                                      Physical Machines
                                  </p>
                              </div>
                              <div class="icon">
                                  <i class="ion ion-laptop"></i>
                              </div>
                              <a href="#" class="small-box-footer">
                                  Lab Detail <i class="fa fa-arrow-circle-right"></i>
                              </a>
                          </div>
                      </div><!-- ./col -->
                 </g:each> 
                 </div><!-- /.row -->
             </div>
        </div>     	
	</section><!-- /.content -->    
</body>
               