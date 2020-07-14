<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>customTest</title>
</head>
<body>
		<div class = 'uploadDiv'>
			<input id = "uploadFile" type="file" name="uploadFile" multiple = "multiple">
			<!--  XHTML에선 multiple 속성 요소 전부 선언해 주지 않으면 인식 불가능 -->
		</div>
	
	<div class = "uploadResult">
		<ul id = "uploadList">
		</ul>
	</div>
	<button id="uploadBtn">upload</button>
	
	<h2> file List</h2>
	<div class = "downloadFileDiv">
		<ul id = "downloadList">
		</ul>
	</div>
</body>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js" ></script>
<script>
var cloneObj = $(".uploadDiv").clone();
var regex = new RegExp("(.*?)\.(exe|sh|alz)$"); // refuse file extansion
var maxSize = 10485760; //10 MB
var tempfileletgth = 0; // init
var tempList = "";
//stringbuffer
var StringBuilder = function(){
	this.buffer = new Array();
}
// Append string to asc
StringBuilder.prototype.Append = function(strValue){
	this.buffer[this.buffer.length] = strValue;
}
//return String
StringBuilder.prototype.ToString = function(){
	return this.buffer.join("");
}
// file Check
var checkExtension = function f_checkExtension(fileName, fileSize) {
	for (var i = 0; i < tempList.length ; i++){
		if (tempList[i] != null){
			if (tempList[i].fileName == fileName ){
				alert("file is already uploaded");
				$(".uploadDiv").html(cloneObj.html());
				return false;
			}
		}
	}
	if (fileSize >= maxSize) {
		alert("max file size is 10 MB");
		$(".uploadDiv").html(cloneObj.html());
		return false;
	}
	if (regex.test(fileName)) {
		alert("you don't upload this file extansion");
		$(".uploadDiv").html(cloneObj.html());
		return false;
	}
	return true;
}
var changeFile = function f_changeFile(){
	var inputFile = $("input[name='uploadFile']"); 
	var files = inputFile[0].files;
	var tempFormData = new FormData();
	
	console.log(files);
	for (var i = 0; i < files.length; i++) {
		if (!checkExtension(files[i].name, files[i].size)) {
			$(".uploadDiv").html(cloneObj.html());
			eventbind();
			return false;
		}
		tempFormData.append("uploadFile", files[i]);
	}
	$.ajax({
		 url: '/dwpF/testController/tempUpload',
		 processData: false,
		 contentType: false,
		 data: tempFormData,
		 type: 'POST',
		 dataType: 'json',
		 success: function(result){
			console.log("tempUpload success")
			tempList = result;
			console.log(tempList);
			var sb = new StringBuilder();
			for (var i = tempfileletgth; i < result.length; i++) {
				sb.Append("<li id='uploadFile'>"+result[i].fileName+"&nbsp;&nbsp;<button class = 'deleteFile' data-path='" + i + "'>-</button></li>");
				tempfileletgth++;
			}
			 $('#uploadList').append(sb.ToString());
			 clickbind();
		 }
	}); //$.ajax
};
var tempFileDelete = function f_tempFileDelete(clickObj, parentLi){
	var tempData = { "removeFile": clickObj};
	var liObj = parentLi;
	$.ajax({
		 url: '/dwpF/testController/tempDelete',
		 type: 'POST',
		 dataType: 'json',
		 data: tempData,
		 success: function(result){
			 console.log("success");
			 tempList = result;
			 console.log(tempList);
			 liObj.remove();
		 }
	 }); //$.ajax  
};

var fileUpload = function f_fileUpload(){
	$.ajax({
		 url: '/dwpF/testController/uploadAjaxAction',
		 dataType: 'json',
		 type: 'POST',
		 success: function(result){
			 console.log("test/uploadAjaxAction success")
			 console.log("upload Count :: " + result);
			 $(".uploadDiv").html(cloneObj.html());
			 $('#uploadList').empty(); 
			 tempfileletgth = 0;
			 tempList = "";
			 fileList();
		 }
	 }); //$.ajax  
};

var fileList = function f_fileList(){
	$.ajax({
		 url: '/dwpF/testController/fileList',
		 dataType: 'json',
		 cache : false,
		 success: function(result){
			 console.log("/testController/fileList")
			 console.log(result);
			 var sb = new StringBuilder();
			 for (i = 0 ; i < result.length ; i++){
				 var path = "";
				 path += result[i].uploadPath+ "\\" + result[i].uuid + "_" + result[i].fileName;
				 sb.Append("<li id='uploadFile' data-path='" + path + "'>"+result[i].fileName+"&nbsp;&nbsp;<button class = 'download'>down</button>&nbsp;&nbsp;<button class = 'delete'>delete</button></li>");
			 }
			 $('#downloadList').html(sb.ToString()); 
			 clickbind();
		 }
	}); //$.ajax  
};
var downloadFile = function f_downloadFile(downloadFilePath){
	$.ajax({
		 url: '/dwpF/testController/downloadAction?fileName='+downloadFilePath,
		 type: 'GET',
		 success: function(result){
			 console.log("path & folder exist");
			 self.location = '/test/downloadAction?fileName='+downloadFilePath;
		 }
	}); //$.ajax  
};
var deleteFile = function f_deleteFile(StringPath){
	var postData = { "path": StringPath};
	$.ajax({
	 url: '/dwpF/testController/deleteFileAction',
	 type: 'POST',
	 data: postData,
	 dataType: 'json',
	 success: function(result){
		 if(result ==1){
			 console.log("delete success");
			 fileList();
		 }
	 }
	 }); //$.ajax
};
var clickbind = function f_clickbind(){
	// temporary file delete
	$('.deleteFile').unbind('click').click(function(event){
		var clickObj =  $(this).data("path");
		var parentLi = $(this).parent();
		tempFileDelete(clickObj, parentLi);
	});
	$('#uploadBtn').unbind('click').click(function(event) {
		console.log("click upload button");
		fileUpload();
	});
	//download 
	$('.download').unbind('click').click(function(event){
		console.log("click download button");
		var pathLi = $(this).parent();
		var downloadFilePath = pathLi.data("path");
		downloadFilePath = encodeURIComponent(downloadFilePath);
		downloadFile(downloadFilePath);
	});
	//delete file
	$('.delete').unbind('click').click(function(event){
		console.log("click delete button");
		var deleteFileLi = $(this).parent();
		var StringPath = deleteFileLi.data("path");
		deleteFile(StringPath);
	});
};
var eventbind = function f_eventbind(){
	$("#uploadFile").change(function(e){
		console.log("eventbind - change file");
		changeFile();
	});
};
$(document).ready(function(){
	//fileList();
	//eventbind();
})
</script>
</html>