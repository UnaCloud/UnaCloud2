<html>
   <head>
      <meta name="layout" content="main"/> 
   </head>
<body>
	<section class="content-header">
        <h1>
            New IP Pool in ${lab.name}
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li><a href="${createLink(uri: '/admin/lab/list', absolute: true)}"><i class="fa fa-sitemap"></i> All laboratories</a></li>
            <li><a href="${createLink(uri: '/admin/lab/'+lab.id, absolute: true)}"><i class="fa fa-flask"></i> ${lab.name}</a></li>
            <li class="active">New IP Pool</li>
        </ol> 
    </section>    
    <section class="content"> 
    	<div class="row">
    		<div class="col-xs-12">      			
    			<div id="label-message"></div> 
    			<g:render template="/share/message"/>         
    			<form method="post" action="${createLink(uri: '/admin/lab/'+lab.id+'/pool/save', absolute: true)}" role="form">	                        		     
		       		<div class="box box-primary"> 
		       		    <div class="box-header">
                           <h3 class="box-title">Create a new IP Pool</h3>
                        </div><!-- /.box-header -->
		       			<div class="box-body"> 	
	                         <div class="form-group">  
                         		<h4>IP Pool</h4>
                         		<p class="help-block">This IP range will be given to executions.</p>
                         		 <input value="${lab.id}" name="id" type="hidden">                        
		                         <div class="form-group">
		                             <label>
		                             	<input type="checkbox" name="isPrivate"  class="check-blue"/> Private network
		                             </label>
		                             <p class="help-block">If your network is private or public.</p>
		                         </div>  
                         		<div class="row">
                     		  		 <div class="col-lg-3 col-sm-6 col-xs-12">
                                    	<label>Start Range</label>
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
		           			<a class="btn btn-danger" href="${createLink(uri: '/admin/lab/'+lab.id, absolute: true)}" >Cancel</a>	                                  
		                </div>		                         	
		       	 	</div>
	       	 	</form>
    		</div>			
    	</div>   	
	</section>
	<script type="text/javascript">mask();</script>
</body>