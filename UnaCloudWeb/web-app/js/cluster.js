function createCluster(){
	$('#button-create').click(function (event){		
		cleanLabel('#label-message');
		var form = document.getElementById("form-new");
		if(form["name"].value&&form["name"].value.length > 0){	
			if(form["images"].value&&form["images"].value> 0){	
				form.submit();			 
			}else addLabel('#label-message', 'At least one image must be selected', true);	
		}else addLabel('#label-message', 'The name for your new cluster is required', true);
	});
}
function loadCluster(){
	$('.deleteCluster').click(function (event){	
		var data = $(this).data("id");
		showConfirm('Confirm','This Cluster will be deleted. Are you sure you want to delete it?', function(){		
			 $.get('delete', {id:data}, function(data){
				if(data.success)window.location.href = data.redirect;
				else if(data.message) showError('Error!',data.message); 
				else showError('Error!','Delete process failed, check server logs for more information'); 
			 }, 'json')		
		});
	});	
}
function calculateDeploy(){
	$('#option-hw').change(function() {
		var label = $(this).attr('data-img');
		var hw = $(this).val();
		 $.get('../maxDeploys', {hwp:hw}, function(data){
			 $('#'+label).text(data.max);
		 }, 'json');
	});
}