function loadCluster(){
	$('.delete_cluster').click(function (event){	
		event.preventDefault();
		var data = $(this).data("id");
		var href = $(this).attr("href");
		showConfirm('Confirm','This Cluster will be deleted. Are you sure you want to delete it?', function(){		
			window.location.href = href+data;
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