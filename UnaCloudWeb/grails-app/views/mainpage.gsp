<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
<body>
<div class="span9">
	<div class="hero-unit">
        <h3 class="text-center"> Welcome ${session.user.name.split(" ")[0]} </h3>
        <br>
        
        <div class="row-fluid text-center">
		<div class="span4 ">
			<a href="functionalities"><g:img file="functionalities.png"/></a>
			<p>Functionalities</p>
		</div>
		<div class="span4">
			<a href="administration"><g:img file="administration.png" /></a>
			<p>Administration</p>
		</div>
		<div class="span4">
			<a href="configuration"><g:img file="configuration.png"/></a>
			<p>Configuration</p>
		</div>
		</div>
		    
    </div>
</div>
    
</body>
</html>