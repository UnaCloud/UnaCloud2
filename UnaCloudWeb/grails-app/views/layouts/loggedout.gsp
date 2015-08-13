
<!DOCTYPE html>
<html lang="en">
   <head>
   		<meta charset="utf-8">
    <title>UnaCloud 2.0</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
	
    <!-- Le styles -->
    <asset:stylesheet src="application.css"/>
    <style type="text/css">
      body {
        padding-top: 60px;
        padding-bottom: 40px;
      }
      form {
      	
     	float: none;
     	margin-left: auto;
     	margin-right: auto;
      	
      }
      .sidebar-nav {
        padding: 9px 0;
      }
      

      @media (max-width: 980px) {
        /* Enable use of floated navbar text */
        .navbar-text.pull-right {
          float: none;
          padding-left: 5px;
          padding-right: 5px;
        }
      }
    </style>
      <g:layoutTitle/>
      
   	
   </head>
   <body>
      <asset:javascript src="application.js"/>
      <div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container-fluid">
          <a class="brand" href="${createLink(uri: '/', absolute: true)}">UnaCloud 2.0</a>
          <div class="nav-collapse collapse">
            <ul class="nav pull-right">
             <li></li>
            </ul>
          </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>
    <div class="container-fluid">
	<div class="row-fluid">
	<g:layoutBody/>
      	
        </div>
        </div>
   </body>
</html>