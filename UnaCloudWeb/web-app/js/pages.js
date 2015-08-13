var page={
		deploy:function(){			
			$(".dialog_button").on("click",function(e){
				var imageId = $(this).data("imageid");
				var vmachineId = $(this).data("vmachineid");
				var imagename = $(this).data("imagename");
				bootbox.dialog({
					title: "Save Image",
					message: "<form>"+
								    "<div class='hero-unit' style='text-align: center;'>"+
									"<h4>Write the name that will be used to save the copy</h4>"+
									"<input id='imageName' name='name' type='text' value='"+imagename+"'>"+						
								"</div>"+						
							"</form>",
					buttons: {
						success: {
							label: "Save",
							className: "btn-success",
							callback: function () {
								var name = $('#imageName').val();
								sendToSave(name, imageId, vmachineId);
							}
						}
					}
				});
			});	
			
			function sendToSave(name, imageId, vmachineId){
				$.post('validate', {name: name, image:imageId, machine:vmachineId}, function(data){	
					var message="";
					if(data.replace == true){
						message="<p class='alert alert-error'>There is another image with the same name. If you save this copy, files will by rewriten.</p>"
					}
					bootbox.dialog({
						message: message+"The image will be copied to server. The execution and files associated will be stopped and deleted from client.",
						title: "Confirm save image",
						buttons: {			    	    
							cancel: {
								label: "Cancel",
								className: "btn-default",								
							},
							save: {
								label: "Save",
								className: "btn-primary",
								callback: function() {
									window.location="save?image="+data.imageId+"&machine="+data.machineId+"&name="+data.imageName;
								}
							}
						}
					});			    
				}, 'json').fail(function(response) {
				    if(response.status=='404'){
				    	window.location="../index.html";
				    } 
				    if(response.status=='505'){
				    	 bootbox.alert("Error: the image that you want to copy is not available in this moment.");
				    } 
				});
			}
		}

}