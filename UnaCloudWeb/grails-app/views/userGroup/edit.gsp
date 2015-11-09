<html>
   <head>
      <meta name="layout" content="main"/>
   </head>
<body>
	<section class="content-header">
        <h1>
            Edit Group
        </h1>
        <ol class="breadcrumb">
            <li><a href="${createLink(uri: '/', absolute: true)}"><i class="fa fa-dashboard"></i> Home</a></li>
            <li><a href="${createLink(uri: '/admin/group/list', absolute: true)}"><i class="fa fa-users"></i> Groups</a></li>
            <li class="active">Edit Group</li>
        </ol>
    </section>    
    <section class="content"> 
    	<div class="row">
    		<div class="col-xs-12">      			
    			<div id="label-message"></div> 
    			<g:render template="/share/message"/>         
    			<form id="form-create" name="newGroup" action="${createLink(uri: '/admin/group/edit/save', absolute: true)}" role="form">
	                        		     
		       		<div class="box box-primary"> 
		           	 	 <div class="box-body"> 
		           	 	 	<div class="form-group">
		                       <label>Group Name</label>
		                       <input type="text" value="${group.visualName}" class="form-control" name="name" placeholder="Group Name">
		                       <input name="id" type="hidden" value="${group.id}">
		                    </div>  
		                   	 <div class="form-group">
		                         <div class="table-responsive">
	                         	 	<div class="form-group">
	                         	 		<label class="control-label">Select Users</label>
	                         	 		<p class="help-block">* Username - Name</p>	                         	 		
                                        <select name= "users"  multiple class="form-control">
                                        <g:each in="${users}" var="user"> 
                                            <option  value="${user.id}"<g:if test="${group.users.contains(user)}"> selected</g:if>>${user.username} - ${user.name}</option>                                                
                                        </g:each>    
                                        </select>                   
                                 	</div>
                                 </div>
		           			 </div>                       
		                 </div><!-- /.box-body -->	
		                 <div class="box-footer"> 
		           			<g:submitButton name="button-submit" class="btn btn-success" value="Submit" />		
		           			<a class="btn btn-danger" href="${createLink(uri: '/admin/group/list', absolute: true)}" >Cancel</a>			
		                </div>		                         	
		       	 	</div>
	       	 	</form>
    		</div>			
    	</div>   	
	</section>
</body>