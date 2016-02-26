<html lang="en">   
    <head>
        <meta charset="UTF-8">
        <title>UnaCloud | Error</title>
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
        <!-- jQuery 2.0.2 -->
        <asset:javascript src="plugins/jquery/jquery_2_0_2.min.js"/>
        <!-- Bootstrap -->
        <asset:javascript src="plugins/bootstrap/bootstrap.min.js"/>    
        
    </head>
	<body class="login">
		<div class="wrapper row-offcanvas row-offcanvas-left"> 
			 <!-- Main content -->
		     <section class="content">                 
		         <div class="error-page">   
		          	 <h1 class=" text-center"> Oops!!! <small>this is embarrasing</small></h1>     		         	 
		             <h2 class="headline text-info text-center"> ${error }</h2>
		             <div class="error-content text-center">			            
		                 <h3><i class="fa fa-warning text-yellow"></i> Error: ${message }.</h3>	
		                 <p>${description}.</p>	                
		             </div><!-- /.error-content -->
		             <br>
		             <p class="text-center">
		                Meanwhile, you may <a href='${createLink(uri: '/', absolute: true)}'>return to dashboard</a>.
		             </p>
		         </div><!-- /.error-page -->
		     </section><!-- /.content -->  
		 </div>              
	 </body>
</html>