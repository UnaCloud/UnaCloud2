<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            New Group
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-dashboard"></i> Home</a></li>
            <li><a href="${createLink(uri: '/admin/group/list', absolute: true)}"><i class="fa fa-users"></i> Groups</a></li>
            <li class="active">New Group</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">
    		<div class="col-xs-12">      			
    			<div id="label-message"></div> 
    			<g:render template="/share/message"/>         
    			<form id="form-create" name="newGroup" action="${createLink(uri: '/admin/group/save', absolute: true)}" role="form">
	                        		     
		       		<div class="box box-primary"> 
		           	 	 <div class="box-body"> 
		           	 	 	<div class="form-group">
		                       <label>Group Name</label>
		                       <input type="text" class="form-control" name="name" placeholder="Group Name">
		                    </div>  
		                   	 <div class="form-group">
		                         <div class="table-responsive">
	                         	 	<div class="form-group">
	                         	 		<label class="control-label">Select Users</label>
	                         	 		<p class="help-block">* Username - Name</p>
                                        <select name= "users" class="form-control" multiple>
                                        <g:each in="${users}" var="user"> 
                                            <option  value="${user.id}">${user.username} - ${user.name}</option>                                                
                                        </g:each>    
                                        </select>                   
                                 	</div>
                                 </div>
		           			 </div>                       
		                 </div><!-- /.box-body -->	
		                 <div class="box-footer"> 
		           			<g:submitButton name="button-submit" class="btn btn-primary" value="Submit" />		
		                </div>		                         	
		       	 	</div>
	       	 	</form>
    		</div>			
    	</div>   	
	</section>
</body>