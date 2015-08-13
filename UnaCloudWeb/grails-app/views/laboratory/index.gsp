<html>
   <head>
      <meta name="layout" content="main"/>
      <r:require modules="bootstrap"/>
   </head>
<body>
<div class="hero-unit span9">
<h3 class="text-center">Infrastructure Management </h3>       
	    <g:each in="${laboratories}" status="i" var="lab">
        	<g:if test="${i%3==0}"><div class="row-fluid"></g:if>
        	<div class="span4 text-center">
        		<g:link controller="laboratory" action="getLab" params="${[id: lab.id]}"><g:img file="infrastructure.png"/></g:link>
        		<p>${lab.name}</p>
        		
        	</div>
        	<g:if test="${i%3==2}"></div></g:if>
        </g:each>
        <div class="span4 text-center">
        		<g:link controller="laboratory" action="addLab"><g:img file="add.png"/></g:link>
        		<p>Add New Lab</p>
        		
        </div>
    </div>
</div>
</body>
