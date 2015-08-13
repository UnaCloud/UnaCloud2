function newUploadImage(){
	$('#button-submit').click(function (event){		
		cleanLabel('#label-message');
		var form = document.getElementById("form-new");
		if(form["name"].value&&form["name"].value.length > 0&&
			form["user"].value&&form["user"].value.length > 0&&
				form["password"].value&&form["password"].value.length > 0&&
					form["accessProtocol"].value&&form["accessProtocol"].value.length > 0 ){			
			if(form["files"].value&&form["files"].value.length > 0){
			  uploadForm(form);		
			}
			else addLabel('#label-message', 'File(s) to upload is/are missing.', true);
		}else addLabel('#label-message', 'All fields are required', true);
	});
}
function changeUploadImage(){
	$('#button-submit').click(function (event){		
		cleanLabel('#label-message');
		var form = document.getElementById("form-change");		
		if(form["files"].value&&form["files"].value.length > 0){
			 uploadForm(form);		
		}
		else addLabel('#label-message', 'File(s) to upload is/are missing.', true);		
	});
}
function uploadForm(form){	
	var formData = new FormData(form);
	var xhr = new XMLHttpRequest();
	xhr.upload.onprogress = function(e) {
		updateUploading(e);
    };
    xhr.onload = function() {
    	hideLoading();   	
        if (xhr.status == 200) {   
        	var jsonResponse = JSON.parse(xhr.responseText);
        	console.log(jsonResponse);
        	if(jsonResponse.success){
        		if((jsonResponse.cPublic&&jsonResponse.cPublic==true)||jsonResponse.cPublic==undefined){
        			window.location.href = jsonResponse.redirect;
        		}else{
        			showClose('Failed saving as a public image!','There is another public image with the same name. Your image was saved as a private image', function(){
		        		window.location.href = jsonResponse.redirect;
   				    });
        		}
        	}
        	else addLabel('#label-message', jsonResponse.message, true);        	
        }else showError('Error!','Upload failed: '+xhr.response);       
    };
    xhr.onerror = function() {
    	hideLoading();
    	showError('Error!','Upload failed. Can not connect to server.')
    };
    showLoadingUploading();
	xhr.open("POST", form.action)	
    xhr.send(formData);
}
function editImage(){
	var pub = document.getElementById("form-edit")["isPublic"].checked;
	$('#button-submit').click(function (event){		
		cleanLabel('#label-message');
		var form = document.getElementById("form-edit");
		if(form["name"].value&&form["name"].value.length > 0&&
			form["user"].value&&form["user"].value.length > 0&&
				form["password"].value&&form["password"].value.length > 0){	
			var call = function(){
				showLoading();
				$.post(form.action, $('#form-edit').serialize(), function(data){
					hideLoading();
					if(data.success){
						if((data.toPublic&&data.toPublic==true)||data.toPublic==undefined){
							 showClose('Success!','The image attributes have been updated', function(){
								 window.location.href = data.redirect;
							 });
			        	}else{
			        		showClose('Failed saving as a public image!','There is another public image with the same name. Your image was saved as a private image', function(){
			        			window.location.href = data.redirect;
			        		});
			        	}						
					}else if(data.message)addLabel('#label-message', data.message, true);
					else showError('Error!','Create process failed, check server logs for more information'); 
				}, 'json');
			}
			if(form["isPublic"].checked!=pub){
				if(pub)	showConfirm('Confirm','Your image will change its privacy from public to private and others users won\'t be allowed to copy it. Are you sure you want to change it?', call);	
				else showConfirm('Confirm','Your image will change its privacy from private to public and others users will be allowed to copy it. Are you sure you want to change it?', call);	
			}else call();	
		}else addLabel('#label-message', 'All fields are required', true);
	});
}
function externalImage(extId){
	$('#button-submit').click(function (event){		
		cleanLabel('#label-message');
		var form = document.getElementById("form-external");		
		var ext = extId
		var call =  function(data){
			 if(data.success){
				 showClose('Success!','The external id from this image has been updated', function(){
					 window.location.href = data.redirect;
				 });
			 }
			 else if(data.message)addLabel('#label-message', data.message, true);
			 else showError('Error!','Update process failed, check server logs for more information'); 
		};
		if(form["externalId"].value&&form["externalId"].value.length >0){	
			$.post(form.action, $('#form-external').serialize(),call,'json');
		}else if(ext){
			showConfirm('Confirm','The external id from this image will be deleted. Are you sure you want to delete it?', function(){
				$.post(form.action, $('#form-external').serialize(),call, 'json');	
			});			
		}else{
			addLabel('#label-message', 'The external id cannot be empty', true);
		}
	});
}

function loadImages(){
	$('.deleteImages').click(function (event){	
		var data = $(this).data("id");
		showConfirm('Confirm','This Image will be deleted from server. Are you sure you want to delete it?', function(){
			showLoading();
			 $.get('delete', {id:data}, function(data){
				hideLoading();
				if(data.success)window.location.href = data.redirect;
				else if(data.message) showError('Error!',data.message); 
				else showError('Error!','Delete process failed, check server logs for more information'); 
			 }, 'json')		
		});
	});
	$('.clearImageFromCache').click(function (event){	
		var data = $(this).data("id");
		showConfirm('Confirm','This image will be removed from all currently connected physical machines. Are you sure you want to remove it?', function(){
			showLoading();
			$.get('clearImageFromCache', {id:data}, function(data){
				hideLoading();
				if(data.success)showDialog("Success!","Image has been removed from all currently connected physical machines");
				else if(data.message) showError('Error!',data.message); 
				else showError('Error!','Delete process failed, check server logs for more information'); 
			 }, 'json')		
		});
	});
}
function createPublicImage(){
	$('#button-submit').click(function (event){		
		cleanLabel('#label-message');
		var form = document.getElementById("form-create");
		if(form["name"].value&&form["name"].value.length > 0){		
			if(form["pImage"].value&&form["pImage"].value> 0){					
				showLoading();
				$.post(form.action, $('#form-create').serialize(), function(data){
					hideLoading();
					if(data.success)window.location.href = data.redirect;
					else if(data.message)addLabel('#label-message', data.message, true);
					else showError('Error!','Create process failed, check server logs for more information'); 
				}, 'json');							 
			}else addLabel('#label-message', 'One public image must be selected', true);
		}else addLabel('#label-message', 'All fields are required', true);
	});
}