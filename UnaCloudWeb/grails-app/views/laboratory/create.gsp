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
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li><a href="${createLink(uri: '/admin/lab/list', absolute: true)}"><i class="fa fa-sitemap"></i> Laboratories</a></li>
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
		       		    <div class="box-header">
                           <h3 class="box-title">Configure your own lab</h3>
                        </div><!-- /.box-header -->
		       			<div class="box-body"> 		           	 	 
                        	 <div class="form-group col-sm-6 col-xs-12">
                             	<label>Laboratory Name</label>
                            	<input type="text" class="form-control" name="name" placeholder="Lab Name">
                         	 </div>
                        	 <div class="form-group col-sm-6 col-xs-12">
	                             <label>Network</label>
	                             <select name= "net" class="form-control">
	                             	<option value="">-- Select an option --</option>
	                             	<g:each in="${netConfigurations}" status="i" var="net">
					  					<option value="${net}">${net}</option>
					  				</g:each>
	                             </select>
	                         </div>	 
	                         <div class="form-group">
	                             <label>
	                             	<input type="checkbox" name="isHigh"  class="check-blue"/> High Availability
	                             </label>
	                             <p class="help-block">If your Physical Machines have a high availability.</p>
	                         </div>  
	                         <hr>
	                         <div class="form-group">  
                         		<h4>IP Pool</h4>
                         		<p class="help-block">This IP range will be given to virtual machines (you can add others IP Pools in edit lab section).</p>
                         		                         
		                         <div class="form-group">
		                             <label>
		                             	<input type="checkbox" name="isPrivate"  class="check-blue"/> Private network
		                             </label>
		                             <p class="help-block">If your network is private or public.</p>
		                         </div>  
                         		<div class="row">
                     		  		 <div class="col-lg-3 col-sm-6 col-xs-12">
                                    	<label>Init Range</label>
                                        <div class="input-group">
                                         <div class="input-group-addon">
                                             <i class="fa fa-laptop"></i>
                                         </div>
                                         <input type="text" name="ipInit" class="form-control" data-inputmask="'alias': 'ip'" data-mask/>
                                    	</div>
                                    </div>
                                    <div class="col-lg-3 col-sm-6 col-xs-12">
                                    	<label>End Range</label>
                                        <div class="input-group">
                                         <div class="input-group-addon">
                                             <i class="fa fa-laptop"></i>
                                         </div>
                                         <input type="text" name="ipEnd" class="form-control" data-inputmask="'alias': 'ip'" data-mask/>
                                    	</div>
                                    </div>
                                    <div class="col-lg-3 col-sm-6 col-xs-12">
                                    	<label>Network Gateway</label>
                                        <div class="input-group">
                                         <div class="input-group-addon">
                                             <i class="fa fa-laptop"></i>
                                         </div>
                                         <input type="text" name="netGateway" class="form-control" data-inputmask="'alias': 'ip'" data-mask/>
                                    	</div>
                                    </div>
                                    <div class="col-lg-3 col-sm-6 col-xs-12">
                                    	<label>Network Mask</label>
                                        <div class="input-group">
                                         <div class="input-group-addon">
                                             <i class="fa fa-laptop"></i>
                                         </div>
                                         <input type="text" name="netMask" class="form-control" data-inputmask="'alias': 'ip'" data-mask/>
                                    	</div>
                                    </div>                                       
                                </div>
                             </div>          
		                 </div><!-- /.box-body -->	
		                 <div class="box-footer"> 
		           			<g:submitButton name="button-submit" class="btn btn-success" value="Submit" />		
		           			<a class="btn btn-danger" href="${createLink(uri: '/admin/lab/list/', absolute: true)}" >Cancel</a>	                                  
		                </div>		                         	
		       	 	</div>
	       	 	</form>
    		</div>			
    	</div>   	
	</section>
	<script type="text/javascript">mask();</script>
</body>