<!DOCTYPE html>
<html lang="en">   
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <title>UnaCloud | Dashboard</title>
        <link rel="shortcut icon" type="image/x-icon" href="${createLink(uri: '/images/favicon.ico', absolute: true)}"/>
        <meta content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no' name='viewport'>
        
        <asset:stylesheet src="main.css"/>
        <asset:stylesheet src="application.css"/>
        <!-- bootstrap 3.0.2 -->
        <asset:stylesheet src="bootstrap.min.css"/>
        <!-- font Awesome -->
        <asset:stylesheet src="font-awesome.min.css"/>
        <!-- Ionicons -->
        <asset:stylesheet src="ionicons.min.css"/>
        <asset:stylesheet src="plugins/datatables/dataTables.bootstrap.css"/>
        <!-- jQuery 2.0.2 -->
        <asset:javascript src="plugins/jquery/jquery_2_0_2.min.js"/>
        <!-- Bootstrap -->
        <asset:javascript src="plugins/bootstrap/bootstrap.min.js"/>    
        <asset:javascript src="plugins/bootbox/bootbox.js"/> 
        <!-- animate -->
        <asset:stylesheet src="animate.css"/> 
        <!-- Datatables -->
        <asset:javascript src="plugins/datatables/jquery.dataTables.js"/>    
        <asset:javascript src="plugins/datatables/dataTables.bootstrap.js"/>        
     	<asset:javascript src="plugins/input-mask/jquery.inputmask.js"/>  
     	<asset:javascript src="plugins/input-mask/jquery.inputmask.extensions.js"/> 
        <asset:javascript src="ui.js"/>   
        <asset:javascript src="pages.js"/>                
        <g:layoutTitle/>
        
    </head>
    <body class="skin-black fixed">
    
        <!-- header logo: style can be found in header.less -->
        <header class="header">
            <a href="${createLink(uri: '/home', absolute: true)}" class="logo"><i class="fa fa-cloud"></i>
                UnaCloud
            </a>
            <!-- Header Navbar: style can be found in header.less -->
            <nav class="navbar navbar-static-top" role="navigation">
                <!-- Sidebar toggle button-->
                <a href="#" class="navbar-btn sidebar-toggle" data-toggle="offcanvas" role="button">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </a>
                <div class="navbar-right">
                    <ul class="nav navbar-nav">
                        <li class="dropdown user user-menu">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                <i class="glyphicon glyphicon-user"></i>
                                <span>${session.user.name}<i class="caret"></i></span>
                            </a>
                            <ul class="dropdown-menu">
                                <!-- User image -->
                                <li class="user-header bg-light-blue">
                                    <i class="fa fa-smile-o"></i>
                                    <p>
                                        ${session.user.name } - ${session.user.description}
                                        <small>Member since ${session.user.registerDate}</small>
                                    </p>
                                </li>
                                <!-- Menu Footer-->
                                <li class="user-footer">
                                    <div class="pull-left">
                                        <a href="${createLink(uri: '/user/profile', absolute: true)}" class="btn btn-default btn-flat">Profile</a>
                                    </div>
                                    <div class="pull-right">
                                   		<a href="${createLink(uri: '/logout', absolute: true)}" class="btn btn-default btn-flat">Sign Out</a>                                       
                                    </div>
                                </li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </nav>
        </header>
        <div  class="wrapper row-offcanvas row-offcanvas-left" >
            <!-- Left side column. contains the logo and sidebar -->
            <aside class="left-side sidebar-offcanvas">                
                <!-- sidebar: style can be found in sidebar.less -->
                <section class="sidebar">
                    <!-- Sidebar user panel -->
                    <div class="user-panel">
                        <div class="pull-left info">
                           <h4><i class="fa fa-smile-o"></i> Hello, ${session.user.username}</h4>
                        </div>
                    </div>
                    <!-- sidebar menu: : style can be found in sidebar.less -->
                    <ul class="sidebar-menu">
                        <li id="services-tree" class="treeview">
                            <a href="#">
                                <i class="fa fa-bar-chart-o"></i>
                                <span>Services</span>
                                <i class="fa fa-angle-left pull-right"></i>
                            </a>
                            <ul class="treeview-menu">
                                <li id="services-image-tree"><a href="${createLink(uri: '/services/image/list', absolute: true)}"><i class="fa fa-angle-double-right"></i> Images</a></li>
                                <li id="services-cluster-tree"><a href="${createLink(uri: '/services/cluster/list', absolute: true)}"><i class="fa fa-angle-double-right"></i> Clusters</a></li>
                                <li id="services-deployment-tree"><a href="${createLink(uri: '/services/deployment/list', absolute: true)}"><i class="fa fa-angle-double-right"></i> Deployments</a></li>
                            </ul>
                        </li>
                       
                        <g:if test="${session.user.isAdmin()}">
	                        <li id="admin-tree" class="treeview">
	                            <a href="#">
	                                <i class="fa fa-laptop"></i>
	                                <span>Administration</span>
	                                <i class="fa fa-angle-left pull-right"></i>
	                            </a>
	                            <ul class="treeview-menu">
	                                <li id="admin-user-tree">
	                                	<a href="${createLink(uri: '/admin/user/list', absolute: true)}">
	                                		<i class="fa fa-angle-double-right"></i>
	                                			Users
	                                	</a>
	                                </li>
	                                <li id="admin-group-tree">
	                                	<a href="${createLink(uri: '/admin/group/list', absolute: true)}">
	                                		<i class="fa fa-angle-double-right"></i>
	                                			Groups
	                                	</a>
	                                </li>
	                                <li id="admin-platform-tree">
	                                	<a href="${createLink(uri: '/admin/platform/list', absolute: true)}">
	                                		<i class="fa fa-angle-double-right"></i>
	                                			Platforms
	                                	</a>
	                                </li>
	                                <li id="admin-os-tree">
	                                	<a href="${createLink(uri: '/admin/os/list', absolute: true)}">
	                                		<i class="fa fa-angle-double-right"></i>
	                                			OS
	                                	</a>
	                                </li>
	                                <li id="admin-lab-tree">
	                                	<a href="${createLink(uri: '/admin/lab/list', absolute: true)}">
	                                		<i class="fa fa-angle-double-right"></i>
	                                			Infrastructure
	                                	</a>
	                                </li>	      
	                                 <!-- Agregado por Carlos E. Gomez - diciembre 11 de 2015   -->                
	                                <li id="admin-lab-tree">
	                                	<a href="${createLink(uri: '/admin/repository/list', absolute: true)}">
	                                		<i class="fa fa-angle-double-right"></i>
	                                			Storage
	                                	</a>
	                                </li>
	                                <!--                               
	                                <li><a href="${createLink(uri: '/admin/hardware/list', absolute: true)}"><i class="fa fa-angle-double-right"></i> Hardware Profiles **</a></li>	 
	                            	 -->
	                            </ul>
	                        </li>
	                     
	                        <li id="config-tree" class="treeview">
	                            <a href="#">
	                                <i class="fa fa-cog"></i> <span>Configuration</span>
	                                <i class="fa fa-angle-left pull-right"></i>
	                            </a>
	                            <ul class="treeview-menu">
	                                <li><a href="${createLink(uri: '/config/variables', absolute: true)}"><i class="fa fa-angle-double-right"></i> Server Variables</a></li>
	                                <li><a href="${createLink(uri: '/config/agent', absolute: true)}"><i class="fa fa-angle-double-right"></i> Agent management</a></li>
	                            </ul>
	                        </li>  
                        </g:if>                   
                    </ul>
                </section>
                <!-- /.sidebar -->
            </aside>

            <!-- Right side column. Contains the navbar and content of the page -->
            <aside class="right-side">              
                <g:layoutBody/>
              	
            </aside><!-- /.right-side -->
            
        </div><!-- ./wrapper -->
		<footer class="footer">
	      <div class="container">
	        <div class="center">
					Universidad de los Andes | Vigilada Mineducaci&oacute;n <br>
					Reconocimiento como Universidad: Decreto 1297 del 30 de mayo de 1964. <br>
					Reconocimiento personer&iacute;a jur&iacute;dica: Resoluci&oacute;n 28 del 23 de febrero de 1949 Minjusticia  <br>
					Edificio Mario Laserna Cra 1Este No 19A - 40 Bogot&aacute; (Colombia) | Tel: [571] 3394949 Ext: 2860, 2861, 2862 | Fax: [571] 3324325 <br>
					&copy; 2016 - <a href="https://sistemas.uniandes.edu.co"> Departamento de Ingenier&iacute;a de Sistemas y Computaci&oacute;n </a>
			</div>
	      </div>
    	</footer>
		<asset:javascript src="main.js"/>
		<asset:javascript src="cloud.js"/>   		
    </body>
</html>