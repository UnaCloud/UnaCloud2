$(document).on('ready',function(){
	$("#unacloudTable").dataTable();
	$("#unacloudTable2").dataTable();
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

	$(".delete_repo").click(function (event){
		event.preventDefault();
		redirectConfirm($(this).data("id"),  $(this).attr("href"), 'Repository')	
	});


	$(".delete_hypervisor").click(function (event){
		event.preventDefault();
		redirectConfirm($(this).data("id"),  $(this).attr("href"), 'Hypervisor')	
	});

	$(".delete_os").click(function (event){
		event.preventDefault();
		redirectConfirm($(this).data("id"),  $(this).attr("href"), 'Operating System')	
	});
	
	$("#delete-lab").click(function (event){
		event.preventDefault();
		redirectConfirm($(this).data("id"),  $(this).attr("href"), 'Laboratory')	
	});
	
	$(".delete_ip").click(function (event){
		event.preventDefault();
		redirectConfirm($(this).data("id"),  $(this).attr("href"), 'IP')	
	});
	
	$(".delete_pool").click(function (event){
		event.preventDefault();
		redirectConfirm($(this).data("id"),  $(this).attr("href"), 'IP Pool')	
	});
	
	$(".delete_machines").click(function (event){
		event.preventDefault();
		redirectConfirm($(this).data("id"),  $(this).attr("href"), 'Host')	
	});
	
	$(".stop-agents").click(function (event){		
		event.preventDefault();
		var href = $(this).attr("href");
		var form = $('#form_machines');
		submitConfirm(form, href, 'All selected host machines will be stopped. Do you want to continue?');
	});
	
	$(".cache-agents").click(function (event){		
		event.preventDefault();
		var href = $(this).attr("href");
		var form = $('#form_machines');
		submitConfirm(form, href, 'All selected host machines will erased their cache. Do you want to continue?');
	});
	
	$(".update-agents").click(function (event){	
		event.preventDefault();
		var href = $(this).attr("href");
		var form = $('#form_machines');
		submitConfirm(form, href, 'All selected host machines will update their agents, some processes in agents will be stopped. Do you want to continue?');
	});
	
	
	$('.clear_image').click(function (event){	
		event.preventDefault();
		var data = $(this).data("id");
		var href = $(this).attr("href");
		sendConfirm('This image will be removed from all currently connected physical machines. Are you sure you want to remove it?',href,data)
	});
	
	$('#disable-lab').click(function (event){
		event.preventDefault();
		var data = $(this).data("id");
		var href = $(this).attr("href");
		var state = $(this).data("state");
		var text = "";
		if(state) text = "enabled to disabled"
		else text = "disabled to enabled"
	    sendConfirm('This laboratory will change its status from <strong>'+text+'</strong>. Are you sure you want to change it?',href,data);
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
	
	$('.btn-variable').click(function(event){
		var parent = $(this).parent();
		parent.find('input.btn-submit').removeClass('hide-segment');
		parent.find('input.btn-cancel').removeClass('hide-segment');
		parent.find('input.btn-variable').addClass('hide-segment');
		$('.'+parent.attr('id')).prop('disabled', false);
	});
	
	$('.btn-cancel').click(function(event){
		var parent = $(this).parent();
		parent.find('input.btn-submit').addClass('hide-segment');
		parent.find('input.btn-cancel').addClass('hide-segment');
		parent.find('input.btn-variable').removeClass('hide-segment');
		$('.'+parent.attr('id')).prop('disabled', true);
	});	
	
	tableChecker();
});

function tableChecker(){	
	var checks = 0;	
	$('#selectAll').click(function (event) {		
        var selected = this.checked;
        if(selected)$('#btn-group-agent').removeClass("hide-segment");
        else{ 
        	$('#btn-group-agent').addClass("hide-segment");
        	checks = 0;
        }
        $('.all:checkbox').each(function () {         	
        	if(!this.checked&&selected)checks++;
        	this.checked = selected; 
        });
	});	
	$('.all:checkbox').click(function(event){
		var selected = this.checked;
		if(selected)checks++;
        else checks--;
		if(checks>=1)$('#btn-group-agent').removeClass("hide-segment");
		else $('#btn-group-agent').addClass("hide-segment");
	});
}

function checkSelected(){
	cleanLabel('#label-message');
	var selected = false;
	$('.all:checkbox').each(function () {  
		if(this.checked){
			selected = true;
			return;
		}
	});
	if(!selected){
		addLabel('#label-message','At least one physical machine should be selected.',true);		
	}
	return selected;
}

function submitConfirm(form, href, message){
	if(checkSelected()){	
		showConfirm('Confirm',message, function(){	
			form.attr('action',href);
			form.submit()
		});			 		
	}
}

function redirectConfirm(data, href, name){
	sendConfirm('This <strong>'+name+'</strong> will be deleted. Are you sure you want to confirm it?',href,data)
}
function sendConfirm(message,href,data){
	showConfirm('Confirm',message, function(){		
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
function calculateDeploy(){
	$('#option-hw').change(function() {
		var label = $(this).attr('data-img');
		var hw = $(this).val();
		 $.get('../maxDeploys', {hwp:hw}, function(data){
			 $('#'+label).text(data.max);
		 }, 'json');
	});
}

function mask(){
	$("[data-mask]").inputmask();
	$("[data-mask-mac]").inputmask("[AA]:[AA]:[AA]:[AA]:[AA]:[AA]");
}
