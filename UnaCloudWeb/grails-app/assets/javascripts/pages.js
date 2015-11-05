$('.delete_user').click(function (event){	
	event.preventDefault();
	redirectConfirm($(this).data("id"),  $(this).attr("href"), 'User')	
});

$('.delete_group').click(function (event){	
	event.preventDefault();
	redirectConfirm($(this).data("id"),  $(this).attr("href"), 'Group')	
});

$('.delete_cluster').click(function (event){	
	event.preventDefault();
	redirectConfirm($(this).data("id"),  $(this).attr("href"), 'Cluster')	
});	

$(".delete_images").click(function (event){
	event.preventDefault();
	redirectConfirm($(this).data("id"),  $(this).attr("href"), 'Image')	
});

$(".delete_hypervisor").click(function (event){
	event.preventDefault();
	redirectConfirm($(this).data("id"),  $(this).attr("href"), 'Hypervisor')	
});

$('.clear_image').click(function (event){	
	event.preventDefault();
	var data = $(this).data("id");
	var href = $(this).attr("href");
	showConfirm('Confirm','This image will be removed from all currently connected physical machines. Are you sure you want to remove it?', function(){
		window.location.href = href+data;
	});
});

$('#button-upload').click(function (event){		
	cleanLabel('#label-message');
	var form = document.getElementById("form-new");
	if(form["name"].value&&form["name"].value.length > 0&&
		form["user"].value&&form["user"].value.length > 0&&
			form["passwd"].value&&form["passwd"].value.length > 0&&
				form["protocol"].value&&form["protocol"].value.length > 0 ){			
		if(form["files"].value&&form["files"].value.length > 0){
		  uploadForm(form);		
		}
		else addLabel('#label-message', 'File(s) to upload is/are missing.', true);
	}else addLabel('#label-message', 'All fields are required', true);
});

$('#button-update').click(function (event){		
	cleanLabel('#label-message');
	var form = document.getElementById("form-change");		
	if(form["files"].value&&form["files"].value.length > 0){
		uploadForm(form);		
	}
	else addLabel('#label-message', 'File(s) to upload is/are missing.', true);		
});

function redirectConfirm(data, href, name){
	showConfirm('Confirm','This '+name+' will be deleted. Are you sure you want to delete it?', function(){		
		window.location.href = href+data;
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
	var pub = $("#check_public").is(':checked')
	$('#button-submit').click(function (event){	
		event.preventDefault();
		cleanLabel('#label-message');
		var form = document.getElementById("form-edit");
		if(form["name"].value&&form["name"].value.length > 0&&
			form["user"].value&&form["user"].value.length > 0){				
			var call = function(){
				form.submit()
			}
			if(form["isPublic"].checked!=pub){
				if(pub)	showConfirm('Confirm','Your image will change its privacy policy from public to private and others users won\'t be allowed to copy it. Are you sure you want to change it?', call);	
				else showConfirm('Confirm','Your image will change its privacy policy from private to public and others users will be allowed to copy it. Are you sure you want to change it?', call);	
			}else call();	
		}else addLabel('#label-message', 'Name and user are required', true);
	});
}
