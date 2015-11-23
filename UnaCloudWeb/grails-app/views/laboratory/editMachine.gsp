<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            Edit Host in ${lab.name}
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-home"></i> Home</a></li>
            <li><a href="${createLink(uri: '/admin/lab/list', absolute: true)}"><i class="fa fa-sitemap"></i> All laboratories</a></li>
            <li><a href="${createLink(uri: '/admin/lab/'+lab.id, absolute: true)}"><i class="fa fa-flask"></i> ${lab.name}</a></li>
            <li class="active">Edit Host</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">
             <div class="col-lg-6 col-sm-6 col-xs-12">   
             	<g:render template="/share/message"/>                   		     
             		<div class="box box-primary">     	
             			<div class="box-header">
             				<h5 class="box-title">Edit a host</h5> 							    			
             			</div>		
	                 	<!-- form start -->
	                 	<form method="post" id="form-new" action="${createLink(uri: '/admin/lab/'+lab.id+'/edit/'+machine.id+'/save', absolute: true)}" role="form">
	                     	<div class="box-body">	                     		
	                        	 <div class="form-group">
	                             	<label>Host Name</label>
	                            	<input type="text" class="form-control" value="${machine.name}" name="name" placeholder="Physical Machine Name">
	                            	<input type="hidden" name="lab" value="${lab.id}">
	                         	 </div>	                        	
		                         <div class="form-group">
		                             <label>Host IP</label>
	                                 <div class="input-group">
	                                     <div class="input-group-addon">
	                                          <i class="fa fa-laptop"></i>
	                                     </div>
	                                     <input type="text" name="ip" value="${machine.ip.ip}" class="form-control" data-inputmask="'alias': 'ip'" data-mask/>
                                 	 </div>
		                         </div>
		                         <div class="form-group">
		                             <label>Host MAC</label>
	                                 <div class="input-group">
	                                     <div class="input-group-addon">
	                                          <i class="fa fa-laptop"></i>
	                                     </div>
	                                     <input type="text" name="mac" value="${machine.mac}" class="form-control" data-inputmask="'alias': 'ip'" data-mask-mac/>
                                 	 </div>
		                         </div>
		                         <div class="form-group">
		                             <label>CPU Logical Cores</label>
		                             <input type="text" class="form-control" value="${machine.cores}" name="cores" placeholder="Logical Cores in Host">
		                         </div>
		                         <div class="form-group">
		                             <label>CPU Physical Cores</label>
		                             <input type="text" class="form-control" value="${machine.pCores}" name="pCores" placeholder="Physical Cores in Host">
		                         </div>   
		                         <div class="form-group">
		                             <label>RAM Memory (MB)</label>
		                             <input type="text" class="form-control" value="${machine.ram}" name="ram" placeholder="Memory Ram in Host">
		                         </div>                           
		                         <div class="form-group">
		                             <label>Operating System</label>
		                             <select name= "osId" class="form-control">
		                             	<g:each in="${oss}" status="i" var="os">
						  					<option value="${os.id}" <g:if test="${os.id == machine.operatingSystem.id}">selected</g:if>>${os.name}</option>
						  				</g:each>
		                             </select>
		                         </div>
		                     </div><!-- /.box-body -->
		                     <div class="box-footer"> 			
		                         <g:submitButton name="button-submit" class="btn btn-success" value="Submit" />	
		                         <a class="btn btn-danger" href="${createLink(uri: '/admin/lab/'+lab.id, absolute: true)}" >Cancel</a>	      
		                     </div>
	                	 </form>
	             	</div>
             </div><!-- /.box -->            
        </div>     	
	</section><!-- /.content -->  
	<script type="text/javascript">mask();</script>   
</body>