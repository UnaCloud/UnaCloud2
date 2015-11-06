<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            New Laboratory
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-dashboard"></i> Home</a></li>
            <li><a href="${createLink(uri: '/admin/lab/list', absolute: true)}"><i class="fa fa-flask"></i> Laboratories</a></li>
            <li class="active">New Laboratory</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">
    		<div class="col-xs-12">      			
    			<div id="label-message"></div> 
    			<g:render template="/share/message"/>         
    			<form method="post" action="${createLink(uri: '/admin/lab/save', absolute: true)}" role="form">	                        		     
		       		<div class="box box-primary"> 
		           	 	 <div class="box-body"> 
	           	 	 		 <p class="help-block">Configure your own lab.</p> 
                        	 <div class="form-group">
                             	<label>Laboratory Name</label>
                            	<input type="text" class="form-control" name="name" placeholder="Lab Name">
                         	 </div>
                        	 <div class="form-group">
	                             <label>Select</label>
	                             <select name= "net" class="form-control">
	                             	<g:each in="${netConfigurations}" status="i" var="net">
					  					<option value="${net}">${net}</option>
					  				</g:each>
	                             </select>
	                         </div>	                          
	                         <div class="form-group">
	                             <label>
	                             	<input type="checkbox" name="isPrivate"  class="check-blue"/> Private network
	                             </label>
	                             <p class="help-block">If your network is private or public.</p>
	                         </div>  
	                         <div class="form-group">
	                             <label>
	                             	<input type="checkbox" name="isHigh"  class="check-blue"/> High Availability
	                             </label>
	                             <p class="help-block">If your Physical Machines have a high availability.</p>
	                         </div>               
		                 </div><!-- /.box-body -->	
		                 <div class="box-footer"> 
		           			<g:submitButton name="button-submit" class="btn btn-success" value="Submit" />		
		                </div>		                         	
		       	 	</div>
	       	 	</form>
    		</div>			
    	</div>   	
	</section>
</body>