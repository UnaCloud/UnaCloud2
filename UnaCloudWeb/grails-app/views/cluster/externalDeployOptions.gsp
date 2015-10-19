<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
     </section>
	 <!-- Main content -->
     <section class="content">                 
         <div class="error-page">
             <h2 class="headline text-info"> Oops!!! </h2>
             <div class="error-content">
                 <h3><i class="fa fa-warning text-yellow"></i> Service not available.</h3>
                 <p>
                     We are sorry, we are working to provide connection with external cloud providers. 
                     Meanwhile, you may <a href='${createLink(uri: '/services/cluster/list', absolute: true)}'>return to image list</a>.
                 </p>
             </div><!-- /.error-content -->
         </div><!-- /.error-page -->
     </section><!-- /.content -->    
</body>