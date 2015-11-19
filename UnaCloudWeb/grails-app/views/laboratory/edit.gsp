<html>
   <head>
      <meta name="layout" content="main"/> 
   </head>
<body>
	<section class="content-header">
        <h1>
            Edit Laboratory
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-dashboard"></i> Home</a></li>
            <li><a href="${createLink(uri: '/admin/lab/list', absolute: true)}"><i class="fa fa-flask"></i> Laboratories</a></li>
            <li><a href="${createLink(uri: '/admin/lab/'+lab.id, absolute: true)}"><i class="fa fa-sitemap"></i> ${lab.name}</a></li>
            <li class="active">Edit Laboratory</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">
    		<div class="col-lg-6 col-sm-6 col-xs-12">      			
    			<div id="label-message"></div> 
    			<g:render template="/share/message"/>         
    			<form method="post" action="${createLink(uri: '/admin/lab/edit/save', absolute: true)}" role="form">	                        		     
		       		<div class="box box-primary"> 
		       		   
		       			<div class="box-body"> 		           	 	 
                        	 <div class="form-group">
                             	<label>Laboratory Name</label>
                             	<input value="${lab.id}" type="hidden" name="lab">
                            	<input value="${lab.name}" type="text" class="form-control" name="name" placeholder="Lab Name">
                         	 </div>
                        	 <div class="form-group">
	                             <label>Select</label>
	                             <select name= "net" class="form-control">
	                             	<g:each in="${netConfigurations}" status="i" var="net">	                             	
					  					<option value="${net}"<g:if test="${net == lab.networkQuality.getName()}">selected</g:if>>${net}</option>
					  				</g:each>
	                             </select>
	                         </div>	  
	                         <div class="form-group">
	                             <label>
	                             	<input type="checkbox" name="isHigh"  class="check-blue" <g:if test="${lab.highAvailability}">checked</g:if>/> High Availability
	                             </label>
	                             <p class="help-block">If your Physical Machines have a high availability.</p>
	                         </div>  	                           
		                 </div><!-- /.box-body -->	
		                 <div class="box-footer"> 
		           			<g:submitButton name="button-submit" class="btn btn-success" value="Submit" />		
		           			<a class="btn btn-danger" href="${createLink(uri: '/admin/lab/'+lab.id, absolute: true)}" >Cancel</a>	                                  
		                </div>		                         	
		       	 	</div>
	       	 	</form>
       	 	</div>       	 			
    	</div>   	
	</section>
	<script type="text/javascript">mask();</script>
</body>